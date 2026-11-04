package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Objects;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

@Plugin(
   name = "IfAll",
   category = "Core",
   printObject = true
)
public final class IfAll implements PathCondition {
   private final PathCondition[] components;

   private IfAll(PathCondition... var1) {
      this.components = Objects.requireNonNull(var1, "filters");
   }

   public PathCondition[] getDeleteFilters() {
      return this.components;
   }

   @Override
   public boolean accept(Path var1, Path var2, BasicFileAttributes var3) {
      return this.components != null && this.components.length != 0 ? accept(this.components, var1, var2, var3) : false;
   }

   public static boolean accept(PathCondition[] var0, Path var1, Path var2, BasicFileAttributes var3) {
      for (PathCondition var7 : var0) {
         if (!var7.accept(var1, var2, var3)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void beforeFileTreeWalk() {
      beforeFileTreeWalk(this.components);
   }

   public static void beforeFileTreeWalk(PathCondition[] var0) {
      for (PathCondition var4 : var0) {
         var4.beforeFileTreeWalk();
      }
   }

   @PluginFactory
   public static IfAll createAndCondition(@PluginElement("PathConditions") PathCondition... var0) {
      return new IfAll(var0);
   }

   @Override
   public String toString() {
      return "IfAll" + Arrays.toString((Object[])this.components);
   }
}
