package org.eclipse.sisu.bean;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LifecycleManager extends BeanScheduler implements BeanManager {
   private final LifecycleBuilder builder = new LifecycleBuilder();
   private final Map<Class<?>, BeanLifecycle> lifecycles = new ConcurrentHashMap<>();
   private final Deque<Object> stoppableBeans = new ArrayDeque<>();

   @Override
   public boolean manage(Class<?> var1) {
      return this.buildLifecycle(var1);
   }

   @Override
   public PropertyBinding manage(BeanProperty<?> var1) {
      return null;
   }

   @Override
   public boolean manage(Object var1) {
      BeanLifecycle var2 = this.lifecycleFor(var1);
      if (var2.isStoppable()) {
         this.pushStoppable(var1);
      }

      if (var2.isStartable()) {
         this.schedule(var1);
      }

      return true;
   }

   @Override
   public boolean unmanage(Object var1) {
      if (this.removeStoppable(var1)) {
         this.lifecycleFor(var1).stop(var1);
      }

      return true;
   }

   @Override
   public boolean unmanage() {
      Object var1;
      while ((var1 = this.popStoppable()) != null) {
         this.lifecycleFor(var1).stop(var1);
      }

      return true;
   }

   @Override
   protected void activate(Object var1) {
      this.lifecycleFor(var1).start(var1);
   }

   private boolean buildLifecycle(Class<?> var1) {
      BeanLifecycle var2 = this.lifecycles.get(var1);
      if (var2 == null) {
         var2 = this.builder.build(var1);
         this.lifecycles.put(var1, var2);
      }

      return var2 != BeanLifecycle.NO_OP;
   }

   private BeanLifecycle lifecycleFor(Object var1) {
      if (var1 != null) {
         for (Class var2 = var1.getClass(); var2 != null && var2 != Object.class; var2 = var2.getSuperclass()) {
            BeanLifecycle var3 = this.lifecycles.get(var2);
            if (var3 != null) {
               return var3;
            }
         }
      }

      return BeanLifecycle.NO_OP;
   }

   private void pushStoppable(Object var1) {
      synchronized (this.stoppableBeans) {
         this.stoppableBeans.addLast(var1);
      }
   }

   private boolean removeStoppable(Object var1) {
      synchronized (this.stoppableBeans) {
         return this.stoppableBeans.remove(var1);
      }
   }

   private Object popStoppable() {
      synchronized (this.stoppableBeans) {
         return this.stoppableBeans.pollLast();
      }
   }
}
