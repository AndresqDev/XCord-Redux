package com.sun.jna;

public final class WString implements CharSequence, Comparable {
   private String string;

   public WString(String var1) {
      if (var1 == null) {
         throw new NullPointerException("String initializer must be non-null");
      } else {
         this.string = var1;
      }
   }

   @Override
   public String toString() {
      return this.string;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof WString && this.toString().equals(var1.toString());
   }

   @Override
   public int hashCode() {
      return this.toString().hashCode();
   }

   @Override
   public int compareTo(Object var1) {
      return this.toString().compareTo(var1.toString());
   }

   @Override
   public int length() {
      return this.toString().length();
   }

   @Override
   public char charAt(int var1) {
      return this.toString().charAt(var1);
   }

   @Override
   public CharSequence subSequence(int var1, int var2) {
      return this.toString().subSequence(var1, var2);
   }
}
