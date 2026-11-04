package org.apache.logging.log4j.core.lookup;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(
   name = "jvmrunargs",
   category = "Lookup"
)
public class JmxRuntimeInputArgumentsLookup extends MapLookup {
   public static final JmxRuntimeInputArgumentsLookup JMX_SINGLETON;

   public JmxRuntimeInputArgumentsLookup() {
   }

   public JmxRuntimeInputArgumentsLookup(Map<String, String> var1) {
      super(var1);
   }

   @Override
   public String lookup(LogEvent var1, String var2) {
      return this.lookup(var2);
   }

   @Override
   public String lookup(String var1) {
      if (var1 == null) {
         return null;
      } else {
         Map var2 = this.getMap();
         return var2 == null ? null : (String)var2.get(var1);
      }
   }

   static {
      List var0 = ManagementFactory.getRuntimeMXBean().getInputArguments();
      JMX_SINGLETON = new JmxRuntimeInputArgumentsLookup(MapLookup.toMap(var0));
   }
}
