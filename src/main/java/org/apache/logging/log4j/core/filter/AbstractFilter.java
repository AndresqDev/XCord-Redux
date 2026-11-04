package org.apache.logging.log4j.core.filter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.message.Message;

public abstract class AbstractFilter extends AbstractLifeCycle implements Filter {
   protected final Filter.Result onMatch;
   protected final Filter.Result onMismatch;

   protected AbstractFilter() {
      this(null, null);
   }

   protected AbstractFilter(Filter.Result var1, Filter.Result var2) {
      this.onMatch = var1 == null ? Filter.Result.NEUTRAL : var1;
      this.onMismatch = var2 == null ? Filter.Result.DENY : var2;
   }

   @Override
   protected boolean equalsImpl(Object var1) {
      if (this == var1) {
         return true;
      } else if (!super.equalsImpl(var1)) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         AbstractFilter var2 = (AbstractFilter)var1;
         return this.onMatch != var2.onMatch ? false : this.onMismatch == var2.onMismatch;
      }
   }

   @Override
   public Filter.Result filter(LogEvent var1) {
      return Filter.Result.NEUTRAL;
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      return Filter.Result.NEUTRAL;
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      return Filter.Result.NEUTRAL;
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      return Filter.Result.NEUTRAL;
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5) {
      return this.filter(var1, var2, var3, var4, var5);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      return this.filter(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @Override
   public Filter.Result filter(
      Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
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
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
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
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
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
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
   }

   @Override
   public final Filter.Result getOnMatch() {
      return this.onMatch;
   }

   @Override
   public final Filter.Result getOnMismatch() {
      return this.onMismatch;
   }

   @Override
   protected int hashCodeImpl() {
      byte var1 = 31;
      int var2 = super.hashCodeImpl();
      var2 = 31 * var2 + (this.onMatch == null ? 0 : this.onMatch.hashCode());
      return 31 * var2 + (this.onMismatch == null ? 0 : this.onMismatch.hashCode());
   }

   @Override
   public String toString() {
      return this.getClass().getSimpleName();
   }

   public abstract static class AbstractFilterBuilder<B extends AbstractFilter.AbstractFilterBuilder<B>> {
      public static final String ATTR_ON_MISMATCH = "onMismatch";
      public static final String ATTR_ON_MATCH = "onMatch";
      @PluginBuilderAttribute("onMatch")
      private Filter.Result onMatch = Filter.Result.NEUTRAL;
      @PluginBuilderAttribute("onMismatch")
      private Filter.Result onMismatch = Filter.Result.DENY;

      public Filter.Result getOnMatch() {
         return this.onMatch;
      }

      public Filter.Result getOnMismatch() {
         return this.onMismatch;
      }

      public B setOnMatch(Filter.Result var1) {
         this.onMatch = var1;
         return this.asBuilder();
      }

      public B setOnMismatch(Filter.Result var1) {
         this.onMismatch = var1;
         return this.asBuilder();
      }

      public B asBuilder() {
         return (B)this;
      }
   }
}
