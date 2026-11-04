package org.codehaus.plexus.interpolation.reflection;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.codehaus.plexus.interpolation.util.StringUtils;

public class ReflectionValueExtractor {
   private static final Class<?>[] CLASS_ARGS = new Class[0];
   private static final Object[] OBJECT_ARGS = new Object[0];
   private static final Map<Class<?>, WeakReference<ClassMap>> classMaps = new WeakHashMap<>();
   static final int EOF = -1;
   static final char PROPERTY_START = '.';
   static final char INDEXED_START = '[';
   static final char INDEXED_END = ']';
   static final char MAPPED_START = '(';
   static final char MAPPED_END = ')';

   private ReflectionValueExtractor() {
   }

   public static Object evaluate(String var0, Object var1) throws Exception {
      return evaluate(var0, var1, true);
   }

   public static Object evaluate(String var0, Object var1, boolean var2) throws Exception {
      Object var3 = var1;
      if (var0 != null && !"".equals(var0.trim()) && Character.isJavaIdentifierStart(var0.charAt(0))) {
         boolean var4 = var0.indexOf(46) >= 0;
         ReflectionValueExtractor.Tokenizer var5;
         if (var2 && var4) {
            var5 = new ReflectionValueExtractor.Tokenizer(var0);
            var5.nextPropertyName();
            if (var5.getPosition() == -1) {
               return null;
            }
         } else {
            var5 = new ReflectionValueExtractor.Tokenizer("." + var0);
         }

         int var6 = var5.getPosition();

         while (var3 != null && var5.peekChar() != -1) {
            switch (var5.skipChar()) {
               case 40:
                  var3 = getMappedValue(var0, var6, var5.getPosition(), var3, var5.nextToken(')'));
                  break;
               case 46:
                  var6 = var5.getPosition();
                  var3 = getPropertyValue(var3, var5.nextPropertyName());
                  break;
               case 91:
                  var3 = getIndexedValue(var0, var6, var5.getPosition(), var3, var5.nextToken(']'));
                  break;
               default:
                  return null;
            }
         }

         return var3;
      } else {
         return null;
      }
   }

   private static Object getMappedValue(String var0, int var1, int var2, Object var3, String var4) throws Exception {
      if (var3 == null || var4 == null) {
         return null;
      } else if (var3 instanceof Map) {
         Object[] var8 = new Object[]{var4};
         ClassMap var6 = getClassMap(var3.getClass());
         Method var7 = var6.findMethod("get", var8);
         return var7.invoke(var3, var8);
      } else {
         String var5 = String.format(
            "The token '%s' at position '%d' refers to a java.util.Map, but the value seems is an instance of '%s'",
            var0.subSequence(var1, var2),
            var1,
            var3.getClass()
         );
         throw new Exception(var5);
      }
   }

   private static Object getIndexedValue(String var0, int var1, int var2, Object var3, String var4) throws Exception {
      try {
         int var5 = Integer.parseInt(var4);
         if (var3.getClass().isArray()) {
            return Array.get(var3, var5);
         }

         if (var3 instanceof List) {
            ClassMap var6 = getClassMap(var3.getClass());
            Object[] var7 = new Object[]{var5};
            Method var8 = var6.findMethod("get", var7);
            return var8.invoke(var3, var7);
         }
      } catch (NumberFormatException var9) {
         return null;
      } catch (InvocationTargetException var10) {
         if (var10.getCause() instanceof IndexOutOfBoundsException) {
            return null;
         }

         throw var10;
      }

      String var11 = String.format(
         "The token '%s' at position '%d' refers to a java.util.List or an array, but the value seems is an instance of '%s'",
         var0.subSequence(var1, var2),
         var1,
         var3.getClass()
      );
      throw new Exception(var11);
   }

   private static Object getPropertyValue(Object var0, String var1) throws Exception {
      if (var0 != null && var1 != null) {
         ClassMap var2 = getClassMap(var0.getClass());
         String var3 = StringUtils.capitalizeFirstLetter(var1);
         String var4 = "get" + var3;
         Method var5 = var2.findMethod(var4, CLASS_ARGS);
         if (var5 == null) {
            var4 = "is" + var3;
            var5 = var2.findMethod(var4, CLASS_ARGS);
         }

         if (var5 == null) {
            return null;
         } else {
            try {
               return var5.invoke(var0, OBJECT_ARGS);
            } catch (InvocationTargetException var7) {
               throw var7;
            }
         }
      } else {
         return null;
      }
   }

   private static ClassMap getClassMap(Class<?> var0) {
      WeakReference var1 = classMaps.get(var0);
      ClassMap var2;
      if (var1 == null || (var2 = (ClassMap)var1.get()) == null) {
         var2 = new ClassMap(var0);
         classMaps.put(var0, new WeakReference<>(var2));
      }

      return var2;
   }

   static class Tokenizer {
      final String expression;
      int idx;

      public Tokenizer(String var1) {
         this.expression = var1;
      }

      public int peekChar() {
         return this.idx < this.expression.length() ? this.expression.charAt(this.idx) : -1;
      }

      public int skipChar() {
         return this.idx < this.expression.length() ? this.expression.charAt(this.idx++) : -1;
      }

      public String nextToken(char var1) {
         int var2 = this.idx;

         while (this.idx < this.expression.length() && var1 != this.expression.charAt(this.idx)) {
            this.idx++;
         }

         return this.idx > var2 && this.idx < this.expression.length() ? this.expression.substring(var2, this.idx++) : null;
      }

      public String nextPropertyName() {
         int var1 = this.idx;

         while (this.idx < this.expression.length() && Character.isJavaIdentifierPart(this.expression.charAt(this.idx))) {
            this.idx++;
         }

         return this.idx > var1 && this.idx <= this.expression.length() ? this.expression.substring(var1, this.idx) : null;
      }

      public int getPosition() {
         return this.idx < this.expression.length() ? this.idx : -1;
      }

      @Override
      public String toString() {
         return this.idx < this.expression.length() ? this.expression.substring(this.idx) : "<EOF>";
      }
   }
}
