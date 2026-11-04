package com.google.gson;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JsonArray extends JsonElement implements Iterable<JsonElement> {
   private final List<JsonElement> elements;

   public JsonArray() {
      this.elements = new ArrayList<>();
   }

   public JsonArray(int var1) {
      this.elements = new ArrayList<>(var1);
   }

   public JsonArray deepCopy() {
      if (this.elements.isEmpty()) {
         return new JsonArray();
      } else {
         JsonArray var1 = new JsonArray(this.elements.size());

         for (JsonElement var3 : this.elements) {
            var1.add(var3.deepCopy());
         }

         return var1;
      }
   }

   public void add(Boolean var1) {
      this.elements.add((JsonElement)(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1)));
   }

   public void add(Character var1) {
      this.elements.add((JsonElement)(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1)));
   }

   public void add(Number var1) {
      this.elements.add((JsonElement)(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1)));
   }

   public void add(String var1) {
      this.elements.add((JsonElement)(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1)));
   }

   public void add(JsonElement var1) {
      if (var1 == null) {
         var1 = JsonNull.INSTANCE;
      }

      this.elements.add((JsonElement)var1);
   }

   public void addAll(JsonArray var1) {
      this.elements.addAll(var1.elements);
   }

   public JsonElement set(int var1, JsonElement var2) {
      return this.elements.set(var1, var2);
   }

   public boolean remove(JsonElement var1) {
      return this.elements.remove(var1);
   }

   public JsonElement remove(int var1) {
      return this.elements.remove(var1);
   }

   public boolean contains(JsonElement var1) {
      return this.elements.contains(var1);
   }

   public int size() {
      return this.elements.size();
   }

   public boolean isEmpty() {
      return this.elements.isEmpty();
   }

   @Override
   public Iterator<JsonElement> iterator() {
      return this.elements.iterator();
   }

   public JsonElement get(int var1) {
      return this.elements.get(var1);
   }

   @Override
   public Number getAsNumber() {
      if (this.elements.size() == 1) {
         return this.elements.get(0).getAsNumber();
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public String getAsString() {
      if (this.elements.size() == 1) {
         return this.elements.get(0).getAsString();
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public double getAsDouble() {
      if (this.elements.size() == 1) {
         return this.elements.get(0).getAsDouble();
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public BigDecimal getAsBigDecimal() {
      if (this.elements.size() == 1) {
         return this.elements.get(0).getAsBigDecimal();
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public BigInteger getAsBigInteger() {
      if (this.elements.size() == 1) {
         return this.elements.get(0).getAsBigInteger();
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public float getAsFloat() {
      if (this.elements.size() == 1) {
         return this.elements.get(0).getAsFloat();
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public long getAsLong() {
      if (this.elements.size() == 1) {
         return this.elements.get(0).getAsLong();
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public int getAsInt() {
      if (this.elements.size() == 1) {
         return this.elements.get(0).getAsInt();
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public byte getAsByte() {
      if (this.elements.size() == 1) {
         return this.elements.get(0).getAsByte();
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public char getAsCharacter() {
      if (this.elements.size() == 1) {
         return this.elements.get(0).getAsCharacter();
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public short getAsShort() {
      if (this.elements.size() == 1) {
         return this.elements.get(0).getAsShort();
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public boolean getAsBoolean() {
      if (this.elements.size() == 1) {
         return this.elements.get(0).getAsBoolean();
      } else {
         throw new IllegalStateException();
      }
   }

   @Override
   public boolean equals(Object var1) {
      return var1 == this || var1 instanceof JsonArray && ((JsonArray)var1).elements.equals(this.elements);
   }

   @Override
   public int hashCode() {
      return this.elements.hashCode();
   }
}
