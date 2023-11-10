package syslog.pump;

import java.nio.charset.StandardCharsets;
import java.util.List;

import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kinesis.KinesisClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;

public class KinesisWriter {

  private AwsCredentialsProvider credentialsProvider;
  private Region region;
  private KinesisClient kinesis;

  public KinesisWriter(KinesisClient kinesisClient) {
    this.kinesis = kinesisClient;
  }

  public void addLines(List<String> lines) {

    RecordAggregator agr = new RecordAggregator();

    lines.stream()
      .forEach(line -> {
        try {
          agr.addUserRecord("bob", line.getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e) {
          throw new RuntimeException(e);
        }

      });

    this.flush(agr.clearAndGet());
  }

  private void flush(AggRecord agr) {
    PutRecordResponse res = kinesis.putRecord(b -> b
      .streamName("prov-logs")
      .partitionKey(agr.getPartitionKey())
      .data(SdkBytes.fromByteArray(agr.toRecordBytes())));
    
    
  }

}
