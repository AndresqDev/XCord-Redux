package net.md_5.bungee.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import net.md_5.bungee.api.chat.ScoreComponent;

public class ScoreComponentSerializer extends BaseComponentSerializer implements JsonDeserializer<ScoreComponent>, JsonSerializer<ScoreComponent> {
   public ScoreComponent deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = var1.getAsJsonObject();
      if (!var4.has("score")) {
         throw new JsonParseException("Could not parse JSON: missing 'score' property");
      } else {
         JsonObject var5 = var4.get("score").getAsJsonObject();
         if (var5.has("name") && var5.has("objective")) {
            String var6 = var5.get("name").getAsString();
            String var7 = var5.get("objective").getAsString();
            ScoreComponent var8 = new ScoreComponent(var6, var7);
            if (var5.has("value") && !var5.get("value").getAsString().isEmpty()) {
               var8.setValue(var5.get("value").getAsString());
            }

            this.deserialize(var4, var8, var3);
            return var8;
         } else {
            throw new JsonParseException("A score component needs at least a name and an objective");
         }
      }
   }

   public JsonElement serialize(ScoreComponent var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      this.serialize(var4, var1, var3);
      JsonObject var5 = new JsonObject();
      var5.addProperty("name", var1.getName());
      var5.addProperty("objective", var1.getObjective());
      var5.addProperty("value", var1.getValue());
      var4.add("score", var5);
      return var4;
   }
}
