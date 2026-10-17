package za.co.heritagebaptist.forge.media;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
@Configuration
public class StorageConfig {
    @Bean @ConditionalOnProperty(name="app.media-storage",havingValue="local")
    StorageService localStorage(@Value("${app.media-dir}") String directory) throws IOException { return new LocalDiskStorage(Path.of(directory)); }
    @Bean(destroyMethod="close") @ConditionalOnProperty(name="app.media-storage",havingValue="s3")
    S3Client mediaS3(@Value("${app.media-s3-endpoint}") String endpoint,@Value("${app.media-s3-region}") String region,
        @Value("${app.media-s3-access-key}") String access,@Value("${app.media-s3-secret-key}") String secret) {
        var builder=S3Client.builder().region(Region.of(region)).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(access,secret)))
            .forcePathStyle(true).overrideConfiguration(config -> config.apiCallTimeout(Duration.ofSeconds(30)).apiCallAttemptTimeout(Duration.ofSeconds(15)));
        if(!endpoint.isBlank()) builder.endpointOverride(URI.create(endpoint));return builder.build();
    }
    @Bean @ConditionalOnProperty(name="app.media-storage",havingValue="s3")
    StorageService s3Storage(S3Client client,@Value("${app.media-s3-bucket}") String bucket) { return new S3Storage(client,bucket); }
}
