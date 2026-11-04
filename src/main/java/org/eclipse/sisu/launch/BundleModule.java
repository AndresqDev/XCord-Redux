package org.eclipse.sisu.launch;

import com.google.inject.Binder;
import com.google.inject.Module;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.BundleClassSpace;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.wire.ParameterKeys;
import org.eclipse.sisu.wire.WireModule;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class BundleModule implements Module {
   protected final BundleClassSpace space;
   protected final SisuExtensions extensions;
   protected final MutableBeanLocator locator;

   public BundleModule(Bundle var1, MutableBeanLocator var2) {
      this.space = new BundleClassSpace(var1);
      this.extensions = SisuExtensions.local(this.space);
      this.locator = var2;
   }

   public void configure(Binder var1) {
      new WireModule(this.modules()).with(this.extensions).configure(var1);
   }

   protected Map<?, ?> getProperties() {
      return System.getProperties();
   }

   protected List<Module> modules() {
      return Arrays.asList(this.extensionsModule(), this.contextModule(), this.spaceModule());
   }

   protected Module extensionsModule() {
      return new Module() {
         public void configure(Binder var1) {
            BundleModule.this.extensions.install(var1, Bundle.class, BundleModule.this.space.getBundle());
         }
      };
   }

   protected Module contextModule() {
      return new Module() {
         public void configure(Binder var1) {
            var1.bind(MutableBeanLocator.class).toInstance(BundleModule.this.locator);
            Bundle var2 = BundleModule.this.space.getBundle();
            var1.bind(ParameterKeys.PROPERTIES).toInstance(BundleModule.this.getProperties());
            var1.bind(BundleContext.class).toInstance(var2.getBundleContext());
         }
      };
   }

   protected Module spaceModule() {
      return new SpaceModule(this.space, BeanScanning.select(this.getProperties())).with(this.extensions);
   }
}
