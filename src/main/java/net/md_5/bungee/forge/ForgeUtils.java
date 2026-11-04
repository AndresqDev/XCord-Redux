package net.md_5.bungee.forge;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class ForgeUtils {
   public static Set<String> readRegisteredChannels(PluginMessage var0) {
      String var1 = new String(var0.getData(), Charsets.UTF_8);
      String[] var2 = var1.split("\u0000");
      return ImmutableSet.copyOf(var2);
   }

   public static Map<String, String> readModList(PluginMessage var0) {
      HashMap var1 = new HashMap();
      ByteBuf var2 = Unpooled.wrappedBuffer(var0.getData());
      byte var3 = var2.readByte();
      if (var3 == 2) {
         ByteBuf var4 = var2.slice();
         int var5 = DefinedPacket.readVarInt(var4, 2);

         for (int var6 = 0; var6 < var5; var6++) {
            var1.put(DefinedPacket.readString(var4), DefinedPacket.readString(var4));
         }
      }

      return var1;
   }

   public static int getFmlBuildNumber(Map<String, String> var0) {
      if (var0.containsKey("FML")) {
         String var1 = (String)var0.get("FML");
         if (var1.equals("7.10.99.99")) {
            Matcher var2 = ForgeConstants.FML_HANDSHAKE_VERSION_REGEX.matcher((CharSequence)var0.get("Forge"));
            if (var2.find()) {
               return Integer.parseInt(var2.group(4));
            }
         } else {
            Matcher var3 = ForgeConstants.FML_HANDSHAKE_VERSION_REGEX.matcher(var1);
            if (var3.find()) {
               return Integer.parseInt(var3.group(4));
            }
         }
      }

      return 0;
   }
}
