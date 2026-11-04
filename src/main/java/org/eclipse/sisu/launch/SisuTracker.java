package org.eclipse.sisu.launch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.sisu.inject.BindingPublisher;
import org.eclipse.sisu.inject.InjectorBindings;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.inject.Weak;
import org.eclipse.sisu.space.BundleClassSpace;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.BundleTracker;
import xcord.Inject;

public class SisuTracker extends BundleTracker<Object> implements BundlePlan {
   private static final Object PLACEHOLDER;
   private static final Set<String> SUPPORT_BUNDLE_NAMES;
   private static final Map<Long, Object> bundlePublishers;
   protected final int stateMask;
   protected final MutableBeanLocator locator;
   protected final List<BundlePlan> plans;

   static {
      HashSet var0 = new HashSet();
      Class[] var1 = new Class[]{Inject.class, Guice.class, SisuExtender.class};

      for (Class var2 : var1) {
         Bundle var6 = FrameworkUtil.getBundle(var2);
         if (var6 != null) {
            var0.add(var6.getSymbolicName());
         }
      }

      SUPPORT_BUNDLE_NAMES = var0;
      PLACEHOLDER = new Object();
      bundlePublishers = Collections.synchronizedMap(Weak.values());
   }

   public SisuTracker(BundleContext var1, int var2, MutableBeanLocator var3) {
      super(var1, var2, null);
      this.stateMask = var2;
      this.locator = var3;
      this.plans = this.discoverPlans();
   }

   public final void open() {
      super.open();
      this.purgeBundles();
   }

   public final Object addingBundle(Bundle var1, BundleEvent var2) {
      Long var3 = var1.getBundleId();
      if (!bundlePublishers.containsKey(var3)) {
         bundlePublishers.put(var3, PLACEHOLDER);
         BindingPublisher var4 = this.prepare(var1);
         if (var4 != null) {
            this.addPublisher(var3, var4);
         } else {
            bundlePublishers.remove(var3);
         }
      }

      return var1;
   }

   public final void removedBundle(Bundle var1, BundleEvent var2, Object var3) {
      if (this.evictBundle(var1)) {
         this.removePublisher(var1.getBundleId());
      }
   }

   public final void purgeBundles() {
      for (long var1 : new ArrayList<>(bundlePublishers.keySet())) {
         Bundle var4 = this.context.getBundle(var1);
         if (var4 == null || this.evictBundle(var4)) {
            this.removePublisher(var1);
         }
      }
   }

   @Override
   public BindingPublisher prepare(Bundle var1) {
      if (SUPPORT_BUNDLE_NAMES.contains(var1.getSymbolicName())) {
         return null;
      } else if (var1.getHeaders().get("Fragment-Host") != null) {
         return null;
      } else {
         BindingPublisher var2 = null;

         for (int var3 = this.plans.size() - 1; var3 >= 0 && var2 == null; var3--) {
            var2 = this.plans.get(var3).prepare(var1);
         }

         return var2;
      }
   }

   protected List<BundlePlan> discoverPlans() {
      ArrayList var1 = new ArrayList();
      var1.add(new SisuBundlePlan(this.locator));
      SisuExtensions var2 = SisuExtensions.local(new BundleClassSpace(this.context.getBundle()));
      var1.addAll(var2.create(BundlePlan.class, MutableBeanLocator.class, this.locator));
      return var1;
   }

   protected boolean evictBundle(Bundle var1) {
      return (var1.getState() & this.stateMask) == 0;
   }

   private void addPublisher(Long var1, BindingPublisher var2) {
      if (this.locator.add(var2)) {
         bundlePublishers.put(var1, var2);
      } else if (var2 instanceof InjectorBindings) {
         bundlePublishers.put(var1, ((InjectorBindings)var2).getInjector());
      }
   }

   private void removePublisher(Long var1) {
      Object var2 = bundlePublishers.remove(var1);
      if (var2 instanceof BindingPublisher) {
         this.locator.remove((BindingPublisher)var2);
      } else if (var2 instanceof Injector) {
         this.locator.remove(new InjectorBindings((Injector)var2, null));
      }
   }
}
