package org.slf4j.helpers;

public class FormattingTuple {
   public static FormattingTuple NULL = new FormattingTuple(null);
   private String message;
   private Throwable throwable;
   private Object[] argArray;

   public FormattingTuple(String var1) {
      this(var1, null, null);
   }

   public FormattingTuple(String var1, Object[] var2, Throwable var3) {
      this.message = var1;
      this.throwable = var3;
      this.argArray = var2;
   }

   public String getMessage() {
      return this.message;
   }

   public Object[] getArgArray() {
      return this.argArray;
   }

   public Throwable getThrowable() {
      return this.throwable;
   }
}
