package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ByteArrayTag extends SpecificTag {
   public final byte[] value;

   public static SpecificTag read(DataInputStream var0) {
      try {
         int var1 = var0.readInt();
         byte[] var2 = new byte[var1];
         var0.readFully(var2, 0, var1);
         return new ByteArrayTag(var2);
      } catch (IOException var3) {
         return new ErrorTag("IOException while reading TAG_Byte_Array:\n" + var3.getMessage());
      }
   }

   @Override
   public void write(DataOutputStream var1) throws IOException {
      var1.writeInt(this.getData().length);
      var1.write(this.getData());
   }

   static void skip(DataInputStream var0) {
      try {
         int var1 = var0.readInt();
         var0.skipBytes(var1);
      } catch (IOException var2) {
      }
   }

   public ByteArrayTag(byte[] var1) {
      this.value = var1;
   }

   public byte[] getData() {
      return this.value;
   }

   @Override
   public String extraInfo() {
      return ": " + this.value.length;
   }

   public String type() {
      return "TAG_Byte_Array";
   }

   @Override
   public String tagName() {
      return "TAG_Byte_Array";
   }

   @Override
   public int tagType() {
      return 7;
   }

   @Override
   public byte[] byteArray() {
      return this.getData();
   }

   @Override
   public byte[] byteArray(byte[] var1) {
      return this.getData();
   }

   @Override
   public boolean isByteArray(int var1) {
      return this.getData().length >= var1;
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof ByteArrayTag && Arrays.equals(((ByteArrayTag)var1).value, this.value);
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.value);
   }
}
