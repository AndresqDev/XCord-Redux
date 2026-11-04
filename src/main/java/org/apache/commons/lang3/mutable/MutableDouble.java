package org.apache.commons.lang3.mutable;

public class MutableDouble extends Number implements Comparable<MutableDouble>, Mutable<Number> {
   private static final long serialVersionUID = 1587163916L;
   private double value;

   public MutableDouble() {
   }

   public MutableDouble(double var1) {
      this.value = var1;
   }

   public MutableDouble(Number var1) {
      this.value = var1.doubleValue();
   }

   public MutableDouble(String var1) {
      this.value = Double.parseDouble(var1);
   }

   public Double getValue() {
      return this.value;
   }

   public void setValue(double var1) {
      this.value = var1;
   }

   public void setValue(Number var1) {
      this.value = var1.doubleValue();
   }

   public boolean isNaN() {
      return Double.isNaN(this.value);
   }

   public boolean isInfinite() {
      return Double.isInfinite(this.value);
   }

   public void increment() {
      this.value++;
   }

   public double getAndIncrement() {
      return this.value++;
   }

   public double incrementAndGet() {
      this.value++;
      return this.value;
   }

   public void decrement() {
      this.value--;
   }

   public double getAndDecrement() {
      return this.value--;
   }

   public double decrementAndGet() {
      this.value--;
      return this.value;
   }

   public void add(double var1) {
      this.value += var1;
   }

   public void add(Number var1) {
      this.value = this.value + var1.doubleValue();
   }

   public void subtract(double var1) {
      this.value -= var1;
   }

   public void subtract(Number var1) {
      this.value = this.value - var1.doubleValue();
   }

   public double addAndGet(double var1) {
      this.value += var1;
      return this.value;
   }

   public double addAndGet(Number var1) {
      this.value = this.value + var1.doubleValue();
      return this.value;
   }

   public double getAndAdd(double var1) {
      double var3 = this.value;
      this.value += var1;
      return var3;
   }

   public double getAndAdd(Number var1) {
      double var2 = this.value;
      this.value = this.value + var1.doubleValue();
      return var2;
   }

   @Override
   public int intValue() {
      return (int)this.value;
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
      return this.value;
   }

   public Double toDouble() {
      return this.doubleValue();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof MutableDouble && Double.doubleToLongBits(((MutableDouble)var1).value) == Double.doubleToLongBits(this.value);
   }

   @Override
   public int hashCode() {
      long var1 = Double.doubleToLongBits(this.value);
      return (int)(var1 ^ var1 >>> 32);
   }

   public int compareTo(MutableDouble var1) {
      return Double.compare(this.value, var1.value);
   }

   @Override
   public String toString() {
      return String.valueOf(this.value);
   }
}
