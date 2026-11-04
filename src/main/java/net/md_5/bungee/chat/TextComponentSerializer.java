package net.md_5.bungee.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.md_5.bungee.api.chat.TextComponent;

public class TextComponentSerializer extends BaseComponentSerializer implements JsonDeserializer<TextComponent>, JsonSerializer<TextComponent> {
   public TextComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      TextComponent var4 = new TextComponent();
      JsonObject var5 = var1.getAsJsonObject();
      if (!var5.has("text")) {
         throw new JsonParseException("Could not parse JSON: missing 'text' property");
      } else {
         var4.setText(var5.get("text").getAsString());
         this.deserialize(var5, var4, var3);
         return var4;
      }
   }

   public JsonElement serialize(TextComponent var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      this.serialize(var4, var1, var3);
      var4.addProperty("text", var1.getText());
      return var4;
   }
}
