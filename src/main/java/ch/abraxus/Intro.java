package ch.abraxus;

import static java.time.Duration.ofMillis;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
public class Intro {

  public <T> Uni<T> toUni(T item) {
    return Uni.createFrom().item(item);
  }

  public <T> Uni<T> toUniDelayed(T item, Duration delay) {
    return Uni.createFrom().item(item)
        .onItem().delayIt().by(delay);
  }

  public Multi<String> count() {
    AtomicInteger i = new AtomicInteger();
    return Multi.createFrom().ticks().every(ofMillis(500))
        .map(nothing -> String.valueOf(i.incrementAndGet()));
  }
}