package org.apache.logging.log4j.core.config.plugins.visitors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.convert.TypeConverters;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public abstract class AbstractPluginVisitor<A extends Annotation> implements PluginVisitor<A> {
   protected static final Logger LOGGER = StatusLogger.getLogger();
   protected final Class<A> clazz;
   protected A annotation;
   protected String[] aliases;
   protected Class<?> conversionType;
   protected StrSubstitutor substitutor;
   protected Member member;

   protected AbstractPluginVisitor(Class<A> var1) {
      this.clazz = var1;
   }

   @Override
   public PluginVisitor<A> setAnnotation(Annotation var1) {
      Annotation var2 = Objects.requireNonNull(var1, "No annotation was provided");
      if (this.clazz.isInstance(var2)) {
         this.annotation = (A)var2;
      }

      return this;
   }

   @Override
   public PluginVisitor<A> setAliases(String... var1) {
      this.aliases = var1;
      return this;
   }

   @Override
   public PluginVisitor<A> setConversionType(Class<?> var1) {
      this.conversionType = Objects.requireNonNull(var1, "No conversion type class was provided");
      return this;
   }

   @Override
   public PluginVisitor<A> setStrSubstitutor(StrSubstitutor var1) {
      this.substitutor = Objects.requireNonNull(var1, "No StrSubstitutor was provided");
      return this;
   }

   @Override
   public PluginVisitor<A> setMember(Member var1) {
      this.member = var1;
      return this;
   }

   protected static String removeAttributeValue(Map<String, String> var0, String var1, String... var2) {
      for (Entry var4 : var0.entrySet()) {
         String var5 = (String)var4.getKey();
         String var6 = (String)var4.getValue();
         if (var5.equalsIgnoreCase(var1)) {
            var0.remove(var5);
            return var6;
         }

         if (var2 != null) {
            for (String var10 : var2) {
               if (var5.equalsIgnoreCase(var10)) {
                  var0.remove(var5);
                  return var6;
               }
            }
         }
      }

      return null;
   }

   protected Object convert(String var1, Object var2) {
      return var2 instanceof String
         ? TypeConverters.convert(var1, this.conversionType, Strings.trimToNull((String)var2))
         : TypeConverters.convert(var1, this.conversionType, var2);
   }
}
