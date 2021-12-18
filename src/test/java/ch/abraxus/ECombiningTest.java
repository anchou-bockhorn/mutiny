package ch.abraxus;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.mutiny.tuples.Tuple3;
import java.util.List;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ECombiningTest {

  @Inject
  Generator generator;

  @Test
  public void combineUnis() {
    Uni<String> itemA = generator.toUniDelayed("Item A", ofSeconds(1));
    Uni<String> itemB = generator.toUniDelayed("Item B", ofSeconds(1));

    Tuple2<String, String> tuple2Uni = Uni.combine().all()
        .unis(itemA, itemB)
        .asTuple()
        .await().indefinitely();

    System.out.println(tuple2Uni); // same as Uni.join() but returning a tuple instead of a list. Useful if the result
    // types are different.
  }

  @Test
  public void combineUnisGroupedByIndex() {
    Multi<String> quickMulti = generator
        .toMultiDelayed(ofMillis(100), "quick 1", "quick 2", "quick 3", "quick 4", "quick 5", "quick 6");

    Multi<String> intermediateMulti = generator
        .toMultiDelayed(ofMillis(300), "medium 1", "medium 2", "medium 3", "medium 4", "medium 5", "medium 6");

    Multi<String> slowMulti = generator
        .toMultiDelayed(ofMillis(300), "slow 1", "slow 2", "slow 3", "slow 4", "slow 5", "slow 6");

    List<Tuple3<String, String, String>> combination = Multi.createBy().combining()
        .streams(slowMulti, intermediateMulti, quickMulti)
        .asTuple()
        .collect().asList()
        .await().indefinitely();

    System.out.println(combination); // expect to group the items of the different streams according to the index of
    // occurrence in the stream
  }

  @Test
  public void combineLatestUnis() {
    Multi<String> quickMulti = generator
        .toMultiDelayed(ofMillis(100), "quick 1", "quick 2", "quick 3", "quick 4", "quick 5", "quick 6");

    Multi<String> intermediateMulti = generator
        .toMultiDelayed(ofMillis(140), "medium 1", "medium 2", "medium 3", "medium 4", "medium 5", "medium 6");

    Multi<String> slowMulti = generator
        .toMultiDelayed(ofMillis(250), "slow 1", "slow 2", "slow 3", "slow 4", "slow 5", "slow 6");

    List<Tuple3<String, String, String>> combination = Multi.createBy().combining()
        .streams(slowMulti, intermediateMulti, quickMulti)
        .latestItems() // each of the upstream events, causes a downstream event combined with the latest item of the
        // other upstreams. Only starts after all streams emitted at least once. Some Events might get lost, that way
        .asTuple()
        .collect().asList()
        .await().indefinitely();

    System.out.println(combination); // expect quick 1 to be lost.
    // Expect first downstream event to be triggered on arrival of slow 1. Next event is triggered by intermediateMulti
    // 280 millis after await() call. Third and fourth event stems from quickMulti 300 and 400 millis after await() call.
  }
}