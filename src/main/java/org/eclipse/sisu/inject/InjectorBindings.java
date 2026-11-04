package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.sisu.Hidden;

public final class InjectorBindings implements BindingPublisher {
   private static final TypeLiteral<Object> OBJECT_TYPE_LITERAL = TypeLiteral.get(Object.class);
   private static final Binding<?>[] NO_BINDINGS = new Binding[0];
   private final Injector injector;
   private final RankingFunction function;
   private volatile Binding<?>[] wildcards;

   public InjectorBindings(Injector var1, RankingFunction var2) {
      this.injector = var1;
      this.function = var2;
   }

   public InjectorBindings(Injector var1) {
      this(var1, (RankingFunction)var1.getInstance(RankingFunction.class));
   }

   public Injector getInjector() {
      return this.injector;
   }

   @Override
   public <T> void subscribe(BindingSubscriber<T> var1) {
      TypeLiteral var2 = var1.type();
      Class var3 = var2.getRawType();
      if (var3 != Object.class) {
         this.publishExactMatches(var2, var1);
         if (var3 != var2.getType()) {
            this.publishGenericMatches(var2, var1, var3);
         }
      }

      this.publishWildcardMatches(var2, var1);
   }

   @Override
   public <T> void unsubscribe(BindingSubscriber<T> var1) {
      Map var2 = this.injector.getBindings();

      for (Binding var3 : var1.bindings()) {
         if (var3 == var2.get(var3.getKey())) {
            var1.remove(var3);
         }
      }
   }

   @Override
   public int maxBindingRank() {
      return this.function.maxRank();
   }

   @Override
   public int hashCode() {
      return this.injector.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof InjectorBindings ? this.injector.equals(((InjectorBindings)var1).injector) : false;
      }
   }

   @Override
   public String toString() {
      return Logs.toString(this.injector);
   }

   private static <T, S> boolean isAssignableFrom(TypeLiteral<T> var0, Binding<S> var1) {
      Class var2 = Implementations.find(var1);
      return var2 != null && var0.getRawType() != var2 ? TypeArguments.isAssignableFrom(var0, TypeLiteral.get(var2)) : false;
   }

   private <T> void publishExactMatches(TypeLiteral<T> var1, BindingSubscriber<T> var2) {
      List var3 = this.injector.findBindingsByType(var1);
      int var4 = 0;

      for (int var5 = var3.size(); var4 < var5; var4++) {
         Binding var6 = (Binding)var3.get(var4);
         if (Sources.getAnnotation(var6, Hidden.class) == null) {
            var2.add(var6, this.function.rank(var6));
         }
      }
   }

   private <T, S> void publishGenericMatches(TypeLiteral<T> var1, BindingSubscriber<T> var2, Class<S> var3) {
      List var4 = this.injector.findBindingsByType(TypeLiteral.get(var3));
      int var5 = 0;

      for (int var6 = var4.size(); var5 < var6; var5++) {
         Binding var7 = (Binding)var4.get(var5);
         if (Sources.getAnnotation(var7, Hidden.class) == null && isAssignableFrom(var1, var7)) {
            var2.add(var7, this.function.rank(var7));
         }
      }
   }

   private <T> void publishWildcardMatches(TypeLiteral<T> var1, BindingSubscriber<T> var2) {
      boolean var3 = var1.getRawType() == Object.class;

      Binding[] var7;
      for (Binding var4 : var7 = this.getWildcardBindings()) {
         if (var3 || isAssignableFrom(var1, var4)) {
            var2.add(var4, this.function.rank(var4));
         }
      }
   }

   private Binding<?>[] getWildcardBindings() {
      if (this.wildcards == null) {
         synchronized (this) {
            if (this.wildcards == null) {
               ArrayList var2 = new ArrayList();
               List var3 = this.injector.findBindingsByType(OBJECT_TYPE_LITERAL);
               int var4 = 0;

               for (int var5 = var3.size(); var4 < var5; var4++) {
                  Binding var6 = (Binding)var3.get(var4);
                  if (Sources.getAnnotation(var6, Hidden.class) == null) {
                     var2.add(var6);
                  }
               }

               this.wildcards = var2.isEmpty() ? NO_BINDINGS : var2.toArray(new Binding[var2.size()]);
            }
         }
      }

      return this.wildcards;
   }
}
