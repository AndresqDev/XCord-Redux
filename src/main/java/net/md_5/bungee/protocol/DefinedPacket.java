package net.md_5.bungee.protocol;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;

public abstract class DefinedPacket {
   private static final boolean PROCESS_TRACES = Boolean.getBoolean("waterfall.bad-packet-traces");
   private static final BadPacketException OVERSIZED_VAR_INT_EXCEPTION = new BadPacketException("VarInt too big");
   private static final BadPacketException NO_MORE_BYTES_EXCEPTION = new BadPacketException("No more bytes reading varint");
   private static final OverflowPacketException STRING_TOO_LONG_EXCEPTION = new OverflowPacketException(
      "A string was longer than allowed. For more information, launch Waterfall with -Dwaterfall.packet-decode-logging=true"
   );
   private static final OverflowPacketException STRING_TOO_MANY_BYTES_EXCEPTION = new OverflowPacketException(
      "A string had more data than allowed. For more information, launch Waterfall with -Dwaterfall.packet-decode-logging=true"
   );

   public static void writeString(String var0, ByteBuf var1) {
      writeString(var0, var1, 32767);
   }

   public static void writeString(String var0, ByteBuf var1, int var2) {
      if (var0.length() > var2) {
         throw new OverflowPacketException("Cannot send string longer than " + var2 + " (got " + var0.length() + " characters)");
      } else {
         byte[] var3 = var0.getBytes(Charsets.UTF_8);
         if (var3.length > var2 * 3) {
            throw new OverflowPacketException("Cannot send string longer than " + var2 * 3 + " (got " + var3.length + " bytes)");
         } else {
            writeVarInt(var3.length, var1);
            var1.writeBytes(var3);
         }
      }
   }

   public static String readString(ByteBuf var0) {
      return readString(var0, 32767);
   }

   public static String readString(ByteBuf var0, int var1) {
      int var2 = readVarInt(var0);
      if (var2 > var1 * 3) {
         throw new OverflowPacketException("Cannot receive string longer than " + var1 * 3 + " (got " + var2 + " bytes)");
      } else {
         String var3 = var0.toString(var0.readerIndex(), var2, Charsets.UTF_8);
         var0.readerIndex(var0.readerIndex() + var2);
         if (var3.length() > var1) {
            throw new OverflowPacketException("Cannot receive string longer than " + var1 + " (got " + var3.length() + " characters)");
         } else {
            return var3;
         }
      }
   }

   public static void writeString(String var0, int var1, ByteBuf var2) {
      if (var0.length() > var1) {
         throw new OverflowPacketException(String.format("Cannot send string longer than %s (got %s characters)", var1, var0.length()));
      } else {
         byte[] var3 = var0.getBytes(Charsets.UTF_8);
         writeVarInt(var3.length, var2);
         var2.writeBytes(var3);
      }
   }

   public static void writeArray(byte[] var0, ByteBuf var1) {
      if (var0.length > 32767) {
         throw new OverflowPacketException("Cannot send byte array longer than Short.MAX_VALUE (got " + var0.length + " bytes)");
      } else {
         writeVarInt(var0.length, var1);
         var1.writeBytes(var0);
      }
   }

   public static byte[] toArray(ByteBuf var0) {
      byte[] var1 = new byte[var0.readableBytes()];
      var0.readBytes(var1);
      return var1;
   }

   public static byte[] readArray(ByteBuf var0) {
      return readArray(var0, var0.readableBytes());
   }

   public static byte[] readArray(ByteBuf var0, int var1) {
      int var2 = readVarInt(var0);
      if (var2 > var1) {
         throw new OverflowPacketException("Cannot receive byte array longer than " + var1 + " (got " + var2 + " bytes)");
      } else {
         byte[] var3 = new byte[var2];
         var0.readBytes(var3);
         return var3;
      }
   }

   public static int[] readVarIntArray(ByteBuf var0) {
      int var1 = readVarInt(var0);
      int[] var2 = new int[var1];

      for (int var3 = 0; var3 < var1; var3++) {
         var2[var3] = readVarInt(var0);
      }

      return var2;
   }

   public static void writeStringArray(List<String> var0, ByteBuf var1) {
      writeVarInt(var0.size(), var1);

      for (String var3 : var0) {
         writeString(var3, var1);
      }
   }

   public static List<String> readStringArray(ByteBuf var0) {
      int var1 = readVarInt(var0);
      ArrayList var2 = new ArrayList(var1);

      for (int var3 = 0; var3 < var1; var3++) {
         var2.add(readString(var0));
      }

      return var2;
   }

   public static int readVarInt(ByteBuf var0) {
      return readVarInt(var0, 5);
   }

   public static int readVarInt(ByteBuf var0, int var1) {
      int var2 = 0;
      int var3 = 0;

      while (var0.readableBytes() != 0) {
         byte var4 = var0.readByte();
         var2 |= (var4 & 127) << var3++ * 7;
         if (var3 > var1) {
            throw PROCESS_TRACES ? new BadPacketException("VarInt too big") : OVERSIZED_VAR_INT_EXCEPTION;
         }

         if ((var4 & 128) != 128) {
            return var2;
         }
      }

      throw PROCESS_TRACES ? new BadPacketException("No more bytes reading varint") : NO_MORE_BYTES_EXCEPTION;
   }

   public static void writeVarInt(int var0, ByteBuf var1) {
      int var2 = (31 - Integer.numberOfLeadingZeros(var0)) / 7;

      for (int var3 = 0; var3 < var2; var3++) {
         var1.writeByte((byte)(var0 & 127 | 128));
         var0 >>>= 7;
      }

      var1.writeByte((byte)var0);
   }

   public static int readVarShort(ByteBuf var0) {
      int var1 = var0.readUnsignedShort();
      short var2 = 0;
      if ((var1 & 32768) != 0) {
         var1 &= 32767;
         var2 = var0.readUnsignedByte();
      }

      return (var2 & 0xFF) << 15 | var1;
   }

   public static void writeVarShort(ByteBuf var0, int var1) {
      int var2 = var1 & 32767;
      int var3 = (var1 & 8355840) >> 15;
      if (var3 != 0) {
         var2 |= 32768;
      }

      var0.writeShort(var2);
      if (var3 != 0) {
         var0.writeByte(var3);
      }
   }

   public static void writeUUID(UUID var0, ByteBuf var1) {
      var1.writeLong(var0.getMostSignificantBits());
      var1.writeLong(var0.getLeastSignificantBits());
   }

   public static UUID readUUID(ByteBuf var0) {
      return new UUID(var0.readLong(), var0.readLong());
   }

   public static void writeProperties(Property[] var0, ByteBuf var1) {
      if (var0 == null) {
         writeVarInt(0, var1);
      } else {
         writeVarInt(var0.length, var1);

         for (Property var5 : var0) {
            writeString(var5.getName(), var1);
            writeString(var5.getValue(), var1);
            if (var5.getSignature() != null) {
               var1.writeBoolean(true);
               writeString(var5.getSignature(), var1);
            } else {
               var1.writeBoolean(false);
            }
         }
      }
   }

   public static Property[] readProperties(ByteBuf var0) {
      Property[] var1 = new Property[readVarInt(var0)];

      for (int var2 = 0; var2 < var1.length; var2++) {
         String var3 = readString(var0);
         String var4 = readString(var0);
         if (var0.readBoolean()) {
            var1[var2] = new Property(var3, var4, readString(var0));
         } else {
            var1[var2] = new Property(var3, var4);
         }
      }

      return var1;
   }

   public static void writePublicKey(PlayerPublicKey var0, ByteBuf var1) {
      if (var0 != null) {
         var1.writeBoolean(true);
         var1.writeLong(var0.getExpiry());
         writeArray(var0.getKey(), var1);
         writeArray(var0.getSignature(), var1);
      } else {
         var1.writeBoolean(false);
      }
   }

   public static PlayerPublicKey readPublicKey(ByteBuf var0) {
      return var0.readBoolean() ? new PlayerPublicKey(var0.readLong(), readArray(var0), readArray(var0)) : null;
   }

   public static Tag readTag(ByteBuf var0) {
      Tag var1 = NamedTag.read(new DataInputStream(new ByteBufInputStream(var0)));
      Preconditions.checkArgument(!var1.isError(), "Error reading tag: %s", var1.error());
      return var1;
   }

   public static void writeTag(Tag var0, ByteBuf var1) {
      try {
         var0.write(new DataOutputStream(new ByteBufOutputStream(var1)));
      } catch (IOException var3) {
         throw new RuntimeException("Exception writing tag", var3);
      }
   }

   public void read(ByteBuf var1) {
      throw new UnsupportedOperationException("Packet must implement read method");
   }

   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.read(var1);
   }

   public void read0(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.read(var1, var2, var3);
   }

   public void write(ByteBuf var1) {
      throw new UnsupportedOperationException("Packet must implement write method");
   }

   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.write(var1);
   }

   public void write0(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.write(var1, var2, var3);
   }

   public abstract void handle(AbstractPacketHandler var1) throws Exception;

   @Override
   public abstract boolean equals(Object var1);

   @Override
   public abstract int hashCode();

   @Override
   public abstract String toString();

   public int expectedMaxLength(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      return -1;
   }

   public int expectedMinLength(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      return -1;
   }
}
