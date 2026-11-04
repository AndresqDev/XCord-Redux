package org.apache.logging.log4j.core.config.plugins.visitors;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginValue;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.Strings;

public class PluginValueVisitor extends AbstractPluginVisitor<PluginValue> {
   public PluginValueVisitor() {
      super(PluginValue.class);
   }

   @Override
   public Object visit(Configuration var1, Node var2, LogEvent var3, StringBuilder var4) {
      String var5 = this.annotation.value();
      String var6 = var2.getValue();
      String var7 = var2.getAttributes().get(var5);
      Object var8 = null;
      if (Strings.isNotEmpty(var6)) {
         if (Strings.isNotEmpty(var7)) {
            LOGGER.error(
               "Configuration contains {} with both attribute value ({}) AND element value ({}). Please specify only one value. Using the element value.",
               var2.getName(),
               var7,
               var6
            );
         }

         var8 = var6;
      } else {
         var8 = removeAttributeValue(var2.getAttributes(), var5, new String[0]);
      }

      String var9 = this.substitutor.replace(var3, (String)var8);
      StringBuilders.appendKeyDqValue(var4, var5, var9);
      return var9;
   }
}
