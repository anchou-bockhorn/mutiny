package ch.abraxus;

import static java.time.Duration.ofMillis;
import static java.time.Duration.ofSeconds;
import static java.time.LocalTime.now;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Multi;
import java.time.LocalTime;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class GeneratorTest {

  @Inject
  Generator generator;

  @Test
  public void toUni() {
    generator.toUni("banana")
        .subscribe().with(System.out::println);
  }

  @Test
  public void testBackpressure() {
    LocalTime startTime = now();
    Multi.createBy().repeating()
        .uni(() -> {
          System.out.println("create upstream uni");
          return generator.toUniDelayed("delayed", ofMillis(1));
        })
        .until(item -> {
          System.out.println(now().minusSeconds(20).isAfter(startTime));
          return now().minusSeconds(20).isAfter(startTime);
        })
        .onItem().transformToUniAndMerge(item -> {
          System.out.println("create downstream uni");
          return generator.toUniDelayed("delayed", ofMillis(5000));
        }).collect().asList()
        .await().indefinitely();
  }

  @Test
  public void multiThreadingUni() {
    generator.toUniDelayed("delayed 1", ofSeconds(2))
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    generator.toUniDelayed("delayed 2", ofSeconds(2))
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    generator.toUniDelayed("delayed 3", ofSeconds(2))
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    generator.toUniDelayed("delayed 4", ofSeconds(2))
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    generator.toUniDelayed("delayed 5", ofSeconds(2))
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    generator.toUniDelayed("delayed 6", ofSeconds(2))
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    generator.toUniDelayed("delayed 7", ofSeconds(2))
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    generator.toUniDelayed("delayed 8", ofSeconds(2))
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    generator.toUniDelayed("await test completion", ofSeconds(3))
        .await().indefinitely();
  }


  @Test
  public void multiThreadingMulti() {
    System.out.println("started");
    Multi.createFrom().ticks().every(ofSeconds(1))
        .map(i -> "Item no. " + i + " of first stream")
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    Multi.createFrom().ticks().every(ofSeconds(1))
        .map(i -> "Item no. " + i + " of second stream")
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    Multi.createFrom().ticks().every(ofSeconds(1))
        .map(i -> "Item no. " + i + " of third stream")
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    Multi.createFrom().ticks().every(ofSeconds(1))
        .map(i -> "Item no. " + i + " of fourth stream")
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    Multi.createFrom().ticks().every(ofSeconds(1))
        .map(i -> "Item no. " + i + " of fifth stream")
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    Multi.createFrom().ticks().every(ofSeconds(1))
        .map(i -> "Item no. " + i + " of sixth stream")
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    Multi.createFrom().ticks().every(ofSeconds(1))
        .map(i -> "Item no. " + i + " of seventh stream")
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    Multi.createFrom().ticks().every(ofSeconds(1))
        .map(i -> "Item no. " + i + " of eighth stream")
        .subscribe()
        .with(x -> System.out.println(x + " processed by Thread No: " + Thread.currentThread().getId()));

    System.out.println("waiting");
    generator.toUniDelayed("await test completion", ofSeconds(15))
        .await().indefinitely();
//    try {
//      Thread.sleep(10000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
  }
}