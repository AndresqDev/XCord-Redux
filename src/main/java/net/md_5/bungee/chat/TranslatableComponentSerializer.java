package net.md_5.bungee.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Arrays;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class TranslatableComponentSerializer
   extends BaseComponentSerializer
   implements JsonDeserializer<TranslatableComponent>,
   JsonSerializer<TranslatableComponent> {
   public TranslatableComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      TranslatableComponent var4 = new TranslatableComponent();
      JsonObject var5 = var1.getAsJsonObject();
      this.deserialize(var5, var4, var3);
      if (!var5.has("translate")) {
         throw new JsonParseException("Could not parse JSON: missing 'translate' property");
      } else {
         var4.setTranslate(var5.get("translate").getAsString());
         if (var5.has("with")) {
            var4.setWith(Arrays.asList((BaseComponent[])var3.deserialize(var5.get("with"), BaseComponent[].class)));
         }

         return var4;
      }
   }

   public JsonElement serialize(TranslatableComponent var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      this.serialize(var4, var1, var3);
      var4.addProperty("translate", var1.getTranslate());
      if (var1.getWith() != null) {
         var4.add("with", var3.serialize(var1.getWith()));
      }

      return var4;
   }
}
