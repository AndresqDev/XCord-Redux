package org.eclipse.sisu.bean;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.DefaultBindingScopingVisitor;
import com.google.inject.spi.ProvisionListener;
import com.google.inject.spi.ProvisionListener.ProvisionInvocation;
import java.util.ArrayList;

public abstract class BeanScheduler {
   static final Object CYCLE_ACTIVATOR;
   static final Object CANDIDATE_CYCLE;
   static final Object CYCLE_CONFIRMED;
   public static final Module MODULE;
   static final ThreadLocal<Object[]> pendingHolder;

   static {
      Object var1 = new Object();
      Object var2 = new Object();

      BeanScheduler.CycleActivator var0;
      try {
         Binder.class.getMethod("bindListener", Matcher.class, ProvisionListener[].class);
         String var3 = System.getProperty("sisu.detect.cycles");
         if ("false".equalsIgnoreCase(var3)) {
            var0 = null;
         } else {
            var0 = new BeanScheduler.CycleActivator();
         }

         if ("pessimistic".equalsIgnoreCase(var3)) {
            var1 = var2;
         }
      } catch (Exception var4) {
         var0 = null;
      } catch (LinkageError var5) {
         var0 = null;
      }

      CYCLE_ACTIVATOR = var0;
      CANDIDATE_CYCLE = var1;
      CYCLE_CONFIRMED = var2;
      MODULE = new Module() {
         public void configure(Binder var1) {
            if (BeanScheduler.CYCLE_ACTIVATOR != null) {
               var1.bindListener(Matchers.any(), new ProvisionListener[]{(ProvisionListener)BeanScheduler.CYCLE_ACTIVATOR});
            }
         }
      };
      pendingHolder = new ThreadLocal<>();
   }

   public static void detectCycle(Object var0) {
      if (CYCLE_ACTIVATOR != null && Scopes.isCircularProxy(var0)) {
         Object[] var1 = pendingHolder.get();
         if (var1 != null) {
            Object var2 = var1[0];
            if (CANDIDATE_CYCLE.equals(var2)) {
               var1[0] = CYCLE_CONFIRMED;
            }
         }
      }
   }

   public final void schedule(Object var1) {
      if (CYCLE_ACTIVATOR != null) {
         Object[] var2 = pendingHolder.get();
         if (var2 != null) {
            Object var3 = var2[0];
            if (CYCLE_CONFIRMED.equals(var3)) {
               var2[0] = new BeanScheduler.Pending(var1);
               return;
            }

            if (var3 instanceof BeanScheduler.Pending) {
               ((BeanScheduler.Pending)var3).add(var1);
               return;
            }
         }
      }

      this.activate(var1);
   }

   protected abstract void activate(Object var1);

   static final class CycleActivator implements ProvisionListener {
      private static final BindingScopingVisitor<Boolean> IS_SCOPED = new DefaultBindingScopingVisitor<Boolean>() {
         public Boolean visitNoScoping() {
            return Boolean.FALSE;
         }

         protected Boolean visitOther() {
            return Boolean.TRUE;
         }
      };

      public <T> void onProvision(ProvisionInvocation<T> var1) {
         if (Boolean.TRUE.equals(var1.getBinding().acceptScopingVisitor(IS_SCOPED))) {
            Object[] var2 = BeanScheduler.pendingHolder.get();
            if (var2 == null) {
               BeanScheduler.pendingHolder.set(var2 = new Object[1]);
            }

            if (var2[0] == null) {
               var2[0] = BeanScheduler.CANDIDATE_CYCLE;

               Object var3;
               try {
                  var1.provision();
               } finally {
                  var3 = var2[0];
                  var2[0] = null;
               }

               if (var3_2 instanceof BeanScheduler.Pending) {
                  ((BeanScheduler.Pending)var3_2).activate();
               }
            }
         }
      }
   }

   private final class Pending extends ArrayList<Object> {
      Pending(Object var2) {
         this.add(var2);
      }

      public void activate() {
         int var1 = 0;

         for (int var2 = this.size(); var1 < var2; var1++) {
            BeanScheduler.this.activate(this.get(var1));
         }
      }
   }
}
