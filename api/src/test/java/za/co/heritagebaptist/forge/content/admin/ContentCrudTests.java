package za.co.heritagebaptist.forge.content.admin;
import com.fasterxml.jackson.databind.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.*;
import za.co.heritagebaptist.forge.content.preacher.*;
import za.co.heritagebaptist.forge.sync.SyncHook;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest(properties="management.health.mail.enabled=false")
@AutoConfigureMockMvc(print=MockMvcPrint.NONE)
@ActiveProfiles({"dev","test"})
@Testcontainers
@org.springframework.test.annotation.DirtiesContext(classMode=org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS)
@WithMockUser(roles="ADMIN")
class ContentCrudTests {
    @Container @ServiceConnection static PostgreSQLContainer<?> postgres=new PostgreSQLContainer<>("postgres:16");
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @Autowired PreacherRepository preachers;
    @Autowired BlogPublicationScheduler scheduler;
    @MockitoBean SyncHook hook;
    UUID preacherId;
    @BeforeEach void fixture() {
        var preacher=new Preacher();preacher.setName("Fixture Preacher");preacher.setSlug("fixture-"+UUID.randomUUID());preacherId=preachers.saveAndFlush(preacher).getId();
    }
    Map<String,Object> body(String type) {
        Map<String,Object> body=new LinkedHashMap<>();
        switch(type) {
            case "sermons" -> { body.put("title","Example Sermon");body.put("preachedOn","2026-10-17");body.put("service","MORNING");body.put("preacherId",preacherId); }
            case "sermon-series" -> body.put("title","Example Series");
            case "preachers" -> body.put("name","Example Preacher");
            case "events" -> { body.put("title","Example Event");body.put("startsAt","2026-10-18T08:00:00Z");body.put("recurrence","NONE"); }
            case "pages" -> { body.put("title","Example Page");body.put("body","Example page body");body.put("section","ABOUT"); }
            case "faqs" -> { body.put("question","Example Question?");body.put("answer","Example answer"); }
            case "people" -> { body.put("name","Example Person");body.put("category","LEADERSHIP"); }
            case "ministries" -> body.put("name","Example Ministry");
            case "growth-groups" -> { body.put("name","Example Group");body.put("dayOfWeek","WEDNESDAY");body.put("time","18:00:00");body.put("active",true); }
            case "blog-posts" -> { body.put("title","Example Post");body.put("body","Example post body");body.put("tags",List.of("news")); }
            case "documents" -> { body.put("title","Example Creed");body.put("category","CREED");body.put("body","Example text"); }
            case "giving-funds" -> { body.put("name","Example Fund");body.put("accountName","DUMMY ACCOUNT");body.put("bank","DUMMY BANK");body.put("branchCode","000000");body.put("accountNumber","0000000000");body.put("accountType","CHEQUE"); }
            case "service-times" -> { body.put("name","Example Service");body.put("dayOfWeek","SUNDAY");body.put("time","09:30:00");body.put("active",true); }
            default -> throw new IllegalArgumentException(type);
        }
        return body;
    }
    JsonNode create(String type,Map<String,Object> body) throws Exception {
        var result=mvc.perform(post("/api/admin/"+type).contentType("application/json").content(json.writeValueAsString(body)))
            .andExpect(status().isCreated()).andExpect(header().exists("Location")).andReturn();
        return json.readTree(result.getResponse().getContentAsString());
    }
    @ParameterizedTest
    @ValueSource(strings={"sermons","sermon-series","preachers","events","pages","faqs","people","ministries","growth-groups","blog-posts","documents","giving-funds","service-times"})
    void everyTypeSupportsAuditedCrud(String type) throws Exception {
        var body=body(type);var created=create(type,body);String path="/api/admin/"+type+"/"+created.get("id").asText();
        mvc.perform(get(path)).andExpect(status().isOk());
        String field=body.containsKey("title")?"title":body.containsKey("name")?"name":"question";
        body.put(field,"Changed example");body.put("version",created.get("version").asLong());
        mvc.perform(put(path).contentType("application/json").content(json.writeValueAsString(body))).andExpect(status().isOk()).andExpect(jsonPath("$."+field).value("Changed example"));
        mvc.perform(delete(path)).andExpect(status().isNoContent());mvc.perform(get(path)).andExpect(status().isNotFound());
        assertThat(jdbc.queryForObject("select count(*) from audit_log where entity_id=?",Integer.class,UUID.fromString(created.get("id").asText()))).isEqualTo(3);
    }
    @Test @WithMockUser(roles="EDITOR") void editorCanManageSermonsAndConflictsAndValidationAreExplicit() throws Exception {
        var body=body("sermons");var created=create("sermons",body);String path="/api/admin/sermons/"+created.get("id").asText();
        body.put("version",created.get("version").asLong());body.put("title","Updated Sermon");
        mvc.perform(put(path).contentType("application/json").content(json.writeValueAsString(body))).andExpect(status().isOk());
        mvc.perform(put(path).contentType("application/json").content(json.writeValueAsString(body))).andExpect(status().isConflict());
        mvc.perform(post(path+"/publish")).andExpect(status().isOk()).andExpect(jsonPath("$.published").value(true));
        mvc.perform(post(path+"/unpublish")).andExpect(status().isOk()).andExpect(jsonPath("$.published").value(false));
        body.put("title","");mvc.perform(post("/api/admin/sermons").contentType("application/json").content(json.writeValueAsString(body))).andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors[0].field").value("title"));
        mvc.perform(get("/api/admin/sermons/"+UUID.randomUUID())).andExpect(status().isNotFound());
    }
    @Test @WithMockUser(roles="EDITOR") void givingWritesRequireAdmin() throws Exception {
        mvc.perform(get("/api/admin/giving-funds")).andExpect(status().isOk());
        mvc.perform(post("/api/admin/giving-funds").contentType("application/json").content(json.writeValueAsString(body("giving-funds")))).andExpect(status().isForbidden());
        mvc.perform(put("/api/admin/giving-funds/"+UUID.randomUUID()).contentType("application/json").content(json.writeValueAsString(body("giving-funds")))).andExpect(status().isForbidden());
        mvc.perform(delete("/api/admin/giving-funds/"+UUID.randomUUID())).andExpect(status().isForbidden());
    }
    @Test void documentsConsentAndReorderingRulesAreEnforced() throws Exception {
        var document=body("documents");document.remove("body");mvc.perform(post("/api/admin/documents").contentType("application/json").content(json.writeValueAsString(document))).andExpect(status().isUnprocessableEntity());
        var person=create("people",body("people"));mvc.perform(post("/api/admin/people/"+person.get("id").asText()+"/publish")).andExpect(status().isUnprocessableEntity());
        var first=create("faqs",body("faqs"));var second=create("faqs",body("faqs"));
        mvc.perform(put("/api/admin/faqs/order").contentType("application/json").content(json.writeValueAsString(List.of(second.get("id").asText(),first.get("id").asText())))).andExpect(status().isNoContent());
        assertThat(jdbc.queryForObject("select sort_order from faq where id=?",Integer.class,UUID.fromString(second.get("id").asText()))).isZero();
    }
    @Test void settingsAreASingletonWithVersionedUpdates() throws Exception {
        var result=mvc.perform(get("/api/admin/settings")).andExpect(status().isOk()).andReturn();
        var settings=json.readTree(result.getResponse().getContentAsString());
        mvc.perform(put("/api/admin/settings").contentType("application/json").content(json.writeValueAsString(Map.of("version",settings.get("version").asLong(),"churchName","Example Church","address","Example Address"))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.churchName").value("Example Church"));
        assertThat(jdbc.queryForObject("select count(*) from site_settings",Integer.class)).isEqualTo(1);
    }
    @Test void scheduledPublicationEmitsExactlyOnceWhenDue() throws Exception {
        var body=body("blog-posts");body.put("published",true);body.put("publishedAt",java.time.Instant.now().plusSeconds(3600).toString());
        var blog=create("blog-posts",body);UUID id=UUID.fromString(blog.get("id").asText());reset(hook);
        jdbc.update("update blog_post set published_at=now()-interval '1 minute' where id=?",id);
        jdbc.update("update scheduled_publication set scheduled_at=now()-interval '1 minute' where blog_id=?",id);
        scheduler.publishDue();scheduler.publishDue();verify(hook,times(1)).onChanged(any());
        assertThat(jdbc.queryForObject("select count(*) from scheduled_publication where blog_id=?",Integer.class,id)).isZero();
    }
    @Test void searchingSortingAndSlugUniquenessWork() throws Exception {
        var first=create("preachers",body("preachers"));var second=create("preachers",body("preachers"));
        assertThat(first.get("slug").asText()).isNotEqualTo(second.get("slug").asText());
        mvc.perform(get("/api/admin/preachers?q=Example&sort=name,asc&page=0&size=1")).andExpect(status().isOk()).andExpect(jsonPath("$.content.length()").value(1));
        mvc.perform(get("/api/admin/preachers?sort=unknown,asc")).andExpect(status().isBadRequest());
    }
}
