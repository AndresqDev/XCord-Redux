package org.eclipse.sisu.osgi;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.name.Names;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.ElementVisitor;
import org.eclipse.sisu.inject.BindingSubscriber;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import xcord.Singleton;

final class ServiceBinding<T> implements Binding<T>, Provider<T> {
   private final Key<T> key;
   private final T instance;
   private final int rank;

   ServiceBinding(BundleContext var1, String var2, int var3, ServiceReference<T> var4) throws ClassNotFoundException {
      Class var5 = var4.getBundle().loadClass(var2);
      Object var6 = var4.getProperty("name");
      if (var6 instanceof String && ((String)var6).length() > 0) {
         this.key = Key.get(var5, Names.named((String)var6));
      } else {
         this.key = Key.get(var5);
      }

      this.instance = (T)var1.getService(var4);
      if (var3 > Integer.MIN_VALUE) {
         int var7 = getServiceRanking(var4);
         this.rank = var7 < var3 ? var7 : var3;
      } else {
         this.rank = Integer.MIN_VALUE;
      }
   }

   public Key<T> getKey() {
      return this.key;
   }

   public Provider<T> getProvider() {
      return this;
   }

   public T get() {
      return this.instance;
   }

   public Object getSource() {
      return "OSGi service registry";
   }

   public void applyTo(Binder var1) {
   }

   public <V> V acceptVisitor(ElementVisitor<V> var1) {
      return (V)var1.visit(this);
   }

   public <V> V acceptTargetVisitor(BindingTargetVisitor<? super T, V> var1) {
      return null;
   }

   public <V> V acceptScopingVisitor(BindingScopingVisitor<V> var1) {
      return (V)var1.visitScopeAnnotation(Singleton.class);
   }

   boolean isCompatibleWith(BindingSubscriber<T> var1) {
      return this.key.getTypeLiteral().getRawType().equals(var1.type().getRawType());
   }

   int rank() {
      return this.rank;
   }

   private static int getServiceRanking(ServiceReference<?> var0) {
      Object var1 = var0.getProperty("service.ranking");
      return var1 instanceof Integer ? (Integer)var1 : 0;
   }
}
