package ch.abraxus;

import static java.time.Duration.ofSeconds;
import static java.time.LocalTime.now;

import ch.abraxus.ATransform;
import ch.abraxus.Generator;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ATransformTest {

  @Inject
  Generator generator;

  @Inject
  ATransform transform;

  @Test
  void toMulti() {
    Uni<String> item = generator.toUniDelayed("item", ofSeconds(1));

    Multi<String> stringMulti = transform.toMulti(item);

    System.out.println(now());
    List<String> collection = stringMulti.collect().asList().await().indefinitely();
  // what happens if subscribe is used? What would be the Reactive Test Method signature?
    System.out.println(now());

    System.out.println(collection);
  }

  @Test
  void toCollectToUni() {
    Multi<String> multiEmitting3timesEachSecond = generator
        .toMultiDelayed(ofSeconds(1), "Item 1", "Item 2", "Item 3");

    System.out.println(now());
    List<String> collection = transform.toCollectToUni(multiEmitting3timesEachSecond)
        .await().indefinitely();
    System.out.println(now()); // waits for the Multi to complete

    System.out.println(collection);
  }

  @Test
  void firstItemToUni() {
    Multi<String> multiEmitting3timesEachSecond = generator
        .toMultiDelayed(ofSeconds(1), "Item 1", "Item 2", "Item 3");

    System.out.println(now());
    String collection = transform.firstItemToUni(multiEmitting3timesEachSecond)
        .await().indefinitely();
    System.out.println(now()); // does not wait for the Multi to complete

    System.out.println(collection);
  }
}