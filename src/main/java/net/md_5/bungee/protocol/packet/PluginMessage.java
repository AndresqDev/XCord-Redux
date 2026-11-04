package net.md_5.bungee.protocol.packet;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.Arrays;
import java.util.Locale;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PluginMessage extends MultiVersionPacketV17 {
   public static final Function<String, String> MODERNISE = new Function<String, String>() {
      public String apply(String var1) {
         if (var1.equals("BungeeCord")) {
            return "bungeecord:main";
         } else if (var1.equals("bungeecord:main")) {
            return "BungeeCord";
         } else {
            return var1.indexOf(58) != -1 ? var1 : "legacy:" + var1.toLowerCase(Locale.ROOT);
         }
      }
   };
   private String tag;
   private byte[] data;
   private boolean allowExtendedPacket = false;

   public PluginMessage(String var1, ByteBuf var2, boolean var3) {
      this(var1, ByteBufUtil.getBytes(var2), var3);
   }

   public void setData(byte[] var1) {
      this.data = (byte[])Preconditions.checkNotNull(var1, "Null data");
   }

   public void setData(ByteBuf var1) {
      Preconditions.checkNotNull(var1, "Null buffer");
      this.setData(ByteBufUtil.getBytes(var1));
   }

   public void v17Read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.tag = readString(var1);
      this.data = v17readArray(var1);
   }

   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.tag = var3 >= 393 ? (String)MODERNISE.apply(readString(var1)) : readString(var1, 20);
      int var4 = var2 == ProtocolConstants.Direction.TO_SERVER ? 32767 : 1048576;
      Preconditions.checkArgument(var1.readableBytes() <= var4, "Payload too large");
      this.data = new byte[var1.readableBytes()];
      var1.readBytes(this.data);
   }

   public void v17Write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.tag, var1);
      v17writeArray(this.data, var1, this.allowExtendedPacket);
   }

   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(var3 >= 393 ? (String)MODERNISE.apply(this.tag) : this.tag, var1);
      var1.writeBytes(this.data);
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public DataInput getStream() {
      return new DataInputStream(new ByteArrayInputStream(this.data));
   }

   public String getTag() {
      return this.tag;
   }

   public byte[] getData() {
      return this.data;
   }

   public boolean isAllowExtendedPacket() {
      return this.allowExtendedPacket;
   }

   public void setTag(String var1) {
      this.tag = var1;
   }

   public void setAllowExtendedPacket(boolean var1) {
      this.allowExtendedPacket = var1;
   }

   public String toString() {
      return "PluginMessage(tag=" + this.getTag() + ", data=" + Arrays.toString(this.getData()) + ", allowExtendedPacket=" + this.isAllowExtendedPacket() + ")";
   }

   public PluginMessage() {
   }

   public PluginMessage(String var1, byte[] var2, boolean var3) {
      this.tag = var1;
      this.data = var2;
      this.allowExtendedPacket = var3;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PluginMessage)) {
         return false;
      } else {
         PluginMessage var2 = (PluginMessage)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.isAllowExtendedPacket() != var2.isAllowExtendedPacket()) {
            return false;
         } else {
            String var3 = this.getTag();
            String var4 = var2.getTag();
            return (var3 == null ? var4 == null : var3.equals(var4)) ? Arrays.equals(this.getData(), var2.getData()) : false;
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof PluginMessage;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + (this.isAllowExtendedPacket() ? 79 : 97);
      String var3 = this.getTag();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      return var2 * 59 + Arrays.hashCode(this.getData());
   }
}
