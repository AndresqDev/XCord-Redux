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
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "StringMatchFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
@PerformanceSensitive({"allocation"})
public final class StringMatchFilter extends AbstractFilter {
   public static final String ATTR_MATCH = "match";
   private final String text;

   private StringMatchFilter(String var1, Filter.Result var2, Filter.Result var3) {
      super(var2, var3);
      this.text = var1;
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      return this.filter(var1.<MessageFactory>getMessageFactory().newMessage(var4, var5).getFormattedMessage());
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      return this.filter(var1.<MessageFactory>getMessageFactory().newMessage(var4).getFormattedMessage());
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      return this.filter(var4.getFormattedMessage());
   }

   @Override
   public Filter.Result filter(LogEvent var1) {
      return this.filter(var1.getMessage().getFormattedMessage());
   }

   private Filter.Result filter(String var1) {
      return var1.contains(this.text) ? this.onMatch : this.onMismatch;
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5) {
      return this.filter(var1.<MessageFactory>getMessageFactory().newMessage(var4, var5).getFormattedMessage());
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      return this.filter(var1.<MessageFactory>getMessageFactory().newMessage(var4, var5, var6).getFormattedMessage());
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      return this.filter(var1.<MessageFactory>getMessageFactory().newMessage(var4, var5, var6, var7).getFormattedMessage());
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      return this.filter(var1.<MessageFactory>getMessageFactory().newMessage(var4, var5, var6, var7, var8).getFormattedMessage());
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.filter(var1.<MessageFactory>getMessageFactory().newMessage(var4, var5, var6, var7, var8, var9).getFormattedMessage());
   }

   @Override
   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.filter(var1.<MessageFactory>getMessageFactory().newMessage(var4, var5, var6, var7, var8, var9, var10).getFormattedMessage());
   }

   @Override
   public Filter.Result filter(
      Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11
   ) {
      return this.filter(var1.<MessageFactory>getMessageFactory().newMessage(var4, var5, var6, var7, var8, var9, var10, var11).getFormattedMessage());
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
      return this.filter(var1.<MessageFactory>getMessageFactory().newMessage(var4, var5, var6, var7, var8, var9, var10, var11, var12).getFormattedMessage());
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
      return this.filter(
         var1.<MessageFactory>getMessageFactory().newMessage(var4, var5, var6, var7, var8, var9, var10, var11, var12, var13).getFormattedMessage()
      );
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
      return this.filter(
         var1.<MessageFactory>getMessageFactory().newMessage(var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14).getFormattedMessage()
      );
   }

   @Override
   public String toString() {
      return this.text;
   }

   @PluginBuilderFactory
   public static StringMatchFilter.Builder newBuilder() {
      return new StringMatchFilter.Builder();
   }

   public static class Builder
      extends AbstractFilter.AbstractFilterBuilder<StringMatchFilter.Builder>
      implements org.apache.logging.log4j.core.util.Builder<StringMatchFilter> {
      @PluginBuilderAttribute
      private String text = "";

      public StringMatchFilter.Builder setMatchString(String var1) {
         this.text = var1;
         return this;
      }

      public StringMatchFilter build() {
         return new StringMatchFilter(this.text, this.getOnMatch(), this.getOnMismatch());
      }
   }
}
