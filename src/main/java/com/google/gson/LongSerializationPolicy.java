package com.google.gson;

public enum LongSerializationPolicy {
   DEFAULT {
      @Override
      public JsonElement serialize(Long var1) {
         return (JsonElement)(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1));
      }
   },
   STRING {
      @Override
      public JsonElement serialize(Long var1) {
         return (JsonElement)(var1 == null ? JsonNull.INSTANCE : new JsonPrimitive(var1.toString()));
      }
   };

   private LongSerializationPolicy() {
   }

   public abstract JsonElement serialize(Long var1);
}
