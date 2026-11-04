package io.github.waterfallmc.travertine.protocol;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants.Direction;

public abstract class MultiVersionPacketV17 extends DefinedPacket {
   protected void v17Read(ByteBuf var1, Direction var2, int var3) {
      this.v17Read(var1);
   }

   public void read0(ByteBuf var1, Direction var2, int var3) {
      switch (var3) {
         case 4:
         case 5:
            this.v17Read(var1, var2, var3);
            break;
         default:
            this.read(var1, var2, var3);
      }
   }

   protected void v17Write(ByteBuf var1, Direction var2, int var3) {
      this.v17Write(var1);
   }

   public void write0(ByteBuf var1, Direction var2, int var3) {
      switch (var3) {
         case 4:
         case 5:
            this.v17Write(var1, var2, var3);
            break;
         default:
            this.write(var1, var2, var3);
      }
   }

   protected void v17Read(ByteBuf var1) {
      throw new UnsupportedOperationException("Packet must implement read method");
   }

   protected void v17Write(ByteBuf var1) {
      throw new UnsupportedOperationException("Packet must implement write method");
   }

   public static void v17writeArray(byte[] var0, ByteBuf var1, boolean var2) {
      if (var2) {
         Preconditions.checkArgument(var0.length <= 2097050, "Cannot send array longer than 2097050 (got %s bytes)", var0.length);
      } else {
         Preconditions.checkArgument(var0.length <= 32767, "Cannot send array longer than Short.MAX_VALUE (got %s bytes)", var0.length);
      }

      writeVarShort(var1, var0.length);
      var1.writeBytes(var0);
   }

   public static byte[] v17readArray(ByteBuf var0) {
      int var1 = readVarShort(var0);
      Preconditions.checkArgument(var1 <= 2097050, "Cannot receive array longer than 2097050 (got %s bytes)", var1);
      byte[] var2 = new byte[var1];
      var0.readBytes(var2);
      return var2;
   }
}
