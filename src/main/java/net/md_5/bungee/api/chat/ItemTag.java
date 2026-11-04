package net.md_5.bungee.api.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public final class ItemTag {
   private final String nbt;

   private ItemTag(String var1) {
      this.nbt = var1;
   }

   public static ItemTag ofNbt(String var0) {
      return new ItemTag(var0);
   }

   private static ItemTag.Builder builder() {
      return new ItemTag.Builder();
   }

   @Override
   public String toString() {
      return "ItemTag(nbt=" + this.getNbt() + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ItemTag)) {
         return false;
      } else {
         ItemTag var2 = (ItemTag)var1;
         String var3 = this.getNbt();
         String var4 = var2.getNbt();
         return var3 == null ? var4 == null : var3.equals(var4);
      }
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      byte var2 = 1;
      String var3 = this.getNbt();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }

   public String getNbt() {
      return this.nbt;
   }

   private static class Builder {
      private String nbt;

      Builder() {
      }

      private ItemTag.Builder nbt(String var1) {
         this.nbt = var1;
         return this;
      }

      private ItemTag build() {
         return new ItemTag(this.nbt);
      }

      @Override
      public String toString() {
         return "ItemTag.Builder(nbt=" + this.nbt + ")";
      }
   }

   public static class Serializer implements JsonDeserializer<ItemTag>, JsonSerializer<ItemTag> {
      public ItemTag deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return ItemTag.ofNbt(var1.getAsJsonPrimitive().getAsString());
      }

      public JsonElement serialize(ItemTag var1, Type var2, JsonSerializationContext var3) {
         return var3.serialize(var1.getNbt());
      }
   }
}
