package org.apache.commons.lang3.mutable;

import java.io.Serializable;

public class MutableObject<T> implements Serializable, Mutable<T> {
   private static final long serialVersionUID = 86241875189L;
   private T value;

   public MutableObject() {
   }

   public MutableObject(T var1) {
      this.value = (T)var1;
   }

   @Override
   public T getValue() {
      return this.value;
   }

   @Override
   public void setValue(T var1) {
      this.value = (T)var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this == var1) {
         return true;
      } else if (this.getClass() == var1.getClass()) {
         MutableObject var2 = (MutableObject)var1;
         return this.value.equals(var2.value);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.value == null ? 0 : this.value.hashCode();
   }

   @Override
   public String toString() {
      return this.value == null ? "null" : this.value.toString();
   }
}
