package com.sun.jna.ptr;

import com.sun.jna.Pointer;

public class PointerByReference extends ByReference {
   public PointerByReference() {
      this(null);
   }

   public PointerByReference(Pointer var1) {
      super(Pointer.SIZE);
      this.setValue(var1);
   }

   public void setValue(Pointer var1) {
      this.getPointer().setPointer(0L, var1);
   }

   public Pointer getValue() {
      return this.getPointer().getPointer(0L);
   }
}
