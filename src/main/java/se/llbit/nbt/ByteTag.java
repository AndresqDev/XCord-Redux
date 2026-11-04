package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByteTag extends SpecificTag {
   public final int value;

   public static SpecificTag read(DataInputStream var0) {
      try {
         return new ByteTag(var0.readByte());
      } catch (IOException var2) {
         return new ErrorTag("IOException while reading TAG_Byte:\n" + var2.getMessage());
      }
   }

   @Override
   public void write(DataOutputStream var1) throws IOException {
      var1.writeByte(this.getData());
   }

   public ByteTag(int var1) {
      this.value = var1;
   }

   public int getData() {
      return this.value;
   }

   @Override
   public String extraInfo() {
      return ": " + this.getData();
   }

   public String type() {
      return "TAG_Byte";
   }

   @Override
   public String tagName() {
      return "TAG_Byte";
   }

   @Override
   public int tagType() {
      return 1;
   }

   @Override
   public boolean boolValue() {
      return this.getData() != 0;
   }

   @Override
   public boolean boolValue(boolean var1) {
      return this.getData() != 0;
   }

   @Override
   public int byteValue() {
      return this.getData();
   }

   @Override
   public int byteValue(int var1) {
      return this.getData();
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof ByteTag && ((ByteTag)var1).value == this.value;
   }

   @Override
   public int hashCode() {
      return this.value;
   }
}
