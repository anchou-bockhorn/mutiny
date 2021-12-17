package ch.abraxus;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ATransform {

  public <T> Multi<T> toMulti(Uni<T> uni) {
    return uni.onItem().transformToMulti(item -> Multi.createFrom().items(item));
  }

  public <T> Uni<List<T>> toCollectToUni(Multi<T> multi) {
    return multi.collect().asList();
  }

  public <T> Uni<T> firstItemToUni(Multi<T> multi) {
    return multi.toUni();
  }
}
