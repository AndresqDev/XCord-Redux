package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

public class ToStringBuilder implements Builder<String> {
   private static volatile ToStringStyle defaultStyle = ToStringStyle.DEFAULT_STYLE;
   private final StringBuffer buffer;
   private final Object object;
   private final ToStringStyle style;

   public static ToStringStyle getDefaultStyle() {
      return defaultStyle;
   }

   public static void setDefaultStyle(ToStringStyle var0) {
      Validate.isTrue(var0 != null, "The style must not be null");
      defaultStyle = var0;
   }

   public static String reflectionToString(Object var0) {
      return ReflectionToStringBuilder.toString(var0);
   }

   public static String reflectionToString(Object var0, ToStringStyle var1) {
      return ReflectionToStringBuilder.toString(var0, var1);
   }

   public static String reflectionToString(Object var0, ToStringStyle var1, boolean var2) {
      return ReflectionToStringBuilder.toString(var0, var1, var2, false, null);
   }

   public static <T> String reflectionToString(T var0, ToStringStyle var1, boolean var2, Class<? super T> var3) {
      return ReflectionToStringBuilder.toString(var0, var1, var2, false, var3);
   }

   public ToStringBuilder(Object var1) {
      this(var1, null, null);
   }

   public ToStringBuilder(Object var1, ToStringStyle var2) {
      this(var1, var2, null);
   }

   public ToStringBuilder(Object var1, ToStringStyle var2, StringBuffer var3) {
      if (var2 == null) {
         var2 = getDefaultStyle();
      }

      if (var3 == null) {
         var3 = new StringBuffer(512);
      }

      this.buffer = var3;
      this.style = var2;
      this.object = var1;
      var2.appendStart(var3, var1);
   }

   public ToStringBuilder append(boolean var1) {
      this.style.append(this.buffer, null, var1);
      return this;
   }

   public ToStringBuilder append(boolean[] var1) {
      this.style.append(this.buffer, null, var1, null);
      return this;
   }

   public ToStringBuilder append(byte var1) {
      this.style.append(this.buffer, null, var1);
      return this;
   }

   public ToStringBuilder append(byte[] var1) {
      this.style.append(this.buffer, null, var1, null);
      return this;
   }

   public ToStringBuilder append(char var1) {
      this.style.append(this.buffer, null, var1);
      return this;
   }

   public ToStringBuilder append(char[] var1) {
      this.style.append(this.buffer, null, var1, null);
      return this;
   }

   public ToStringBuilder append(double var1) {
      this.style.append(this.buffer, null, var1);
      return this;
   }

   public ToStringBuilder append(double[] var1) {
      this.style.append(this.buffer, null, var1, null);
      return this;
   }

   public ToStringBuilder append(float var1) {
      this.style.append(this.buffer, null, var1);
      return this;
   }

   public ToStringBuilder append(float[] var1) {
      this.style.append(this.buffer, null, var1, null);
      return this;
   }

   public ToStringBuilder append(int var1) {
      this.style.append(this.buffer, null, var1);
      return this;
   }

   public ToStringBuilder append(int[] var1) {
      this.style.append(this.buffer, null, var1, null);
      return this;
   }

   public ToStringBuilder append(long var1) {
      this.style.append(this.buffer, null, var1);
      return this;
   }

   public ToStringBuilder append(long[] var1) {
      this.style.append(this.buffer, null, var1, null);
      return this;
   }

   public ToStringBuilder append(Object var1) {
      this.style.append(this.buffer, null, var1, null);
      return this;
   }

   public ToStringBuilder append(Object[] var1) {
      this.style.append(this.buffer, null, var1, null);
      return this;
   }

   public ToStringBuilder append(short var1) {
      this.style.append(this.buffer, null, var1);
      return this;
   }

   public ToStringBuilder append(short[] var1) {
      this.style.append(this.buffer, null, var1, null);
      return this;
   }

   public ToStringBuilder append(String var1, boolean var2) {
      this.style.append(this.buffer, var1, var2);
      return this;
   }

   public ToStringBuilder append(String var1, boolean[] var2) {
      this.style.append(this.buffer, var1, var2, null);
      return this;
   }

   public ToStringBuilder append(String var1, boolean[] var2, boolean var3) {
      this.style.append(this.buffer, var1, var2, var3);
      return this;
   }

   public ToStringBuilder append(String var1, byte var2) {
      this.style.append(this.buffer, var1, var2);
      return this;
   }

   public ToStringBuilder append(String var1, byte[] var2) {
      this.style.append(this.buffer, var1, var2, null);
      return this;
   }

   public ToStringBuilder append(String var1, byte[] var2, boolean var3) {
      this.style.append(this.buffer, var1, var2, var3);
      return this;
   }

   public ToStringBuilder append(String var1, char var2) {
      this.style.append(this.buffer, var1, var2);
      return this;
   }

   public ToStringBuilder append(String var1, char[] var2) {
      this.style.append(this.buffer, var1, var2, null);
      return this;
   }

   public ToStringBuilder append(String var1, char[] var2, boolean var3) {
      this.style.append(this.buffer, var1, var2, var3);
      return this;
   }

   public ToStringBuilder append(String var1, double var2) {
      this.style.append(this.buffer, var1, var2);
      return this;
   }

   public ToStringBuilder append(String var1, double[] var2) {
      this.style.append(this.buffer, var1, var2, null);
      return this;
   }

   public ToStringBuilder append(String var1, double[] var2, boolean var3) {
      this.style.append(this.buffer, var1, var2, var3);
      return this;
   }

   public ToStringBuilder append(String var1, float var2) {
      this.style.append(this.buffer, var1, var2);
      return this;
   }

   public ToStringBuilder append(String var1, float[] var2) {
      this.style.append(this.buffer, var1, var2, null);
      return this;
   }

   public ToStringBuilder append(String var1, float[] var2, boolean var3) {
      this.style.append(this.buffer, var1, var2, var3);
      return this;
   }

   public ToStringBuilder append(String var1, int var2) {
      this.style.append(this.buffer, var1, var2);
      return this;
   }

   public ToStringBuilder append(String var1, int[] var2) {
      this.style.append(this.buffer, var1, var2, null);
      return this;
   }

   public ToStringBuilder append(String var1, int[] var2, boolean var3) {
      this.style.append(this.buffer, var1, var2, var3);
      return this;
   }

   public ToStringBuilder append(String var1, long var2) {
      this.style.append(this.buffer, var1, var2);
      return this;
   }

   public ToStringBuilder append(String var1, long[] var2) {
      this.style.append(this.buffer, var1, var2, null);
      return this;
   }

   public ToStringBuilder append(String var1, long[] var2, boolean var3) {
      this.style.append(this.buffer, var1, var2, var3);
      return this;
   }

   public ToStringBuilder append(String var1, Object var2) {
      this.style.append(this.buffer, var1, var2, null);
      return this;
   }

   public ToStringBuilder append(String var1, Object var2, boolean var3) {
      this.style.append(this.buffer, var1, var2, var3);
      return this;
   }

   public ToStringBuilder append(String var1, Object[] var2) {
      this.style.append(this.buffer, var1, var2, null);
      return this;
   }

   public ToStringBuilder append(String var1, Object[] var2, boolean var3) {
      this.style.append(this.buffer, var1, var2, var3);
      return this;
   }

   public ToStringBuilder append(String var1, short var2) {
      this.style.append(this.buffer, var1, var2);
      return this;
   }

   public ToStringBuilder append(String var1, short[] var2) {
      this.style.append(this.buffer, var1, var2, null);
      return this;
   }

   public ToStringBuilder append(String var1, short[] var2, boolean var3) {
      this.style.append(this.buffer, var1, var2, var3);
      return this;
   }

   public ToStringBuilder appendAsObjectToString(Object var1) {
      ObjectUtils.identityToString(this.getStringBuffer(), var1);
      return this;
   }

   public ToStringBuilder appendSuper(String var1) {
      if (var1 != null) {
         this.style.appendSuper(this.buffer, var1);
      }

      return this;
   }

   public ToStringBuilder appendToString(String var1) {
      if (var1 != null) {
         this.style.appendToString(this.buffer, var1);
      }

      return this;
   }

   public Object getObject() {
      return this.object;
   }

   public StringBuffer getStringBuffer() {
      return this.buffer;
   }

   public ToStringStyle getStyle() {
      return this.style;
   }

   @Override
   public String toString() {
      if (this.getObject() == null) {
         this.getStringBuffer().append(this.getStyle().getNullText());
      } else {
         this.style.appendEnd(this.getStringBuffer(), this.getObject());
      }

      return this.getStringBuffer().toString();
   }

   public String build() {
      return this.toString();
   }
}
