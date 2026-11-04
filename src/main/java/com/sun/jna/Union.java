package com.sun.jna;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Union extends Structure {
   private Structure.StructField activeField;

   protected Union() {
   }

   protected Union(Pointer var1) {
      super(var1);
   }

   protected Union(Pointer var1, int var2) {
      super(var1, var2);
   }

   protected Union(TypeMapper var1) {
      super(var1);
   }

   protected Union(Pointer var1, int var2, TypeMapper var3) {
      super(var1, var2, var3);
   }

   @Override
   protected List<String> getFieldOrder() {
      List var1 = this.getFieldList();
      ArrayList var2 = new ArrayList(var1.size());

      for (Field var4 : var1) {
         var2.add(var4.getName());
      }

      return var2;
   }

   public void setType(Class<?> var1) {
      this.ensureAllocated();

      for (Structure.StructField var3 : this.fields().values()) {
         if (var3.type == var1) {
            this.activeField = var3;
            return;
         }
      }

      throw new IllegalArgumentException("No field of type " + var1 + " in " + this);
   }

   public void setType(String var1) {
      this.ensureAllocated();
      Structure.StructField var2 = this.fields().get(var1);
      if (var2 != null) {
         this.activeField = var2;
      } else {
         throw new IllegalArgumentException("No field named " + var1 + " in " + this);
      }
   }

   @Override
   public Object readField(String var1) {
      this.ensureAllocated();
      this.setType(var1);
      return super.readField(var1);
   }

   @Override
   public void writeField(String var1) {
      this.ensureAllocated();
      this.setType(var1);
      super.writeField(var1);
   }

   @Override
   public void writeField(String var1, Object var2) {
      this.ensureAllocated();
      this.setType(var1);
      super.writeField(var1, var2);
   }

   public Object getTypedValue(Class<?> var1) {
      this.ensureAllocated();

      for (Structure.StructField var3 : this.fields().values()) {
         if (var3.type == var1) {
            this.activeField = var3;
            this.read();
            return this.getFieldValue(this.activeField.field);
         }
      }

      throw new IllegalArgumentException("No field of type " + var1 + " in " + this);
   }

   public Object setTypedValue(Object var1) {
      Structure.StructField var2 = this.findField(var1.getClass());
      if (var2 != null) {
         this.activeField = var2;
         this.setFieldValue(var2.field, var1);
         return this;
      } else {
         throw new IllegalArgumentException("No field of type " + var1.getClass() + " in " + this);
      }
   }

   private Structure.StructField findField(Class<?> var1) {
      this.ensureAllocated();

      for (Structure.StructField var3 : this.fields().values()) {
         if (var3.type.isAssignableFrom(var1)) {
            return var3;
         }
      }

      return null;
   }

   @Override
   protected void writeField(Structure.StructField var1) {
      if (var1 == this.activeField) {
         super.writeField(var1);
      }
   }

   @Override
   protected Object readField(Structure.StructField var1) {
      return var1 != this.activeField
            && (Structure.class.isAssignableFrom(var1.type) || String.class.isAssignableFrom(var1.type) || WString.class.isAssignableFrom(var1.type))
         ? null
         : super.readField(var1);
   }

   @Override
   protected int getNativeAlignment(Class<?> var1, Object var2, boolean var3) {
      return super.getNativeAlignment(var1, var2, true);
   }
}
