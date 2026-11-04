package org.eclipse.sisu.osgi;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import org.eclipse.sisu.inject.BindingPublisher;
import org.eclipse.sisu.inject.BindingSubscriber;
import org.eclipse.sisu.inject.Logs;
import org.osgi.framework.BundleContext;

public final class ServiceBindings implements BindingPublisher {
   private static final Pattern GLOB_SYNTAX = Pattern.compile("(?:\\w+|\\*)(?:\\.?(?:\\w+|\\*))*");
   private final ConcurrentMap<String, BindingTracker<?>> trackers = new ConcurrentHashMap<>();
   private final BundleContext context;
   private final Pattern[] allowed;
   private final Pattern[] ignored;
   private final int maxRank;

   public ServiceBindings(BundleContext var1, String var2, String var3, int var4) {
      this.context = var1;
      this.maxRank = var4;
      this.allowed = parseGlobs(var2);
      this.ignored = parseGlobs(var3);
   }

   public ServiceBindings(BundleContext var1) {
      this(var1, defaultAllow(), defaultIgnore(), Integer.MIN_VALUE);
   }

   public static String defaultAllow() {
      return System.getProperty(ServiceBindings.class.getName() + ".allow", "");
   }

   public static String defaultIgnore() {
      return System.getProperty(ServiceBindings.class.getName() + ".ignore", "");
   }

   @Override
   public <T> void subscribe(BindingSubscriber<T> var1) {
      String var2 = var1.type().getRawType().getName();
      if (this.shouldTrack(var2)) {
         BindingTracker var3 = this.trackers.get(var2);
         if (var3 == null) {
            var3 = new BindingTracker(this.context, this.maxRank, var2);
            BindingTracker var4 = this.trackers.putIfAbsent(var2, var3);
            if (var4 != null) {
               var3 = var4;
            }
         }

         var3.subscribe(var1);
      }
   }

   @Override
   public <T> void unsubscribe(BindingSubscriber<T> var1) {
      String var2 = var1.type().getRawType().getName();
      BindingTracker var3 = this.trackers.get(var2);
      if (var3 != null) {
         var3.unsubscribe(var1);
      }
   }

   @Override
   public int maxBindingRank() {
      return this.maxRank;
   }

   private boolean shouldTrack(String var1) {
      for (Pattern var2 : this.allowed) {
         if (var2.matcher(var1).matches()) {
            for (Pattern var6 : this.ignored) {
               if (var6.matcher(var1).matches()) {
                  return false;
               }
            }

            return true;
         }
      }

      return false;
   }

   private static Pattern[] parseGlobs(String var0) {
      ArrayList var1 = new ArrayList();

      String[] var5;
      for (String var2 : var5 = var0.split("\\s*,\\s*")) {
         if (GLOB_SYNTAX.matcher(var2).matches()) {
            var1.add(Pattern.compile(var2.replace(".", "\\.").replace("*", ".*")));
         } else if (var2.length() > 0) {
            Logs.warn("Ignoring malformed glob pattern: {}", var2, null);
         }
      }

      return var1.toArray(new Pattern[var1.size()]);
   }
}
