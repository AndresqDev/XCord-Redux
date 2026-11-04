package org.apache.logging.log4j.core.config.plugins.visitors;

import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.util.StringBuilders;

public class PluginAttributeVisitor extends AbstractPluginVisitor<PluginAttribute> {
   public PluginAttributeVisitor() {
      super(PluginAttribute.class);
   }

   @Override
   public Object visit(Configuration var1, Node var2, LogEvent var3, StringBuilder var4) {
      String var5 = this.annotation.value();
      Map var6 = var2.getAttributes();
      String var7 = removeAttributeValue(var6, var5, this.aliases);
      String var8 = this.substitutor.replace(var3, var7);
      Object var9 = this.findDefaultValue(var3);
      Object var10 = this.convert(var8, var9);
      Object var11 = this.annotation.sensitive() ? "*****" : var10;
      StringBuilders.appendKeyDqValue(var4, var5, var11);
      return var10;
   }

   private Object findDefaultValue(LogEvent var1) {
      if (this.conversionType == int.class || this.conversionType == Integer.class) {
         return this.annotation.defaultInt();
      } else if (this.conversionType == long.class || this.conversionType == Long.class) {
         return this.annotation.defaultLong();
      } else if (this.conversionType == boolean.class || this.conversionType == Boolean.class) {
         return this.annotation.defaultBoolean();
      } else if (this.conversionType == float.class || this.conversionType == Float.class) {
         return this.annotation.defaultFloat();
      } else if (this.conversionType == double.class || this.conversionType == Double.class) {
         return this.annotation.defaultDouble();
      } else if (this.conversionType == byte.class || this.conversionType == Byte.class) {
         return this.annotation.defaultByte();
      } else if (this.conversionType == char.class || this.conversionType == Character.class) {
         return this.annotation.defaultChar();
      } else if (this.conversionType == short.class || this.conversionType == Short.class) {
         return this.annotation.defaultShort();
      } else {
         return this.conversionType == Class.class ? this.annotation.defaultClass() : this.substitutor.replace(var1, this.annotation.defaultString());
      }
   }
}
