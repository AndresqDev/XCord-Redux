package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

public class MutableByte extends Number implements Comparable<MutableByte>, Mutable<Number> {
   private static final long serialVersionUID = -1585823265L;
   private byte value;

   public MutableByte() {
   }

   public MutableByte(byte var1) {
      this.value = var1;
   }

   public MutableByte(Number var1) {
      this.value = var1.byteValue();
   }

   public MutableByte(String var1) {
      this.value = Byte.parseByte(var1);
   }

   public Byte getValue() {
      return this.value;
   }

   public void setValue(byte var1) {
      this.value = var1;
   }

   public void setValue(Number var1) {
      this.value = var1.byteValue();
   }

   public void increment() {
      this.value++;
   }

   public byte getAndIncrement() {
      return this.value++;
   }

   public byte incrementAndGet() {
      this.value++;
      return this.value;
   }

   public void decrement() {
      this.value--;
   }

   public byte getAndDecrement() {
      return this.value--;
   }

   public byte decrementAndGet() {
      this.value--;
      return this.value;
   }

   public void add(byte var1) {
      this.value += var1;
   }

   public void add(Number var1) {
      this.value = (byte)(this.value + var1.byteValue());
   }

   public void subtract(byte var1) {
      this.value -= var1;
   }

   public void subtract(Number var1) {
      this.value = (byte)(this.value - var1.byteValue());
   }

   public byte addAndGet(byte var1) {
      this.value += var1;
      return this.value;
   }

   public byte addAndGet(Number var1) {
      this.value = (byte)(this.value + var1.byteValue());
      return this.value;
   }

   public byte getAndAdd(byte var1) {
      byte var2 = this.value;
      this.value += var1;
      return var2;
   }

   public byte getAndAdd(Number var1) {
      byte var2 = this.value;
      this.value = (byte)(this.value + var1.byteValue());
      return var2;
   }

   @Override
   public byte byteValue() {
      return this.value;
   }

   @Override
   public int intValue() {
      return this.value;
   }

   @Override
   public long longValue() {
      return (long)this.value;
   }

   @Override
   public float floatValue() {
      return (float)this.value;
   }

   @Override
   public double doubleValue() {
      return (double)this.value;
   }

   public Byte toByte() {
      return this.byteValue();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof MutableByte ? this.value == ((MutableByte)var1).byteValue() : false;
   }

   @Override
   public int hashCode() {
      return this.value;
   }

   public int compareTo(MutableByte var1) {
      return NumberUtils.compare(this.value, var1.value);
   }

   @Override
   public String toString() {
      return String.valueOf(this.value);
   }
}
