package com.sun.jna.ptr;

public class DoubleByReference extends ByReference {
   public DoubleByReference() {
      this(0.0);
   }

   public DoubleByReference(double var1) {
      super(8);
      this.setValue(var1);
   }

   public void setValue(double var1) {
      this.getPointer().setDouble(0L, var1);
   }

   public double getValue() {
      return this.getPointer().getDouble(0L);
   }
}
