package net.md_5.bungee.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.md_5.bungee.api.chat.SelectorComponent;

public class SelectorComponentSerializer extends BaseComponentSerializer implements JsonDeserializer<SelectorComponent>, JsonSerializer<SelectorComponent> {
   public SelectorComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = var1.getAsJsonObject();
      if (!var4.has("selector")) {
         throw new JsonParseException("Could not parse JSON: missing 'selector' property");
      } else {
         SelectorComponent var5 = new SelectorComponent(var4.get("selector").getAsString());
         this.deserialize(var4, var5, var3);
         return var5;
      }
   }

   public JsonElement serialize(SelectorComponent var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      this.serialize(var4, var1, var3);
      var4.addProperty("selector", var1.getSelector());
      return var4;
   }
}
