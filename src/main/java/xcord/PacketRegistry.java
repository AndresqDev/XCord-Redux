package xcord;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

public class PacketRegistry<T extends DefinedPacket> {
   private final IntObjectMap<T> packets = new IntObjectHashMap<>(ProtocolConstants.SUPPORTED_VERSIONS.size() * 4);

   public PacketRegistry(Class<T> var1, Direction var2, String var3) {
      try {
         BufferedReader var4 = new BufferedReader(new InputStreamReader(Objects.requireNonNull(var1.getClassLoader().getResourceAsStream(var3))));

         String var5;
         try {
            while ((var5 = var4.readLine()) != null && var5.length() > 2) {
               String[] var6 = var5.split(",");
               int var7 = Integer.parseInt(var6[0]);
               int var8 = Integer.parseInt(var6[1]);
               byte[] var9 = new byte[var6.length - 2];

               for (int var10 = 2; var10 < var6.length; var10++) {
                  var9[var10 - 2] = Byte.parseByte(var6[var10]);
               }

               this.register(var1, var2, var7, var8, var9);
            }
         } catch (Throwable var12) {
            try {
               var4.close();
            } catch (Throwable var11) {
               var12.addSuppressed(var11);
            }

            throw var12;
         }

         var4.close();
      } catch (Exception var13) {
         var13.printStackTrace();
      }
   }

   protected void put(int var1, T var2) {
      this.packets.put(var1, (T)var2);
   }

   protected void register(Class<T> var1, Direction var2, int var3, int var4, byte[] var5) throws IllegalAccessException, InstantiationException {
      ByteBuf var6 = Unpooled.copiedBuffer(var5);
      DefinedPacket var7 = (DefinedPacket)var1.newInstance();

      try {
         var7.read(var6, var2, var3);
      } catch (Exception var12) {
         var12.printStackTrace();
      } finally {
         var6.release();
      }

      this.put(var3, (T)var7);

      for (int var9 : ProtocolConstants.SUPPORTED_VERSION_IDS) {
         if (var9 >= var3) {
            if (var9 >= var4) {
               break;
            }

            this.put(var9, (T)var7);
         }
      }
   }

   public T get(int var1) {
      return this.packets.get(var1);
   }
}
