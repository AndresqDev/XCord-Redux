package net.md_5.bungee.event;

import com.google.common.collect.ImmutableSet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventBus {
   protected final Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byListenerAndPriority = new HashMap<>();
   protected final Map<Class<?>, EventHandlerMethod[]> byEventBaked = new ConcurrentHashMap<>();
   private final Lock lock = new ReentrantLock();
   private final Logger logger;
   private boolean[] nonEmptyListeners = new boolean[0];
   private final Map<Class<?>, Integer> watchingEmptyListeners = new IdentityHashMap<>();

   public EventBus() {
      this(null);
   }

   public EventBus(Logger var1) {
      this.logger = var1 == null ? Logger.getLogger("global") : var1;
   }

   public boolean isWatchedListenerActive(int var1) {
      return var1 >= this.nonEmptyListeners.length ? true : this.nonEmptyListeners[var1];
   }

   public int watchForEmpty(Class<?> var1) {
      this.watchingEmptyListeners.put(var1, this.nonEmptyListeners.length);
      if (this.nonEmptyListeners.length < this.watchingEmptyListeners.size()) {
         this.nonEmptyListeners = Arrays.copyOf(this.nonEmptyListeners, this.nonEmptyListeners.length + 1);
      }

      return this.nonEmptyListeners.length - 1;
   }

   public <T> void post(T var1, EventExceptionHandler<T> var2) {
      EventHandlerMethod[] var3 = this.byEventBaked.get(var1.getClass());
      if (var3 != null) {
         for (EventHandlerMethod var7 : var3) {
            try {
               var7.invoke(var1);
            } catch (IllegalAccessException var10) {
               throw new Error("Method became inaccessible: " + var1, var10);
            } catch (IllegalArgumentException var11) {
               throw new Error("Method rejected target/argument: " + var1, var11);
            } catch (InvocationTargetException var12) {
               String var9 = MessageFormat.format("Error dispatching event {0} to listener {1}", var1, var7.getListener());
               this.logger.log(Level.WARNING, var9, var12.getCause());
               if (var2 != null) {
                  var2.handleEventException(var9, var1, var7, var12);
               }
            }
         }
      }
   }

   private Map<Class<?>, Map<Byte, Set<Method>>> findHandlers(Object var1) {
      HashMap var2 = new HashMap();

      for (Method var5 : ImmutableSet.builder().add(var1.getClass().getMethods()).add(var1.getClass().getDeclaredMethods()).build()) {
         EventHandler var6 = var5.getAnnotation(EventHandler.class);
         if (var6 != null) {
            Class[] var7 = var5.getParameterTypes();
            if (var7.length != 1) {
               this.logger
                  .log(Level.INFO, "Method {0} in class {1} annotated with {2} does not have single argument", new Object[]{var5, var1.getClass(), var6});
            } else {
               Object var8 = (Map)var2.get(var7[0]);
               if (var8 == null) {
                  var8 = new HashMap();
                  var2.put(var7[0], var8);
               }

               Object var9 = (Set)var8.get(var6.priority());
               if (var9 == null) {
                  var9 = new HashSet();
                  var8.put(var6.priority(), var9);
               }

               var9.add(var5);
            }
         }
      }

      return var2;
   }

   public void register(Object var1) {
      Map var2 = this.findHandlers(var1);
      this.lock.lock();

      try {
         for (Entry var4 : var2.entrySet()) {
            Object var5 = this.byListenerAndPriority.get(var4.getKey());
            if (var5 == null) {
               var5 = new HashMap();
               this.byListenerAndPriority.put((Class<?>)var4.getKey(), (Map<Byte, Map<Object, Method[]>>)var5);
            }

            for (Entry var7 : ((Map)var4.getValue()).entrySet()) {
               Object var8 = (Map)var5.get(var7.getKey());
               if (var8 == null) {
                  var8 = new HashMap();
                  var5.put((Byte)var7.getKey(), var8);
               }

               var8.put(var1, ((Set)var7.getValue()).toArray(new Method[0]));
            }

            this.bakeHandlers((Class<?>)var4.getKey());
         }
      } finally {
         this.lock.unlock();
      }
   }

   public void unregister(Object var1) {
      Map var2 = this.findHandlers(var1);
      this.lock.lock();

      try {
         for (Entry var4 : var2.entrySet()) {
            Map var5 = this.byListenerAndPriority.get(var4.getKey());
            if (var5 != null) {
               for (Byte var7 : ((Map)var4.getValue()).keySet()) {
                  Map var8 = (Map)var5.get(var7);
                  if (var8 != null) {
                     var8.remove(var1);
                     if (var8.isEmpty()) {
                        var5.remove(var7);
                     }
                  }
               }

               if (var5.isEmpty()) {
                  this.byListenerAndPriority.remove(var4.getKey());
               }
            }

            this.bakeHandlers((Class<?>)var4.getKey());
         }
      } finally {
         this.lock.unlock();
      }
   }

   private void bakeHandlers(Class<?> var1) {
      Integer var2 = this.watchingEmptyListeners.get(var1);
      if (var2 != null) {
         this.nonEmptyListeners[var2] = true;
      }

      Map var3 = this.byListenerAndPriority.get(var1);
      if (var3 != null) {
         ArrayList var4 = new ArrayList(var3.size() * 2);
         byte var5 = -128;

         do {
            Map var6 = (Map)var3.get(var5);
            if (var6 != null) {
               for (Entry var8 : var6.entrySet()) {
                  for (Method var12 : (Method[])var8.getValue()) {
                     EventHandlerMethod var13 = new EventHandlerMethod(var8.getKey(), var12);
                     var4.add(var13);
                  }
               }
            }
         } while (var5++ < 127);

         this.byEventBaked.put(var1, var4.toArray(new EventHandlerMethod[0]));
      } else {
         this.byEventBaked.remove(var1);
      }
   }

   public Map<Class<?>, Map<Byte, Map<Object, Method[]>>> getByListenerAndPriority() {
      return this.byListenerAndPriority;
   }

   public Map<Class<?>, EventHandlerMethod[]> getByEventBaked() {
      return this.byEventBaked;
   }
}
