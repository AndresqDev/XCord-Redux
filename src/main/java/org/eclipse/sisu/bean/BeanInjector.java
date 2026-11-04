package org.eclipse.sisu.bean;

import com.google.inject.MembersInjector;
import java.util.List;

final class BeanInjector<B> implements MembersInjector<B> {
   private final PropertyBinding[] bindings;

   BeanInjector(List<PropertyBinding> var1) {
      int var2 = var1.size();
      this.bindings = new PropertyBinding[var2];
      int var3 = 0;
      int var4 = var2;

      while (var3 < var2) {
         this.bindings[var3++] = (PropertyBinding)var1.get(--var4);
      }
   }

   public void injectMembers(B var1) {
      for (PropertyBinding var2 : this.bindings) {
         var2.injectProperty(var1);
      }
   }
}
