package org.apache.logging.log4j.core.config.plugins.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.ConstraintValidator;
import org.apache.logging.log4j.core.config.plugins.validation.ConstraintValidators;
import org.apache.logging.log4j.core.config.plugins.visitors.PluginVisitor;
import org.apache.logging.log4j.core.config.plugins.visitors.PluginVisitors;
import org.apache.logging.log4j.core.util.Builder;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.apache.logging.log4j.core.util.TypeUtil;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StringBuilders;

public class PluginBuilder implements Builder<Object> {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final PluginType<?> pluginType;
   private final Class<?> clazz;
   private Configuration configuration;
   private Node node;
   private LogEvent event;

   public PluginBuilder(PluginType<?> var1) {
      this.pluginType = var1;
      this.clazz = var1.getPluginClass();
   }

   public PluginBuilder withConfiguration(Configuration var1) {
      this.configuration = var1;
      return this;
   }

   public PluginBuilder withConfigurationNode(Node var1) {
      this.node = var1;
      return this;
   }

   public PluginBuilder forLogEvent(LogEvent var1) {
      this.event = var1;
      return this;
   }

   @Override
   public Object build() {
      this.verify();

      try {
         LOGGER.debug("Building Plugin[name={}, class={}].", this.pluginType.getElementName(), this.pluginType.getPluginClass().getName());
         Builder var1 = createBuilder(this.clazz);
         if (var1 != null) {
            this.injectFields(var1);
            return var1.build();
         }
      } catch (ConfigurationException var4) {
         LOGGER.error("Could not create plugin of type {} for element {}", this.clazz, this.node.getName(), var4);
         return null;
      } catch (Throwable var5) {
         LOGGER.error(
            "Could not create plugin of type {} for element {}: {}",
            this.clazz,
            this.node.getName(),
            (var5 instanceof InvocationTargetException ? ((InvocationTargetException)var5).getCause() : var5).toString(),
            var5
         );
      }

      try {
         Method var6 = findFactoryMethod(this.clazz);
         Object[] var2 = this.generateParameters(var6);
         return var6.invoke(null, var2);
      } catch (Throwable var3) {
         LOGGER.error(
            "Unable to invoke factory method in {} for element {}: {}",
            this.clazz,
            this.node.getName(),
            (var3 instanceof InvocationTargetException ? ((InvocationTargetException)var3).getCause() : var3).toString(),
            var3
         );
         return null;
      }
   }

   private void verify() {
      Objects.requireNonNull(this.configuration, "No Configuration object was set.");
      Objects.requireNonNull(this.node, "No Node object was set.");
   }

   private static Builder<?> createBuilder(Class<?> var0) throws InvocationTargetException, IllegalAccessException {
      for (Method var4 : var0.getDeclaredMethods()) {
         if (var4.isAnnotationPresent(PluginBuilderFactory.class)
            && Modifier.isStatic(var4.getModifiers())
            && TypeUtil.isAssignable(Builder.class, var4.getReturnType())) {
            ReflectionUtil.makeAccessible(var4);
            return (Builder<?>)var4.invoke(null);
         }
      }

      return null;
   }

   private void injectFields(Builder<?> var1) throws IllegalAccessException {
      List var2 = TypeUtil.getAllDeclaredFields(var1.getClass());
      AccessibleObject.setAccessible(var2.toArray(new Field[0]), true);
      StringBuilder var3 = new StringBuilder();
      boolean var4 = false;
      String var5 = "";

      for (Field var7 : var2) {
         var3.append(var3.length() == 0 ? simpleName(var1) + "(" : ", ");
         Annotation[] var8 = var7.getDeclaredAnnotations();
         String[] var9 = extractPluginAliases(var8);

         for (Annotation var13 : var8) {
            if (!(var13 instanceof PluginAliases)) {
               PluginVisitor var14 = PluginVisitors.findVisitor(var13.annotationType());
               if (var14 != null) {
                  Object var15 = var14.setAliases(var9)
                     .setAnnotation(var13)
                     .setConversionType(var7.getType())
                     .setStrSubstitutor(this.event == null ? this.configuration.getConfigurationStrSubstitutor() : this.configuration.getStrSubstitutor())
                     .setMember(var7)
                     .visit(this.configuration, this.node, this.event, var3);
                  if (var15 != null) {
                     var7.set(var1, var15);
                  }
               }
            }
         }

         Collection var16 = ConstraintValidators.findValidators(var8);
         Object var17 = var7.get(var1);

         for (ConstraintValidator var19 : var16) {
            if (!var19.isValid(var7.getName(), var17)) {
               var4 = true;
               if (!var5.isEmpty()) {
                  var5 = var5 + ", ";
               }

               var5 = var5 + "field '" + var7.getName() + "' has invalid value '" + var17 + "'";
            }
         }
      }

      var3.append(var3.length() == 0 ? var1.getClass().getSimpleName() + "()" : ")");
      LOGGER.debug(var3.toString());
      if (var4) {
         throw new ConfigurationException("Arguments given for element " + this.node.getName() + " are invalid: " + var5);
      } else {
         this.checkForRemainingAttributes();
         this.verifyNodeChildrenUsed();
      }
   }

   private static String simpleName(Object var0) {
      if (var0 == null) {
         return "null";
      } else {
         String var1 = var0.getClass().getName();
         int var2 = var1.lastIndexOf(46);
         return var2 < 0 ? var1 : var1.substring(var2 + 1);
      }
   }

   private static Method findFactoryMethod(Class<?> var0) {
      for (Method var4 : var0.getDeclaredMethods()) {
         if (var4.isAnnotationPresent(PluginFactory.class) && Modifier.isStatic(var4.getModifiers())) {
            ReflectionUtil.makeAccessible(var4);
            return var4;
         }
      }

      throw new IllegalStateException("No factory method found for class " + var0.getName());
   }

   private Object[] generateParameters(Method var1) {
      StringBuilder var2 = new StringBuilder();
      Class[] var3 = var1.getParameterTypes();
      Annotation[][] var4 = var1.getParameterAnnotations();
      Object[] var5 = new Object[var4.length];
      boolean var6 = false;

      for (int var7 = 0; var7 < var4.length; var7++) {
         var2.append(var2.length() == 0 ? var1.getName() + "(" : ", ");
         String[] var8 = extractPluginAliases(var4[var7]);

         for (Annotation var12 : var4[var7]) {
            if (!(var12 instanceof PluginAliases)) {
               PluginVisitor var13 = PluginVisitors.findVisitor(var12.annotationType());
               if (var13 != null) {
                  Object var14 = var13.setAliases(var8)
                     .setAnnotation(var12)
                     .setConversionType(var3[var7])
                     .setStrSubstitutor(this.event == null ? this.configuration.getConfigurationStrSubstitutor() : this.configuration.getStrSubstitutor())
                     .setMember(var1)
                     .visit(this.configuration, this.node, this.event, var2);
                  if (var14 != null) {
                     var5[var7] = var14;
                  }
               }
            }
         }

         Collection var15 = ConstraintValidators.findValidators(var4[var7]);
         Object var16 = var5[var7];
         String var17 = "arg[" + var7 + "](" + simpleName(var16) + ")";

         for (ConstraintValidator var19 : var15) {
            if (!var19.isValid(var17, var16)) {
               var6 = true;
            }
         }
      }

      var2.append(var2.length() == 0 ? var1.getName() + "()" : ")");
      this.checkForRemainingAttributes();
      this.verifyNodeChildrenUsed();
      LOGGER.debug(var2.toString());
      if (var6) {
         throw new ConfigurationException("Arguments given for element " + this.node.getName() + " are invalid");
      } else {
         return var5;
      }
   }

   private static String[] extractPluginAliases(Annotation... var0) {
      String[] var1 = null;

      for (Annotation var5 : var0) {
         if (var5 instanceof PluginAliases) {
            var1 = ((PluginAliases)var5).value();
         }
      }

      return var1;
   }

   private void checkForRemainingAttributes() {
      Map var1 = this.node.getAttributes();
      if (!var1.isEmpty()) {
         StringBuilder var2 = new StringBuilder();

         for (String var4 : var1.keySet()) {
            if (var2.length() == 0) {
               var2.append(this.node.getName());
               var2.append(" contains ");
               if (var1.size() == 1) {
                  var2.append("an invalid element or attribute ");
               } else {
                  var2.append("invalid attributes ");
               }
            } else {
               var2.append(", ");
            }

            StringBuilders.appendDqValue(var2, var4);
         }

         LOGGER.error(var2.toString());
      }
   }

   private void verifyNodeChildrenUsed() {
      List var1 = this.node.getChildren();
      if (!this.pluginType.isDeferChildren() && !var1.isEmpty()) {
         for (Node var3 : var1) {
            String var4 = this.node.getType().getElementName();
            String var5 = var4.equals(this.node.getName()) ? this.node.getName() : var4 + ' ' + this.node.getName();
            LOGGER.error("{} has no parameter that matches element {}", var5, var3.getName());
         }
      }
   }
}
