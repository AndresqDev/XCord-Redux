package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Scopes;
import com.google.inject.spi.ElementSource;
import com.google.inject.spi.ProviderInstanceBinding;
import xcord.Provider;

public final class Guice4 {
   private static final boolean HAS_DECLARING_SOURCE;
   private static final boolean HAS_USER_SUPPLIED_PROVIDER;
   private static final boolean HAS_OLD_SCOPES_SINGLETON;
   static final Object NIL;

   static {
      boolean var0;
      try {
         var0 = ElementSource.class.getMethod("getDeclaringSource") != null;
      } catch (Exception var5) {
         var0 = false;
      } catch (LinkageError var6) {
         var0 = false;
      }

      HAS_DECLARING_SOURCE = var0;

      boolean var1;
      try {
         var1 = ProviderInstanceBinding.class.getMethod("getUserSuppliedProvider") != null;
      } catch (Exception var3) {
         var1 = false;
      } catch (LinkageError var4) {
         var1 = false;
      }

      HAS_USER_SUPPLIED_PROVIDER = var1;

      boolean var2;
      try {
         var2 = Scopes.class.equals(Scopes.SINGLETON.getClass().getEnclosingClass()) && Scopes.SINGLETON.scope(null, null) != null;
      } catch (Exception var7) {
         var2 = false;
      } catch (LinkageError var8) {
         var2 = false;
      }

      HAS_OLD_SCOPES_SINGLETON = var2;
      NIL = new Object();
   }

   private Guice4() {
   }

   public static Object getDeclaringSource(Binding<?> var0) {
      Object var1 = var0.getSource();
      return HAS_DECLARING_SOURCE && var1 instanceof ElementSource ? ((ElementSource)var1).getDeclaringSource() : var1;
   }

   public static Provider<?> getProviderInstance(ProviderInstanceBinding<?> var0) {
      return (Provider<?>)(HAS_USER_SUPPLIED_PROVIDER ? var0.getUserSuppliedProvider() : var0.getProviderInstance());
   }

   public static <T> Provider<T> lazy(Binding<T> var0) {
      if (HAS_OLD_SCOPES_SINGLETON) {
         return Scopes.SINGLETON.scope(var0.getKey(), var0.getProvider());
      } else {
         final com.google.inject.Provider var1 = var0.getProvider();
         return new Provider<T>() {
            private volatile Object value = Guice4.NIL;

            public T get() {
               if (Guice4.NIL == this.value) {
                  synchronized (this) {
                     if (Guice4.NIL == this.value) {
                        this.value = var1.get();
                     }
                  }
               }

               return (T)this.value;
            }
         };
      }
   }
}
