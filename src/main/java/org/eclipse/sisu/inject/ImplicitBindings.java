package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.eclipse.sisu.Hidden;

final class ImplicitBindings {
   private final Iterable<BindingPublisher> publishers;

   ImplicitBindings(Iterable<BindingPublisher> var1) {
      this.publishers = var1;
   }

   public <T> Binding<T> get(TypeLiteral<T> var1) {
      Key var2 = TypeArguments.implicitKey(var1.getRawType());

      for (BindingPublisher var3 : this.publishers) {
         if (var3 instanceof InjectorBindings) {
            Injector var5 = ((InjectorBindings)var3).getInjector();
            Binding var6 = (Binding)var5.getBindings().get(var2);
            if (var6 != null) {
               Logs.trace("Using implicit binding: {} from: <>", var6, var5);
               return var6;
            }
         }
      }

      Key var10 = Key.get(var1);

      for (BindingPublisher var11 : this.publishers) {
         if (var11 instanceof InjectorBindings) {
            Injector var13 = ((InjectorBindings)var11).getInjector();

            try {
               Binding var7 = var13.getBinding(var10);
               if (Sources.getAnnotation(var7, Hidden.class) == null) {
                  Logs.trace("Using just-in-time binding: {} from: <>", var7, var13);
                  return var7;
               }
            } catch (RuntimeException var8) {
               Logs.trace("Problem with just-in-time binding: {}", var10, var8);
            } catch (LinkageError var9) {
               Logs.trace("Problem with just-in-time binding: {}", var10, var9);
            }
         }
      }

      return null;
   }
}
