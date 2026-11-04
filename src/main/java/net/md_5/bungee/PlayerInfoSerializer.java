package net.md_5.bungee;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.UUID;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PlayerInfoSerializer implements JsonDeserializer<ServerPing.PlayerInfo>, JsonSerializer<ServerPing.PlayerInfo> {
   private final int protocol;

   public PlayerInfoSerializer() {
      this.protocol = 5;
   }

   public PlayerInfoSerializer(int var1) {
      this.protocol = var1;
   }

   public ServerPing.PlayerInfo deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
      JsonObject var4 = var1.getAsJsonObject();
      ServerPing.PlayerInfo var5 = new ServerPing.PlayerInfo(var4.get("name").getAsString(), (UUID)null);
      String var6 = var4.get("id").getAsString();
      if (!ProtocolConstants.isBeforeOrEq(this.protocol, 4) && var6.contains("-")) {
         var5.setUniqueId(UUID.fromString(var6));
      } else {
         var5.setId(var6);
      }

      return var5;
   }

   public JsonElement serialize(ServerPing.PlayerInfo var1, Type var2, JsonSerializationContext var3) {
      JsonObject var4 = new JsonObject();
      var4.addProperty("name", var1.getName());
      if (ProtocolConstants.isBeforeOrEq(this.protocol, 4)) {
         var4.addProperty("id", var1.getId());
      } else {
         var4.addProperty("id", var1.getUniqueId().toString());
      }

      return var4;
   }
}
