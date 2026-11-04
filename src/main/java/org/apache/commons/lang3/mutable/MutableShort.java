package org.apache.commons.lang3.mutable;

import org.apache.commons.lang3.math.NumberUtils;

public class MutableShort extends Number implements Comparable<MutableShort>, Mutable<Number> {
   private static final long serialVersionUID = -2135791679L;
   private short value;

   public MutableShort() {
   }

   public MutableShort(short var1) {
      this.value = var1;
   }

   public MutableShort(Number var1) {
      this.value = var1.shortValue();
   }

   public MutableShort(String var1) {
      this.value = Short.parseShort(var1);
   }

   public Short getValue() {
      return this.value;
   }

   public void setValue(short var1) {
      this.value = var1;
   }

   public void setValue(Number var1) {
      this.value = var1.shortValue();
   }

   public void increment() {
      this.value++;
   }

   public short getAndIncrement() {
      return this.value++;
   }

   public short incrementAndGet() {
      this.value++;
      return this.value;
   }

   public void decrement() {
      this.value--;
   }

   public short getAndDecrement() {
      return this.value--;
   }

   public short decrementAndGet() {
      this.value--;
      return this.value;
   }

   public void add(short var1) {
      this.value += var1;
   }

   public void add(Number var1) {
      this.value = (short)(this.value + var1.shortValue());
   }

   public void subtract(short var1) {
      this.value -= var1;
   }

   public void subtract(Number var1) {
      this.value = (short)(this.value - var1.shortValue());
   }

   public short addAndGet(short var1) {
      this.value += var1;
      return this.value;
   }

   public short addAndGet(Number var1) {
      this.value = (short)(this.value + var1.shortValue());
      return this.value;
   }

   public short getAndAdd(short var1) {
      short var2 = this.value;
      this.value += var1;
      return var2;
   }

   public short getAndAdd(Number var1) {
      short var2 = this.value;
      this.value = (short)(this.value + var1.shortValue());
      return var2;
   }

   @Override
   public short shortValue() {
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

   public Short toShort() {
      return this.shortValue();
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof MutableShort ? this.value == ((MutableShort)var1).shortValue() : false;
   }

   @Override
   public int hashCode() {
      return this.value;
   }

   public int compareTo(MutableShort var1) {
      return NumberUtils.compare(this.value, var1.value);
   }

   @Override
   public String toString() {
      return String.valueOf(this.value);
   }
}
