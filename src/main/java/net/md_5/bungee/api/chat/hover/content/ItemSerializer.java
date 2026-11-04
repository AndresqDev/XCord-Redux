package net.md_5.bungee.api.chat.hover.content;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.md_5.bungee.api.chat.ItemTag;

public class ItemSerializer implements JsonDeserializer<Item>, JsonSerializer<Item> {
   public Item deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = var1.getAsJsonObject();
      int var5 = -1;
      if (var4.has("Count")) {
         JsonPrimitive var6 = var4.get("Count").getAsJsonPrimitive();
         if (var6.isNumber()) {
            var5 = var6.getAsInt();
         } else if (var6.isString()) {
            String var7 = var6.getAsString();
            char var8 = var7.charAt(var7.length() - 1);
            if (var8 == 'b' || var8 == 's' || var8 == 'l' || var8 == 'f' || var8 == 'd') {
               var7 = var7.substring(0, var7.length() - 1);
            }

            try {
               var5 = Integer.parseInt(var7);
            } catch (NumberFormatException var10) {
               throw new JsonParseException("Could not parse count: " + var10);
            }
         }
      }

      return new Item(
         var4.has("id") ? var4.get("id").getAsString() : null, var5, var4.has("tag") ? (ItemTag)var3.deserialize(var4.get("tag"), ItemTag.class) : null
      );
   }

   public JsonElement serialize(Item var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("id", var1.getId() == null ? "minecraft:air" : var1.getId());
      if (var1.getCount() != -1) {
         var4.addProperty("Count", var1.getCount());
      }

      if (var1.getTag() != null) {
         var4.add("tag", var3.serialize(var1.getTag()));
      }

      return var4;
   }
}
