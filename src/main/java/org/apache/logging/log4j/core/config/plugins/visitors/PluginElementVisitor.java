package org.apache.logging.log4j.core.config.plugins.visitors;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;

public class PluginElementVisitor extends AbstractPluginVisitor<PluginElement> {
   public PluginElementVisitor() {
      super(PluginElement.class);
   }

   @Override
   public Object visit(Configuration var1, Node var2, LogEvent var3, StringBuilder var4) {
      String var5 = this.annotation.value();
      if (!this.conversionType.isArray()) {
         Node var13 = this.findNamedNode(var5, var2.getChildren());
         if (var13 == null) {
            var4.append(var5).append("=null");
            return null;
         } else {
            var4.append(var13.getName()).append('(').append(var13.toString()).append(')');
            var2.getChildren().remove(var13);
            return var13.getObject();
         }
      } else {
         this.setConversionType(this.conversionType.getComponentType());
         ArrayList var6 = new ArrayList();
         ArrayList var7 = new ArrayList();
         var4.append("={");
         boolean var8 = true;

         for (Node var10 : var2.getChildren()) {
            PluginType var11 = var10.getType();
            if (var5.equalsIgnoreCase(var11.getElementName()) || this.conversionType.isAssignableFrom(var11.getPluginClass())) {
               if (!var8) {
                  var4.append(", ");
               }

               var8 = false;
               var7.add(var10);
               Object var12 = var10.getObject();
               if (var12 == null) {
                  LOGGER.error("Null object returned for {} in {}.", var10.getName(), var2.getName());
               } else {
                  if (var12.getClass().isArray()) {
                     var4.append(Arrays.toString((Object[])var12)).append('}');
                     var2.getChildren().removeAll(var7);
                     return var12;
                  }

                  var4.append(var10.toString());
                  var6.add(var12);
               }
            }
         }

         var4.append('}');
         if (!var6.isEmpty() && !this.conversionType.isAssignableFrom(var6.get(0).getClass())) {
            LOGGER.error(
               "Attempted to assign attribute {} to list of type {} which is incompatible with {}.", var5, var6.get(0).getClass(), this.conversionType
            );
            return null;
         } else {
            var2.getChildren().removeAll(var7);
            Object[] var14 = (Object[])Array.newInstance(this.conversionType, var6.size());

            for (int var15 = 0; var15 < var14.length; var15++) {
               var14[var15] = var6.get(var15);
            }

            return var14;
         }
      }
   }

   private Node findNamedNode(String var1, Iterable<Node> var2) {
      for (Node var4 : var2) {
         PluginType var5 = var4.getType();
         if (var5 == null) {
         }

         if (var1.equalsIgnoreCase(var5.getElementName()) || this.conversionType.isAssignableFrom(var5.getPluginClass())) {
            return var4;
         }
      }

      return null;
   }
}
