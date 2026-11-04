package com.google.gson;

import com.google.gson.internal.$Gson$Preconditions;
import com.google.gson.internal.LazilyParsedNumber;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class JsonPrimitive extends JsonElement {
   private final Object value;

   public JsonPrimitive(Boolean var1) {
      this.value = $Gson$Preconditions.checkNotNull(var1);
   }

   public JsonPrimitive(Number var1) {
      this.value = $Gson$Preconditions.checkNotNull(var1);
   }

   public JsonPrimitive(String var1) {
      this.value = $Gson$Preconditions.checkNotNull(var1);
   }

   public JsonPrimitive(Character var1) {
      this.value = $Gson$Preconditions.checkNotNull(var1).toString();
   }

   public JsonPrimitive deepCopy() {
      return this;
   }

   public boolean isBoolean() {
      return this.value instanceof Boolean;
   }

   @Override
   public boolean getAsBoolean() {
      return this.isBoolean() ? (Boolean)this.value : Boolean.parseBoolean(this.getAsString());
   }

   public boolean isNumber() {
      return this.value instanceof Number;
   }

   @Override
   public Number getAsNumber() {
      return (Number)(this.value instanceof String ? new LazilyParsedNumber((String)this.value) : (Number)this.value);
   }

   public boolean isString() {
      return this.value instanceof String;
   }

   @Override
   public String getAsString() {
      if (this.isNumber()) {
         return this.getAsNumber().toString();
      } else {
         return this.isBoolean() ? ((Boolean)this.value).toString() : (String)this.value;
      }
   }

   @Override
   public double getAsDouble() {
      return this.isNumber() ? this.getAsNumber().doubleValue() : Double.parseDouble(this.getAsString());
   }

   @Override
   public BigDecimal getAsBigDecimal() {
      return this.value instanceof BigDecimal ? (BigDecimal)this.value : new BigDecimal(this.value.toString());
   }

   @Override
   public BigInteger getAsBigInteger() {
      return this.value instanceof BigInteger ? (BigInteger)this.value : new BigInteger(this.value.toString());
   }

   @Override
   public float getAsFloat() {
      return this.isNumber() ? this.getAsNumber().floatValue() : Float.parseFloat(this.getAsString());
   }

   @Override
   public long getAsLong() {
      return this.isNumber() ? this.getAsNumber().longValue() : Long.parseLong(this.getAsString());
   }

   @Override
   public short getAsShort() {
      return this.isNumber() ? this.getAsNumber().shortValue() : Short.parseShort(this.getAsString());
   }

   @Override
   public int getAsInt() {
      return this.isNumber() ? this.getAsNumber().intValue() : Integer.parseInt(this.getAsString());
   }

   @Override
   public byte getAsByte() {
      return this.isNumber() ? this.getAsNumber().byteValue() : Byte.parseByte(this.getAsString());
   }

   @Override
   public char getAsCharacter() {
      return this.getAsString().charAt(0);
   }

   @Override
   public int hashCode() {
      if (this.value == null) {
         return 31;
      } else if (isIntegral(this)) {
         long var3 = this.getAsNumber().longValue();
         return (int)(var3 ^ var3 >>> 32);
      } else if (this.value instanceof Number) {
         long var1 = Double.doubleToLongBits(this.getAsNumber().doubleValue());
         return (int)(var1 ^ var1 >>> 32);
      } else {
         return this.value.hashCode();
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         JsonPrimitive var2 = (JsonPrimitive)var1;
         if (this.value == null) {
            return var2.value == null;
         } else if (isIntegral(this) && isIntegral(var2)) {
            return this.getAsNumber().longValue() == var2.getAsNumber().longValue();
         } else if (this.value instanceof Number && var2.value instanceof Number) {
            double var3 = this.getAsNumber().doubleValue();
            double var5 = var2.getAsNumber().doubleValue();
            return var3 == var5 || Double.isNaN(var3) && Double.isNaN(var5);
         } else {
            return this.value.equals(var2.value);
         }
      } else {
         return false;
      }
   }

   private static boolean isIntegral(JsonPrimitive var0) {
      if (!(var0.value instanceof Number)) {
         return false;
      } else {
         Number var1 = (Number)var0.value;
         return var1 instanceof BigInteger || var1 instanceof Long || var1 instanceof Integer || var1 instanceof Short || var1 instanceof Byte;
      }
   }
}
