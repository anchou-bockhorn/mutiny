package ch.abraxus;

import static java.time.Duration.ofMillis;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import java.util.List;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class DMergingTest {

  @Inject
  Generator generator;

  @Test
  public void mergeMultis() {
    Multi<String> streamA = generator
        .toMultiDelayed(ofMillis(100), "Stream A", "Stream A", "Stream A", "Stream A", "Stream A", "Stream A");
    Multi<String> streamB = generator
        .toMultiDelayed(ofMillis(100), "Stream B", "Stream B", "Stream B", "Stream B", "Stream B", "Stream B");

    Multi<String> streams = Multi.createBy().merging().streams(streamA, streamB);

    List<String> collectedEvents = streams.collect().asList()
        .await().indefinitely();

    System.out.println(collectedEvents); // expect items from both streams to be mixed together
  }

  @Test
  public void mergeMultisDoesNotEnsureAlternatingConsumption() {
    Multi<String> streamA = Multi.createFrom()
        .items("Stream A", "Stream A", "Stream A", "Stream A", "Stream A", "Stream A");
    Multi<String> streamB = Multi.createFrom()
        .items("Stream B", "Stream B", "Stream B", "Stream B", "Stream B", "Stream B");

    Multi<String> streams = Multi.createBy().merging().streams(streamA, streamB);

    List<String> collectedEvents = streams.collect().asList()
        .await().indefinitely();

    System.out.println(collectedEvents); // do not expect that merging makes sure, both streams are consumed in
    // alternating order. Merging doesn't care at all about order, it only cares about efficiency. That is the reason,
    // that when merging streams, it might result in the same stream as with concatenation.
    // To achieve an alternating order, I think Multi.createBy().combining() might be the solution
  }

  @Test
  public void concatenateMultis() {
    Multi<String> streamA = generator
        .toMultiDelayed(ofMillis(100), "Stream A", "Stream A", "Stream A", "Stream A", "Stream A", "Stream A");
    Multi<String> streamB = generator
        .toMultiDelayed(ofMillis(100), "Stream B", "Stream B", "Stream B", "Stream B", "Stream B", "Stream B");

    Multi<String> streams = Multi.createBy().concatenating().streams(streamA, streamB);

    List<String> collectedEvents = streams.collect().asList()
        .await().indefinitely();

    System.out.println(collectedEvents); // expect a list of Stream A items concatenated with a list of Stream B items
  }

  @Test
  public void concatenateMultiOnMulti() {
    Multi<String> streamA = generator
        .toMultiDelayed(ofMillis(1000), "Stream A", "Stream A", "Stream A", "Stream A", "Stream A", "Stream A");
    Multi<String> streamB = generator
        .toMultiDelayed(ofMillis(1000),
            "Stream B1",
            "Stream B2",
            "Stream B3",
            "Stream B4",
            "Stream B5",
            "Stream B6");

    Multi<String> streams = streamA.onItem().transformToMultiAndConcatenate(e -> {
      System.out.println("process stream a element: " + e);
      return streamB.invoke(f -> System.out.println("Stream B invoke" + f));
    });

    List<String> collectedEvents = streams.collect().asList()
        .await().indefinitely();

    System.out.println(collectedEvents); // expect a list of Stream A items concatenated with a list of Stream B items
  }

  @Test
  public void mergeMultiOnMulti() {
    Multi<String> streamA = generator
        .toMultiDelayed(ofMillis(1000), "Stream A", "Stream A", "Stream A", "Stream A", "Stream A", "Stream A");
    Multi<String> streamB = generator
        .toMultiDelayed(ofMillis(1000),
            "Stream B1",
            "Stream B2",
            "Stream B3",
            "Stream B4",
            "Stream B5",
            "Stream B6");

    Multi<String> streams = streamA.onItem().transformToMultiAndMerge(e -> {
      System.out.println("process stream a element: " + e);
      return streamB.invoke(f -> System.out.println("Stream B invoke" + f));
    });

    List<String> collectedEvents = streams.collect().asList()
        .await().indefinitely();

    System.out.println(collectedEvents); // expect a list of Stream A items concatenated with a list of Stream B items
  }
}