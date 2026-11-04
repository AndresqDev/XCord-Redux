package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.ClassUtils;

public class MultilineRecursiveToStringStyle extends RecursiveToStringStyle {
   private static final long serialVersionUID = 1L;
   private static final int INDENT = 2;
   private int spaces = 2;

   public MultilineRecursiveToStringStyle() {
      this.resetIndent();
   }

   private void resetIndent() {
      this.setArrayStart("{" + System.lineSeparator() + this.spacer(this.spaces));
      this.setArraySeparator("," + System.lineSeparator() + this.spacer(this.spaces));
      this.setArrayEnd(System.lineSeparator() + this.spacer(this.spaces - 2) + "}");
      this.setContentStart("[" + System.lineSeparator() + this.spacer(this.spaces));
      this.setFieldSeparator("," + System.lineSeparator() + this.spacer(this.spaces));
      this.setContentEnd(System.lineSeparator() + this.spacer(this.spaces - 2) + "]");
   }

   private StringBuilder spacer(int var1) {
      StringBuilder var2 = new StringBuilder();

      for (int var3 = 0; var3 < var1; var3++) {
         var2.append(" ");
      }

      return var2;
   }

   @Override
   public void appendDetail(StringBuffer var1, String var2, Object var3) {
      if (!ClassUtils.isPrimitiveWrapper(var3.getClass()) && !String.class.equals(var3.getClass()) && this.accept(var3.getClass())) {
         this.spaces += 2;
         this.resetIndent();
         var1.append(ReflectionToStringBuilder.toString(var3, this));
         this.spaces -= 2;
         this.resetIndent();
      } else {
         super.appendDetail(var1, var2, var3);
      }
   }

   @Override
   protected void appendDetail(StringBuffer var1, String var2, Object[] var3) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(var1, var2, var3);
      this.spaces -= 2;
      this.resetIndent();
   }

   @Override
   protected void reflectionAppendArrayDetail(StringBuffer var1, String var2, Object var3) {
      this.spaces += 2;
      this.resetIndent();
      super.reflectionAppendArrayDetail(var1, var2, var3);
      this.spaces -= 2;
      this.resetIndent();
   }

   @Override
   protected void appendDetail(StringBuffer var1, String var2, long[] var3) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(var1, var2, var3);
      this.spaces -= 2;
      this.resetIndent();
   }

   @Override
   protected void appendDetail(StringBuffer var1, String var2, int[] var3) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(var1, var2, var3);
      this.spaces -= 2;
      this.resetIndent();
   }

   @Override
   protected void appendDetail(StringBuffer var1, String var2, short[] var3) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(var1, var2, var3);
      this.spaces -= 2;
      this.resetIndent();
   }

   @Override
   protected void appendDetail(StringBuffer var1, String var2, byte[] var3) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(var1, var2, var3);
      this.spaces -= 2;
      this.resetIndent();
   }

   @Override
   protected void appendDetail(StringBuffer var1, String var2, char[] var3) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(var1, var2, var3);
      this.spaces -= 2;
      this.resetIndent();
   }

   @Override
   protected void appendDetail(StringBuffer var1, String var2, double[] var3) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(var1, var2, var3);
      this.spaces -= 2;
      this.resetIndent();
   }

   @Override
   protected void appendDetail(StringBuffer var1, String var2, float[] var3) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(var1, var2, var3);
      this.spaces -= 2;
      this.resetIndent();
   }

   @Override
   protected void appendDetail(StringBuffer var1, String var2, boolean[] var3) {
      this.spaces += 2;
      this.resetIndent();
      super.appendDetail(var1, var2, var3);
      this.spaces -= 2;
      this.resetIndent();
   }
}
