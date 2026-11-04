package org.apache.logging.log4j.core.appender.rolling;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "Policies",
   category = "Core",
   printObject = true
)
public final class CompositeTriggeringPolicy extends AbstractTriggeringPolicy {
   private final TriggeringPolicy[] triggeringPolicies;

   private CompositeTriggeringPolicy(TriggeringPolicy... var1) {
      this.triggeringPolicies = var1;
   }

   public TriggeringPolicy[] getTriggeringPolicies() {
      return this.triggeringPolicies;
   }

   @Override
   public void initialize(RollingFileManager var1) {
      for (TriggeringPolicy var5 : this.triggeringPolicies) {
         LOGGER.debug("Initializing triggering policy {}", var5.toString());
         var5.initialize(var1);
      }
   }

   @Override
   public boolean isTriggeringEvent(LogEvent var1) {
      for (TriggeringPolicy var5 : this.triggeringPolicies) {
         if (var5.isTriggeringEvent(var1)) {
            return true;
         }
      }

      return false;
   }

   @PluginFactory
   public static CompositeTriggeringPolicy createPolicy(@PluginElement("Policies") TriggeringPolicy... var0) {
      return new CompositeTriggeringPolicy(var0);
   }

   @Override
   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      boolean var4 = true;

      for (TriggeringPolicy var8 : this.triggeringPolicies) {
         if (var8 instanceof LifeCycle2) {
            var4 &= ((LifeCycle2)var8).stop(var1, var3);
         } else if (var8 instanceof LifeCycle) {
            ((LifeCycle)var8).stop();
            var4 &= true;
         }
      }

      this.setStopped();
      return var4;
   }

   @Override
   public String toString() {
      return "CompositeTriggeringPolicy(policies=" + Arrays.toString((Object[])this.triggeringPolicies) + ")";
   }
}
