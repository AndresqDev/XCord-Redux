package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "IfAny",
   category = "Core",
   printObject = true
)
public final class IfAny implements PathCondition {
   private final PathCondition[] components;

   private IfAny(PathCondition... var1) {
      this.components = Objects.requireNonNull(var1, "filters");
   }

   public PathCondition[] getDeleteFilters() {
      return this.components;
   }

   @Override
   public boolean accept(Path var1, Path var2, BasicFileAttributes var3) {
      for (PathCondition var7 : this.components) {
         if (var7.accept(var1, var2, var3)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void beforeFileTreeWalk() {
      for (PathCondition var4 : this.components) {
         var4.beforeFileTreeWalk();
      }
   }

   @PluginFactory
   public static IfAny createOrCondition(@PluginElement("PathConditions") PathCondition... var0) {
      return new IfAny(var0);
   }

   @Override
   public String toString() {
      return "IfAny" + Arrays.toString((Object[])this.components);
   }
}
