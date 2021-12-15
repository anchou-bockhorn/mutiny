package ch.abraxus;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.time.LocalTime;
import java.util.function.Function;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static java.time.Duration.ofMillis;
import static java.time.Duration.ofMinutes;
import static java.time.LocalTime.now;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class IntroTest {
//  todo: showcase backpressure
  @Inject
  Intro intro;

  @Test
  public void toUni() {
    intro.toUni("banana")
        .subscribe().with(System.out::println);
  }

  @Test
  public void testBackpressure() throws InterruptedException {
    LocalTime startTime = now();
    Multi.createBy().repeating()
        .uni(() -> {
          System.out.println("create upstream uni");
          return intro.toUniDelayed("delayed", ofMillis(1));
        })
        .until(item -> {
          System.out.println(now().minusSeconds(20).isAfter(startTime));
          return now().minusSeconds(20).isAfter(startTime);
        })
        .onItem().transformToUniAndMerge(item -> {
          System.out.println("create downstream uni");
          return intro.toUniDelayed("delayed", ofMillis(10000));
        }).collect().asList()
        .await().indefinitely();
  }
}