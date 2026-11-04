package org.eclipse.sisu.inject;

import com.google.inject.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Iterator;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;

@Deprecated
public final class Legacy<S> {
   private static final Legacy<BeanEntry<?, ?>> LEGACY_BEAN_ENTRY = as(org.sonatype.inject.BeanEntry.class);
   private final Constructor<?> proxyConstructor;

   private Legacy(Class<? extends S> var1) {
      Class var2 = Proxy.getProxyClass(var1.getClassLoader(), var1);

      try {
         this.proxyConstructor = var2.getConstructor(InvocationHandler.class);
      } catch (NoSuchMethodException var4) {
         throw new IllegalStateException(var4);
      }
   }

   public <T extends S> T proxy(final S var1) {
      try {
         return (T)(var1 == null ? null : this.proxyConstructor.newInstance(new InvocationHandler() {
            @Override
            public Object invoke(Object var1x, Method var2, Object[] var3) throws Exception {
               return var2.invoke(var1, var3);
            }
         }));
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   public static <S, T extends S> Legacy<S> as(Class<T> var0) {
      return new Legacy<>(var0);
   }

   public static <Q extends Annotation, T> org.sonatype.inject.BeanEntry<Q, T> adapt(BeanEntry<Q, T> var0) {
      return LEGACY_BEAN_ENTRY.proxy(var0);
   }

   public static <Q extends Annotation, T> Iterable<org.sonatype.inject.BeanEntry<Q, T>> adapt(final Iterable<? extends BeanEntry<Q, T>> var0) {
      return new Iterable<org.sonatype.inject.BeanEntry<Q, T>>() {
         @Override
         public Iterator<org.sonatype.inject.BeanEntry<Q, T>> iterator() {
            final Iterator var1 = var0.iterator();
            return new Iterator<org.sonatype.inject.BeanEntry<Q, T>>() {
               @Override
               public boolean hasNext() {
                  return var1.hasNext();
               }

               public org.sonatype.inject.BeanEntry<Q, T> next() {
                  return Legacy.adapt((BeanEntry<Q, T>)var1.next());
               }

               @Override
               public void remove() {
                  var1.remove();
               }
            };
         }
      };
   }

   public static <Q extends Annotation, T> Provider<Iterable<org.sonatype.inject.BeanEntry<Q, T>>> adapt(
      final Provider<Iterable<? extends BeanEntry<Q, T>>> var0
   ) {
      return new Provider<Iterable<org.sonatype.inject.BeanEntry<Q, T>>>() {
         public Iterable<org.sonatype.inject.BeanEntry<Q, T>> get() {
            return Legacy.adapt((Iterable<? extends BeanEntry<Q, T>>)var0.get());
         }
      };
   }

   public static <Q extends Annotation, T, W> Mediator<Q, T, W> adapt(final org.sonatype.inject.Mediator<Q, T, W> var0) {
      return var0 == null ? null : new Mediator<Q, T, W>() {
         @Override
         public void add(BeanEntry<Q, T> var1, W var2) throws Exception {
            var0.add(Legacy.adapt(var1), var2);
         }

         @Override
         public void remove(BeanEntry<Q, T> var1, W var2) throws Exception {
            var0.remove(Legacy.adapt(var1), var2);
         }
      };
   }
}
