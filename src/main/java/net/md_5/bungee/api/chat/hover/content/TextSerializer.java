package net.md_5.bungee.api.chat.hover.content;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.md_5.bungee.api.chat.BaseComponent;

public class TextSerializer implements JsonDeserializer<Text>, JsonSerializer<Text> {
   public Text deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      if (var1.isJsonArray()) {
         return new Text((BaseComponent[])var3.deserialize(var1, BaseComponent[].class));
      } else {
         return var1.isJsonPrimitive()
            ? new Text(var1.getAsJsonPrimitive().getAsString())
            : new Text(new BaseComponent[]{(BaseComponent)var3.deserialize(var1, BaseComponent.class)});
      }
   }

   public JsonElement serialize(Text var1, Type var2, JsonSerializationContext var3) {
      return var3.serialize(var1.getValue());
   }
}
