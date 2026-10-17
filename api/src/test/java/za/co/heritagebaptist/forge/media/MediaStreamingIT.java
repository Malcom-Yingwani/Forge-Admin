package za.co.heritagebaptist.forge.media;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.net.http.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.*;
import org.springframework.test.context.*;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import za.co.heritagebaptist.forge.auth.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT,properties="management.health.mail.enabled=false")
@AutoConfigureMockMvc(print=MockMvcPrint.NONE)
@ActiveProfiles({"dev","test"})
@Testcontainers
@WithMockUser(roles="EDITOR")
@org.springframework.test.annotation.DirtiesContext(classMode=org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS)
class MediaStreamingIT {
    @Container @ServiceConnection static PostgreSQLContainer<?> postgres=new PostgreSQLContainer<>("postgres:16");
    @Container static MinIOContainer minio=new MinIOContainer(DockerImageName.parse("forge-minio:RELEASE.2025-09-07T16-13-09Z").asCompatibleSubstituteFor("minio/minio"));
    @DynamicPropertySource static void storage(DynamicPropertyRegistry registry) {
        registry.add("app.media-storage",() -> "s3");registry.add("app.media-s3-endpoint",minio::getS3URL);
        registry.add("app.media-s3-access-key",minio::getUserName);registry.add("app.media-s3-secret-key",minio::getPassword);
        registry.add("app.media-s3-bucket",() -> "media-test");
    }
    @Autowired MockMvc mvc;
    @Autowired S3Client s3;
    @Autowired StorageCleanup cleanup;
    @Autowired ObjectMapper json;
    @Autowired AppUserRepository users;
    @Autowired JwtService jwt;
    @LocalServerPort int port;
    @TempDir Path directory;
    @BeforeEach void bucket() { try { s3.createBucket(CreateBucketRequest.builder().bucket("media-test").build()); } catch(BucketAlreadyOwnedByYouException ignored) {} }
    byte[] png() throws IOException {
        var image=new BufferedImage(1200,600,BufferedImage.TYPE_INT_RGB);var bytes=new ByteArrayOutputStream();ImageIO.write(image,"png",bytes);image.flush();return bytes.toByteArray();
    }
    @Test void detectsActualImageBytesCreatesVariantsListsAndPreviews() throws Exception {
        byte[] image=png();var result=mvc.perform(multipart("/api/admin/media").file(new MockMultipartFile("file","photo.exe","application/octet-stream",image))).andExpect(status().isCreated()).andReturn();
        var body=json.readTree(result.getResponse().getContentAsString());assertThat(body.get("contentType").asText()).isEqualTo("image/png");
        assertThat(body.get("key").asText()).matches("[0-9]{4}/[0-9]{2}/[a-f0-9-]+\\.png");
        for(String size:List.of("320","960")) {
            String key=body.get("variants").get(size).get("key").asText();var head=s3.headObject(HeadObjectRequest.builder().bucket("media-test").key(key).build());
            assertThat(head.contentType()).isEqualTo("image/webp");assertThat(head.contentLength()).isPositive();
            byte[] stored=s3.getObjectAsBytes(GetObjectRequest.builder().bucket("media-test").key(key).build()).asByteArray();
            assertThat(ImageIO.read(new ByteArrayInputStream(stored)).getWidth()).isEqualTo(Integer.parseInt(size));
        }
        mvc.perform(get("/api/admin/media?type=image&q=photo")).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(1));
        mvc.perform(get("/api/admin/media/"+body.get("id").asText()+"/file")).andExpect(status().isOk()).andExpect(content().bytes(image)).andExpect(header().string("X-Content-Type-Options","nosniff"));
    }
    @Test void rejectsDisguisedExecutablesOversizeImagesAndMissingFiles() throws Exception {
        mvc.perform(multipart("/api/admin/media").file(new MockMultipartFile("file","sermon.mp3","audio/mpeg","MZ-fake-executable".getBytes()))).andExpect(status().isUnsupportedMediaType());
        byte[] large=Arrays.copyOf(png(),11*1024*1024);
        mvc.perform(multipart("/api/admin/media").file(new MockMultipartFile("file","large.png","image/png",large))).andExpect(status().isPayloadTooLarge());
        mvc.perform(multipart("/api/admin/media")).andExpect(status().isBadRequest());
    }
    @Test void referencedAssetsCannotBeDeletedAndCleanupIsDurable() throws Exception {
        var result=mvc.perform(multipart("/api/admin/media").file(new MockMultipartFile("file","example.pdf","application/pdf","%PDF-1.4\nExample only\n%%EOF".getBytes()))).andExpect(status().isCreated()).andReturn();
        var asset=json.readTree(result.getResponse().getContentAsString());String id=asset.get("id").asText(),key=asset.get("key").asText();
        var document=mvc.perform(post("/api/admin/documents").contentType("application/json").content(json.writeValueAsString(Map.of("title","Example PDF","category","CREED","pdfId",id))))
            .andExpect(status().isCreated()).andReturn();
        mvc.perform(delete("/api/admin/media/"+id)).andExpect(status().isConflict());
        mvc.perform(delete("/api/admin/documents/"+json.readTree(document.getResponse().getContentAsString()).get("id").asText())).andExpect(status().isNoContent());
        mvc.perform(delete("/api/admin/media/"+id)).andExpect(status().isNoContent());cleanup.run();
        assertThatThrownBy(() -> s3.headObject(HeadObjectRequest.builder().bucket("media-test").key(key).build())).isInstanceOf(S3Exception.class);
    }
    @Test @WithAnonymousUser void anonymousUsersCannotUploadListPreviewOrDelete() throws Exception {
        mvc.perform(get("/api/admin/media")).andExpect(status().isUnauthorized());
        mvc.perform(multipart("/api/admin/media").file(new MockMultipartFile("file","x.png","image/png",png()))).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/admin/media/"+UUID.randomUUID()+"/file")).andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/admin/media/"+UUID.randomUUID())).andExpect(status().isUnauthorized());
    }
    @Test void streamsA100MegabyteMp3OverHttpWithA256MegabyteHeap() throws Exception {
        assertThat(Runtime.getRuntime().maxMemory()).isLessThanOrEqualTo(256L*1024*1024);
        Path mp3=directory.resolve("example.mp3");byte[] frame=new byte[417];frame[0]=(byte)0xff;frame[1]=(byte)0xfb;frame[2]=(byte)0x90;
        try(var out=new BufferedOutputStream(Files.newOutputStream(mp3))) { for(long written=0;written<100L*1024*1024;written+=frame.length) out.write(frame); }
        var user=new AppUser();user.setEmail(UUID.randomUUID()+"@example.test");user.setDisplayName("Streaming Test");user.setRole(Role.EDITOR);user.setEnabled(true);user.setPasswordHash("unused-password-hash");user=users.saveAndFlush(user);
        String boundary="forge-test-"+UUID.randomUUID();
        var body=HttpRequest.BodyPublishers.concat(HttpRequest.BodyPublishers.ofString("--"+boundary+"\r\nContent-Disposition: form-data; name=\"file\"; filename=\"example.mp3\"\r\nContent-Type: audio/mpeg\r\n\r\n"),HttpRequest.BodyPublishers.ofFile(mp3),HttpRequest.BodyPublishers.ofString("\r\n--"+boundary+"--\r\n"));
        var request=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/api/admin/media")).timeout(Duration.ofMinutes(3))
            .header("Authorization","Bearer "+jwt.issue(user)).header("Content-Type","multipart/form-data; boundary="+boundary).POST(body).build();
        try(var client=HttpClient.newHttpClient()) {
            var response=client.send(request,HttpResponse.BodyHandlers.ofString());assertThat(response.statusCode()).as(response.body()).isEqualTo(201);
            var uploaded=json.readTree(response.body());assertThat(uploaded.get("size").asLong()).isEqualTo(Files.size(mp3));assertThat(uploaded.get("durationSeconds").asInt()).isPositive();
            assertThat(s3.headObject(HeadObjectRequest.builder().bucket("media-test").key(uploaded.get("key").asText()).build()).contentLength()).isEqualTo(Files.size(mp3));
        }
    }
}
