package com.google.protobuf;

import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

final class FloatArrayList extends AbstractProtobufList<Float> implements Internal.FloatList, PrimitiveNonBoxingCollection, RandomAccess {
   private static final FloatArrayList EMPTY_LIST = new FloatArrayList(new float[0], 0);
   private float[] array;
   private int size;

   public static FloatArrayList emptyList() {
      return EMPTY_LIST;
   }

   FloatArrayList() {
      this(new float[10], 0);
   }

   private FloatArrayList(float[] var1, int var2) {
      this.array = var1;
      this.size = var2;
   }

   @Override
   protected void removeRange(int var1, int var2) {
      this.ensureIsMutable();
      if (var2 < var1) {
         throw new IndexOutOfBoundsException("toIndex < fromIndex");
      } else {
         System.arraycopy(this.array, var2, this.array, var1, this.size - var2);
         this.size -= var2 - var1;
         this.modCount++;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof FloatArrayList)) {
         return super.equals(var1);
      } else {
         FloatArrayList var2 = (FloatArrayList)var1;
         if (this.size != var2.size) {
            return false;
         } else {
            float[] var3 = var2.array;

            for (int var4 = 0; var4 < this.size; var4++) {
               if (Float.floatToIntBits(this.array[var4]) != Float.floatToIntBits(var3[var4])) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   public int hashCode() {
      int var1 = 1;

      for (int var2 = 0; var2 < this.size; var2++) {
         var1 = 31 * var1 + Float.floatToIntBits(this.array[var2]);
      }

      return var1;
   }

   @Override
   public Internal.FloatList mutableCopyWithCapacity(int var1) {
      if (var1 < this.size) {
         throw new IllegalArgumentException();
      } else {
         return new FloatArrayList(Arrays.copyOf(this.array, var1), this.size);
      }
   }

   public Float get(int var1) {
      return this.getFloat(var1);
   }

   @Override
   public float getFloat(int var1) {
      this.ensureIndexInRange(var1);
      return this.array[var1];
   }

   @Override
   public int size() {
      return this.size;
   }

   public Float set(int var1, Float var2) {
      return this.setFloat(var1, var2);
   }

   @Override
   public float setFloat(int var1, float var2) {
      this.ensureIsMutable();
      this.ensureIndexInRange(var1);
      float var3 = this.array[var1];
      this.array[var1] = var2;
      return var3;
   }

   public boolean add(Float var1) {
      this.addFloat(var1);
      return true;
   }

   public void add(int var1, Float var2) {
      this.addFloat(var1, var2);
   }

   @Override
   public void addFloat(float var1) {
      this.ensureIsMutable();
      if (this.size == this.array.length) {
         int var2 = this.size * 3 / 2 + 1;
         float[] var3 = new float[var2];
         System.arraycopy(this.array, 0, var3, 0, this.size);
         this.array = var3;
      }

      this.array[this.size++] = var1;
   }

   private void addFloat(int var1, float var2) {
      this.ensureIsMutable();
      if (var1 >= 0 && var1 <= this.size) {
         if (this.size < this.array.length) {
            System.arraycopy(this.array, var1, this.array, var1 + 1, this.size - var1);
         } else {
            int var3 = this.size * 3 / 2 + 1;
            float[] var4 = new float[var3];
            System.arraycopy(this.array, 0, var4, 0, var1);
            System.arraycopy(this.array, var1, var4, var1 + 1, this.size - var1);
            this.array = var4;
         }

         this.array[var1] = var2;
         this.size++;
         this.modCount++;
      } else {
         throw new IndexOutOfBoundsException(this.makeOutOfBoundsExceptionMessage(var1));
      }
   }

   @Override
   public boolean addAll(Collection<? extends Float> var1) {
      this.ensureIsMutable();
      Internal.checkNotNull(var1);
      if (!(var1 instanceof FloatArrayList)) {
         return super.addAll(var1);
      } else {
         FloatArrayList var2 = (FloatArrayList)var1;
         if (var2.size == 0) {
            return false;
         } else {
            int var3 = Integer.MAX_VALUE - this.size;
            if (var3 < var2.size) {
               throw new OutOfMemoryError();
            } else {
               int var4 = this.size + var2.size;
               if (var4 > this.array.length) {
                  this.array = Arrays.copyOf(this.array, var4);
               }

               System.arraycopy(var2.array, 0, this.array, this.size, var2.size);
               this.size = var4;
               this.modCount++;
               return true;
            }
         }
      }
   }

   @Override
   public boolean remove(Object var1) {
      this.ensureIsMutable();

      for (int var2 = 0; var2 < this.size; var2++) {
         if (var1.equals(this.array[var2])) {
            System.arraycopy(this.array, var2 + 1, this.array, var2, this.size - var2 - 1);
            this.size--;
            this.modCount++;
            return true;
         }
      }

      return false;
   }

   public Float remove(int var1) {
      this.ensureIsMutable();
      this.ensureIndexInRange(var1);
      float var2 = this.array[var1];
      if (var1 < this.size - 1) {
         System.arraycopy(this.array, var1 + 1, this.array, var1, this.size - var1 - 1);
      }

      this.size--;
      this.modCount++;
      return var2;
   }

   private void ensureIndexInRange(int var1) {
      if (var1 < 0 || var1 >= this.size) {
         throw new IndexOutOfBoundsException(this.makeOutOfBoundsExceptionMessage(var1));
      }
   }

   private String makeOutOfBoundsExceptionMessage(int var1) {
      return "Index:" + var1 + ", Size:" + this.size;
   }

   static {
      EMPTY_LIST.makeImmutable();
   }
}
