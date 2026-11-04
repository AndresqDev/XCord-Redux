package org.eclipse.sisu.bean;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import java.util.ArrayList;
import java.util.HashSet;
import xcord.Inject;

public final class BeanListener implements TypeListener {
   private final BeanBinder beanBinder;

   public BeanListener(BeanBinder var1) {
      this.beanBinder = var1;
   }

   public <B> void hear(TypeLiteral<B> var1, TypeEncounter<B> var2) {
      PropertyBinder var3 = this.beanBinder.bindBean(var1, var2);
      if (var3 != null) {
         ArrayList var4 = new ArrayList();
         HashSet var5 = new HashSet();

         for (BeanProperty var6 : new BeanProperties(var1.getRawType())) {
            if (var6.getAnnotation(Inject.class) == null && var6.getAnnotation(com.google.inject.Inject.class) == null) {
               String var8 = var6.getName();
               if (var5.add(var8)) {
                  try {
                     PropertyBinding var9 = var3.bindProperty(var6);
                     if (var9 == PropertyBinder.LAST_BINDING) {
                        break;
                     }

                     if (var9 != null) {
                        var4.add(var9);
                     } else {
                        var5.remove(var8);
                     }
                  } catch (RuntimeException var10) {
                     var2.addError(new ProvisionException("Error binding: " + var6, var10));
                  }
               }
            }
         }

         if (var4.size() > 0) {
            var2.register(new BeanInjector(var4));
         }
      }
   }
}
