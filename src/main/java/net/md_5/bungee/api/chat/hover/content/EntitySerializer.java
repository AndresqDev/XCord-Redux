package net.md_5.bungee.api.chat.hover.content;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.md_5.bungee.api.chat.BaseComponent;

public class EntitySerializer implements JsonDeserializer<Entity>, JsonSerializer<Entity> {
   public Entity deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = var1.getAsJsonObject();
      return new Entity(
         var4.has("type") ? var4.get("type").getAsString() : null,
         var4.get("id").getAsString(),
         var4.has("name") ? (BaseComponent)var3.deserialize(var4.get("name"), BaseComponent.class) : null
      );
   }

   public JsonElement serialize(Entity var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("type", var1.getType() != null ? var1.getType() : "minecraft:pig");
      var4.addProperty("id", var1.getId());
      if (var1.getName() != null) {
         var4.add("name", var3.serialize(var1.getName()));
      }

      return var4;
   }
}
