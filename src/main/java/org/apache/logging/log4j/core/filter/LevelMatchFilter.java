package org.apache.logging.log4j.core.filter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "LevelMatchFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
@PerformanceSensitive({"allocation"})
public final class LevelMatchFilter extends AbstractFilter {
   public static final String ATTR_MATCH = "match";
   private final Level level;

   private LevelMatchFilter(Level var1, Filter.Result var2, Filter.Result var3) {
      super(var2, var3);
      this.level = var1;
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      return this.filter(var2);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      return this.filter(var2);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      return this.filter(var2);
   }

   @Override
   public Filter.Result filter(LogEvent var1) {
      return this.filter(var1.getLevel());
   }

   private Filter.Result filter(Level var1) {
      return var1 == this.level ? this.onMatch : this.onMismatch;
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5) {
      return this.filter(var2);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      return this.filter(var2);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      return this.filter(var2);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      return this.filter(var2);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.filter(var2);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.filter(var2);
   }

   @Override
   public Filter.Result filter(
      Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      return this.filter(var2);
   }

   @Override
   public Filter.Result filter(
      Logger var1,
      Level var2,
      Marker var3,
      String var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12
   ) {
      return this.filter(var2);
   }

   @Override
   public Filter.Result filter(
      Logger var1,
      Level var2,
      Marker var3,
      String var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12,
      Object var13
   ) {
      return this.filter(var2);
   }

   @Override
   public Filter.Result filter(
      Logger var1,
      Level var2,
      Marker var3,
      String var4,
      Object var5,
      Object var6,
      Object var7,
      Object var8,
      Object var9,
      Object var10,
      Object var11,
      Object var12,
      Object var13,
      Object var14
   ) {
      return this.filter(var2);
   }

   @Override
   public String toString() {
      return this.level.toString();
   }

   @PluginBuilderFactory
   public static LevelMatchFilter.Builder newBuilder() {
      return new LevelMatchFilter.Builder();
   }

   public static class Builder
      extends AbstractFilter.AbstractFilterBuilder<LevelMatchFilter.Builder>
      implements org.apache.logging.log4j.core.util.Builder<LevelMatchFilter> {
      @PluginBuilderAttribute
      private Level level = Level.ERROR;

      public LevelMatchFilter.Builder setLevel(Level var1) {
         this.level = var1;
         return this;
      }

      public LevelMatchFilter build() {
         return new LevelMatchFilter(this.level, this.getOnMatch(), this.getOnMismatch());
      }
   }
}
