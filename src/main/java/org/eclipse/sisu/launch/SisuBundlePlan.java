package org.eclipse.sisu.launch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.eclipse.sisu.inject.BindingPublisher;
import org.eclipse.sisu.inject.InjectorBindings;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.osgi.framework.Bundle;

public class SisuBundlePlan implements BundlePlan {
   protected final MutableBeanLocator locator;

   public SisuBundlePlan(MutableBeanLocator var1) {
      this.locator = var1;
   }

   @Override
   public BindingPublisher prepare(Bundle var1) {
      return this.appliesTo(var1) ? new InjectorBindings(this.inject(this.compose(var1))) : null;
   }

   protected boolean appliesTo(Bundle var1) {
      if (var1.getHeaders().get("Bundle-Blueprint") != null) {
         return false;
      } else {
         String var2 = (String)var1.getHeaders().get("Import-Package");
         return var2 != null && (var2.contains("javax.inject") || var2.contains("com.google.inject"));
      }
   }

   protected Injector inject(Module var1) {
      return Guice.createInjector(new Module[]{var1});
   }

   protected Module compose(Bundle var1) {
      return new BundleModule(var1, this.locator);
   }
}
