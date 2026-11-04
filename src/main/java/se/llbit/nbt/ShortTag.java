package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ShortTag extends SpecificTag {
   public final short value;

   public static SpecificTag read(DataInputStream var0) {
      try {
         return new ShortTag(var0.readShort());
      } catch (IOException var2) {
         return new ErrorTag("IOException while reading TAG_Short:\n" + var2.getMessage());
      }
   }

   @Override
   public void write(DataOutputStream var1) throws IOException {
      var1.writeShort(this.getData());
   }

   public ShortTag(short var1) {
      this.value = var1;
   }

   public short getData() {
      return this.value;
   }

   @Override
   public String extraInfo() {
      return ": " + this.getData();
   }

   public String type() {
      return "TAG_Short";
   }

   @Override
   public String tagName() {
      return "TAG_Short";
   }

   @Override
   public int tagType() {
      return 2;
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
   public short shortValue() {
      return this.getData();
   }

   @Override
   public short shortValue(short var1) {
      return this.getData();
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof ShortTag && ((ShortTag)var1).value == this.value;
   }

   @Override
   public int hashCode() {
      return this.value;
   }
}
