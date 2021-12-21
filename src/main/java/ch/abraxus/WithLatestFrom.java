package ch.abraxus;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.operators.multi.AbstractMultiOperator;
import io.smallrye.mutiny.operators.multi.MultiOperatorProcessor;
import io.smallrye.mutiny.subscription.MultiSubscriber;
import java.util.concurrent.ThreadLocalRandom;

public class WithLatestFrom<T> extends AbstractMultiOperator<T, T> {

  public WithLatestFrom(Multi<? extends T> upstream) {
    super(upstream);
  }

  /*
  public class WithLatestFrom<T, U, V> implements Transformer<T, V> {
     private final Func2<T, U, V> function;
     private final Observable<U> latest;

     private WithLatestFrom<T, U, V>(final Observable<U> latest, Func2<T, U, V> function) {
       this.function = function;
       this.latest = latest;
     }

     public static <T, U, V> WithLatestFrom<T, U, V> with(
         final Observable<U> latest, Func2<T, U, V> function) {
       return new WithLatestFrom<T, U, V>(latest, function);
     }

     @Override
     public Observable<V> call(final Observable<T> source) {
       return source.publish((publishedSource) -> latest.switchMap((y) ->
           publishedSource.map((x) -> function.call(x, y)));
     }

  }
   */

  @Override
  public void subscribe(MultiSubscriber<? super T> downstream) {
    upstream.subscribe().withSubscriber(new DropProcessor(downstream));
  }

  private class DropProcessor extends MultiOperatorProcessor<T, T> {
    DropProcessor(MultiSubscriber<? super T> downstream) {
      super(downstream);
    }

    @Override
    public void onItem(T item) {
      if (ThreadLocalRandom.current().nextBoolean()) {
        super.onItem(item);
      }
    }
  }
}