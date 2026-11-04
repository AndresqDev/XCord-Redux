package org.apache.logging.log4j.core.appender.rewrite;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.util.KeyValuePair;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "MapRewritePolicy",
   category = "Core",
   elementType = "rewritePolicy",
   printObject = true
)
public final class MapRewritePolicy implements RewritePolicy {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   private final Map<String, Object> map;
   private final MapRewritePolicy.Mode mode;

   private MapRewritePolicy(Map<String, Object> var1, MapRewritePolicy.Mode var2) {
      this.map = var1;
      this.mode = var2;
   }

   @Override
   public LogEvent rewrite(LogEvent var1) {
      Message var2 = var1.getMessage();
      if (var2 != null && var2 instanceof MapMessage) {
         MapMessage var3 = (MapMessage)var2;
         HashMap var4 = new HashMap(var3.getData());
         switch (this.mode) {
            case Add:
               var4.putAll(this.map);
               break;
            default:
               for (Entry var6 : this.map.entrySet()) {
                  if (var4.containsKey(var6.getKey())) {
                     var4.put(var6.getKey(), var6.getValue());
                  }
               }
         }

         MapMessage var7 = var3.newInstance(var4);
         return new Log4jLogEvent.Builder(var1).setMessage(var7).build();
      } else {
         return var1;
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("mode=").append(this.mode);
      var1.append(" {");
      boolean var2 = true;

      for (Entry var4 : this.map.entrySet()) {
         if (!var2) {
            var1.append(", ");
         }

         var1.append((String)var4.getKey()).append('=').append(var4.getValue());
         var2 = false;
      }

      var1.append('}');
      return var1.toString();
   }

   @PluginFactory
   public static MapRewritePolicy createPolicy(@PluginAttribute("mode") String var0, @PluginElement("KeyValuePair") KeyValuePair[] var1) {
      MapRewritePolicy.Mode var2;
      var2 = var0 == null ? (var2 = MapRewritePolicy.Mode.Add) : MapRewritePolicy.Mode.valueOf(var0);
      if (var1 != null && var1.length != 0) {
         HashMap var3 = new HashMap();

         for (KeyValuePair var7 : var1) {
            String var8 = var7.getKey();
            if (var8 == null) {
               LOGGER.error("A null key is not valid in MapRewritePolicy");
            } else {
               String var9 = var7.getValue();
               if (var9 == null) {
                  LOGGER.error("A null value for key " + var8 + " is not allowed in MapRewritePolicy");
               } else {
                  var3.put(var7.getKey(), var7.getValue());
               }
            }
         }

         if (var3.isEmpty()) {
            LOGGER.error("MapRewritePolicy is not configured with any valid key value pairs");
            return null;
         } else {
            return new MapRewritePolicy(var3, var2);
         }
      } else {
         LOGGER.error("keys and values must be specified for the MapRewritePolicy");
         return null;
      }
   }

   public static enum Mode {
      Add,
      Update;
   }
}
