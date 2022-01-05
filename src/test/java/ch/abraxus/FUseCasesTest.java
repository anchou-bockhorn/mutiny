package ch.abraxus;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.tuples.Tuple2;
import java.time.Duration;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@ApplicationScoped
class FUseCasesTest {

  @Inject
  Generator generator;

  @Test
  public void aTriggersLatestB() {
    Multi<String> streamA = generator
        .toMultiDelayed(Duration.ofSeconds(500), "", "", "", "", "", "", "", "", "", "", "", "", "", "");
    Multi<String> streamB = generator
        .toMultiDelayed(Duration.ofSeconds(500), "1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

    streamA.subscribe().with(x -> System.out.println(x));

    Multi<String> latest = Multi.createBy().combining()
        .streams(streamA.map(x -> x), streamB)
        .latestItems()
        .asTuple()
        .map(Tuple2::getItem1);
  }
}
