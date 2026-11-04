package org.apache.logging.log4j.core.filter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "NoMarkerFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
@PerformanceSensitive({"allocation"})
public final class NoMarkerFilter extends AbstractFilter {
   private NoMarkerFilter(Filter.Result var1, Filter.Result var2) {
      super(var1, var2);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      return this.filter(var3);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      return this.filter(var3);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      return this.filter(var3);
   }

   @Override
   public Filter.Result filter(LogEvent var1) {
      return this.filter(var1.getMarker());
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5) {
      return this.filter(var3);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      return this.filter(var3);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      return this.filter(var3);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      return this.filter(var3);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.filter(var3);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.filter(var3);
   }

   @Override
   public Filter.Result filter(
      Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      return this.filter(var3);
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
      return this.filter(var3);
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
      return this.filter(var3);
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
      return this.filter(var3);
   }

   private Filter.Result filter(Marker var1) {
      return null == var1 ? this.onMatch : this.onMismatch;
   }

   @PluginBuilderFactory
   public static NoMarkerFilter.Builder newBuilder() {
      return new NoMarkerFilter.Builder();
   }

   public static class Builder
      extends AbstractFilter.AbstractFilterBuilder<NoMarkerFilter.Builder>
      implements org.apache.logging.log4j.core.util.Builder<NoMarkerFilter> {
      public NoMarkerFilter build() {
         return new NoMarkerFilter(this.getOnMatch(), this.getOnMismatch());
      }
   }
}
