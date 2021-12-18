package ch.abraxus;

import static java.time.LocalTime.now;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import java.util.List;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class CJoiningTest {

  @Inject
  Generator generator;

  @Test
  public void collectResultsOfSeveralUnis() {
    Uni<String> quickItem = generator.toUniDelayed("Quick item", Duration.ofSeconds(1));
    Uni<String> intermediateItem = generator.toUniDelayed("Intermediate item", Duration.ofSeconds(2));
    Uni<String> lateItem = generator.toUniDelayed("Late item", Duration.ofSeconds(3));

    System.out.println(now());
    List<String> collectedResults = Uni.join().all(intermediateItem, lateItem, quickItem)
        .andFailFast()
        .await().indefinitely();
    System.out.println(now()); // expect to wait for last Uni. Expect 3 seconds duration

    System.out.println(collectedResults);
  }

  @Test
  public void getResultOfResolvingUni() {
    Uni<String> quickItem = generator.toUniDelayed("Quick item", Duration.ofSeconds(1));
    Uni<String> intermediateItem = generator.toUniDelayed("Intermediate item", Duration.ofSeconds(2));
    Uni<String> lateItem = generator.toUniDelayed("Late item", Duration.ofSeconds(3));

    System.out.println(now());
    String firstResult = Uni.join().first(intermediateItem, lateItem, quickItem)
        .toTerminate()
        .await().indefinitely();
    System.out.println(now()); // expect to skip all but first resolving Uni. Expect 1 seconds duration

    System.out.println(firstResult);
  }
}