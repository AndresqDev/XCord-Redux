package net.md_5.bungee.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.md_5.bungee.api.chat.KeybindComponent;

public class KeybindComponentSerializer extends BaseComponentSerializer implements JsonDeserializer<KeybindComponent>, JsonSerializer<KeybindComponent> {
   public KeybindComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = var1.getAsJsonObject();
      if (!var4.has("keybind")) {
         throw new JsonParseException("Could not parse JSON: missing 'keybind' property");
      } else {
         KeybindComponent var5 = new KeybindComponent();
         this.deserialize(var4, var5, var3);
         var5.setKeybind(var4.get("keybind").getAsString());
         return var5;
      }
   }

   public JsonElement serialize(KeybindComponent var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      this.serialize(var4, var1, var3);
      var4.addProperty("keybind", var1.getKeybind());
      return var4;
   }
}
