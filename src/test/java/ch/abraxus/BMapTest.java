package ch.abraxus;

import static java.time.Duration.ofSeconds;
import static java.time.LocalTime.now;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.List;
import org.junit.jupiter.api.Test;

@QuarkusTest
class BMapTest {

  @Test
  public void mapWithSyncMethod() {
    String suffix = " Suffix";

    Multi<String> mappedMulti = Multi.createFrom().items("Item 1", "Item 2", "Item 3")
        .map(item -> item + suffix); // is a shortcut for .onItem().transform()

    List<String> collectedMappedItems = mappedMulti.collect().asList()
        .await().indefinitely();

    System.out.println(collectedMappedItems);
  }

  @Test
  public void mapWithSubsequentAsyncMethod() {
    String suffix = " Suffix";

    Multi<String> mappedMulti = Multi.createFrom().items("Item 1", "Item 2", "Item 3")
        .onItem().transformToUniAndConcatenate( // shortcut for .transformToUni().concatenate()
            item -> Uni.createFrom().item(suffix).onItem().delayIt().by(ofSeconds(1)).map(uniResult -> item + uniResult)
        );

    System.out.println(now());
    List<String> collectedMappedItems = mappedMulti.collect().asList()
        .await().indefinitely();
    System.out.println(now()); // executes the unis one after the other. Expect 3 second duration

    System.out.println(collectedMappedItems); // expect to be in order
  }

  @Test
  public void mapWithParallelAsyncMethod() {
    String suffix = " Suffix";

    Multi<String> mappedMulti = Multi.createFrom().items("Item 1", "Item 2", "Item 3")
        .onItem().transformToUniAndMerge( // shortcut for .transformToUni().merge()
            item -> Uni.createFrom().item(suffix).onItem().delayIt().by(ofSeconds(1)).map(uniResult -> item + uniResult)
        );

    System.out.println(now());
    List<String> collectedMappedItems = mappedMulti.collect().asList()
        .await().indefinitely();
    System.out.println(now()); // executes the unis in parallel. Expect 1 second duration

    System.out.println(collectedMappedItems); // might be out of order
  }

  @Test
  public void mapWithSubsequentAsyncMultiMethod() {
    Multi<String> mappedMulti = Multi.createFrom().items("Item 1", "Item 2", "Item 3")
        .onItem().transformToMultiAndConcatenate( // shortcut for .transformToMulti().concatenate()
            item -> Multi.createFrom().items("a", "b")
                .onItem().transformToUniAndConcatenate(
                    element -> Uni.createFrom().item(element).onItem().delayIt().by(ofSeconds(1))
                ).map(uniResult -> item + uniResult)
        );

    System.out.println(now());
    List<String> collectedMappedItems = mappedMulti.collect().asList()
        .await().indefinitely();
    System.out.println(now()); // executes the unis sequentially. Expect 6 second duration

    System.out.println(collectedMappedItems); // expect to be in order
  }

  @Test
  public void mapWithParallelAsyncMultiMethod() {
    Multi<String> mappedMulti = Multi.createFrom().items("Item 1", "Item 2", "Item 3")
        .onItem().transformToMultiAndMerge( // shortcut for .transformToMulti().merge()
            item -> Multi.createFrom().items("a", "b")
                .onItem().transformToUniAndMerge(
                    element -> Uni.createFrom().item(element).onItem().delayIt().by(ofSeconds(1))
                ).map(uniResult -> item + uniResult)
        );

    System.out.println(now());
    List<String> collectedMappedItems = mappedMulti.collect().asList()
        .await().indefinitely();
    System.out.println(now()); // executes the unis in parallel. Expect 1 second duration

    System.out.println(collectedMappedItems); // might be out of order
  }
}