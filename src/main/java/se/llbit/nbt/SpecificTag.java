package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class SpecificTag extends Tag {
   public static SpecificTag read(byte var0, DataInputStream var1) {
      switch (var0) {
         case 1:
            return ByteTag.read(var1);
         case 2:
            return ShortTag.read(var1);
         case 3:
            return IntTag.read(var1);
         case 4:
            return LongTag.read(var1);
         case 5:
            return FloatTag.read(var1);
         case 6:
            return DoubleTag.read(var1);
         case 7:
            return ByteArrayTag.read(var1);
         case 8:
            return StringTag.read(var1);
         case 9:
            return ListTag.read(var1);
         case 10:
            return CompoundTag.read(var1);
         case 11:
            return IntArrayTag.read(var1);
         case 12:
            return LongArrayTag.read(var1);
         default:
            return new ErrorTag("Unknown tag type: " + var0);
      }
   }

   public void writeType(DataOutputStream var1) throws IOException {
      var1.writeByte(this.tagType());
   }

   static void skip(byte var0, DataInputStream var1) {
      try {
         switch (var0) {
            case 1:
               var1.skipBytes(1);
               break;
            case 2:
               var1.skipBytes(2);
               break;
            case 3:
               var1.skipBytes(4);
               break;
            case 4:
               var1.skipBytes(8);
               break;
            case 5:
               var1.skipBytes(4);
               break;
            case 6:
               var1.skipBytes(8);
               break;
            case 7:
               ByteArrayTag.skip(var1);
               break;
            case 8:
               StringTag.skip(var1);
               break;
            case 9:
               ListTag.skip(var1);
               break;
            case 10:
               CompoundTag.skip(var1);
               break;
            case 11:
               IntArrayTag.skip(var1);
               break;
            case 12:
               LongArrayTag.skip(var1);
         }
      } catch (IOException var3) {
      }
   }

   public SpecificTag clone() throws CloneNotSupportedException {
      return (SpecificTag)super.clone();
   }

   public abstract int tagType();
}
