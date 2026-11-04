package org.eclipse.sisu.launch;

import java.util.Collections;
import java.util.Map;
import org.eclipse.sisu.inject.DefaultBeanLocator;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.inject.Weak;
import org.eclipse.sisu.osgi.ServiceBindings;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class SisuExtender implements BundleActivator {
   private static final Map<Long, MutableBeanLocator> locators = Collections.synchronizedMap(Weak.values());
   protected SisuTracker tracker;

   public void start(BundleContext var1) {
      this.tracker = this.createTracker(var1);
      this.tracker.open();
   }

   public void stop(BundleContext var1) {
      this.tracker.close();
      this.tracker = null;
   }

   protected int bundleStateMask() {
      return 40;
   }

   protected SisuTracker createTracker(BundleContext var1) {
      return new SisuTracker(var1, this.bundleStateMask(), this.findLocator(var1));
   }

   protected MutableBeanLocator createLocator(BundleContext var1) {
      DefaultBeanLocator var2 = new DefaultBeanLocator();
      var2.add(new ServiceBindings(var1));
      return var2;
   }

   protected final MutableBeanLocator findLocator(BundleContext var1) {
      Long var2 = var1.getBundle().getBundleId();
      MutableBeanLocator var3 = locators.get(var2);
      if (var3 == null) {
         locators.put(var2, var3 = this.createLocator(var1));
      }

      return var3;
   }
}
