package org.jline.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ShutdownHooks {
   private static final List<ShutdownHooks.Task> tasks = new ArrayList<>();
   private static Thread hook;

   public static synchronized <T extends ShutdownHooks.Task> T add(T var0) {
      Objects.requireNonNull(var0);
      if (hook == null) {
         hook = addHook(new Thread("JLine Shutdown Hook") {
            @Override
            public void run() {
               ShutdownHooks.runTasks();
            }
         });
      }

      Log.debug("Adding shutdown-hook task: ", var0);
      tasks.add(var0);
      return (T)var0;
   }

   private static synchronized void runTasks() {
      Log.debug("Running all shutdown-hook tasks");

      for (ShutdownHooks.Task var3 : tasks.toArray(new ShutdownHooks.Task[tasks.size()])) {
         Log.debug("Running task: ", var3);

         try {
            var3.run();
         } catch (Throwable var5) {
            Log.warn("Task failed", var5);
         }
      }

      tasks.clear();
   }

   private static Thread addHook(Thread var0) {
      Log.debug("Registering shutdown-hook: ", var0);
      Runtime.getRuntime().addShutdownHook(var0);
      return var0;
   }

   public static synchronized void remove(ShutdownHooks.Task var0) {
      Objects.requireNonNull(var0);
      if (hook != null) {
         tasks.remove(var0);
         if (tasks.isEmpty()) {
            removeHook(hook);
            hook = null;
         }
      }
   }

   private static void removeHook(Thread var0) {
      Log.debug("Removing shutdown-hook: ", var0);

      try {
         Runtime.getRuntime().removeShutdownHook(var0);
      } catch (IllegalStateException var2) {
      }
   }

   public interface Task {
      void run() throws Exception;
   }
}
