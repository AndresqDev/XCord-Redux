package org.apache.commons.lang3.mutable;

import java.io.Serializable;
import org.apache.commons.lang3.BooleanUtils;

public class MutableBoolean implements Serializable, Comparable<MutableBoolean>, Mutable<Boolean> {
   private static final long serialVersionUID = -4830728138360036487L;
   private boolean value;

   public MutableBoolean() {
   }

   public MutableBoolean(boolean var1) {
      this.value = var1;
   }

   public MutableBoolean(Boolean var1) {
      this.value = var1;
   }

   public Boolean getValue() {
      return this.value;
   }

   public void setValue(boolean var1) {
      this.value = var1;
   }

   public void setFalse() {
      this.value = false;
   }

   public void setTrue() {
      this.value = true;
   }

   public void setValue(Boolean var1) {
      this.value = var1;
   }

   public boolean isTrue() {
      return this.value;
   }

   public boolean isFalse() {
      return !this.value;
   }

   public boolean booleanValue() {
      return this.value;
   }

   public Boolean toBoolean() {
      return this.booleanValue();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof MutableBoolean ? this.value == ((MutableBoolean)var1).booleanValue() : false;
   }

   @Override
   public int hashCode() {
      return this.value ? Boolean.TRUE.hashCode() : Boolean.FALSE.hashCode();
   }

   public int compareTo(MutableBoolean var1) {
      return BooleanUtils.compare(this.value, var1.value);
   }

   @Override
   public String toString() {
      return String.valueOf(this.value);
   }
}
