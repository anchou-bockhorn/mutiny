package ch.abraxus;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FUseCases {
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