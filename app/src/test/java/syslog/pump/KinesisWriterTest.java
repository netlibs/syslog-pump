package syslog.pump;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.utility.DockerImageName;

import com.google.common.io.Resources;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;

class KinesisWriterTest {
  @ClassRule
  public static LocalStackContainer localstack =
    new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.3.2"))
      .withServices(
        Service.KINESIS);

  @Test
  void test() throws IOException {
    URL resource = Resources.getResource(getClass(), "loggering.txt");

    List<String> lines = Resources.readLines(resource, StandardCharsets.UTF_8);
    
    
    localstack.start();
    
     KinesisClient kinesis = KinesisClient
        .builder()
        .endpointOverride(localstack.getEndpoint())
        .credentialsProvider(
          StaticCredentialsProvider.create(AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey()))
            ).region(Region.of(localstack.getRegion()))
        .build();


     new KinesisWriter(kinesis);
     

    System.out.println(lines);

  }

}
