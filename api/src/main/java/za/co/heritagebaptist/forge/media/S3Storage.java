package za.co.heritagebaptist.forge.media;
import java.io.*;
import java.util.*;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
public class S3Storage implements StorageService {
    private static final int PART_SIZE=8*1024*1024;
    private final S3Client client;
    private final String bucket;
    public S3Storage(S3Client client,String bucket) { this.client=client;this.bucket=bucket; }
    public void put(String key,InputStream input,long size,String contentType) throws IOException {
        if(size<PART_SIZE) { client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType).build(),RequestBody.fromInputStream(input,size));return; }
        String uploadId=client.createMultipartUpload(CreateMultipartUploadRequest.builder().bucket(bucket).key(key).contentType(contentType).build()).uploadId();
        boolean complete=false;
        try {
            List<CompletedPart> parts=new ArrayList<>();long copied=0;int number=1;
            while(true) {
                byte[] bytes=input.readNBytes(PART_SIZE);if(bytes.length==0) break;copied+=bytes.length;
                var response=client.uploadPart(UploadPartRequest.builder().bucket(bucket).key(key).uploadId(uploadId).partNumber(number).contentLength((long)bytes.length).build(),RequestBody.fromBytes(bytes));
                parts.add(CompletedPart.builder().partNumber(number++).eTag(response.eTag()).build());
            }
            if(copied!=size) throw new IOException("Incomplete multipart upload");
            client.completeMultipartUpload(CompleteMultipartUploadRequest.builder().bucket(bucket).key(key).uploadId(uploadId).multipartUpload(CompletedMultipartUpload.builder().parts(parts).build()).build());complete=true;
        } finally { if(!complete) client.abortMultipartUpload(AbortMultipartUploadRequest.builder().bucket(bucket).key(key).uploadId(uploadId).build()); }
    }
    public InputStream get(String key) { return client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build()); }
    public void delete(String key) { client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build()); }
}
