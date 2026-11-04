package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FloatTag extends SpecificTag {
   public final float value;

   public static SpecificTag read(DataInputStream var0) {
      try {
         return new FloatTag(var0.readFloat());
      } catch (IOException var2) {
         return new ErrorTag("IOException while reading TAG_Float:\n" + var2.getMessage());
      }
   }

   @Override
   public void write(DataOutputStream var1) throws IOException {
      var1.writeFloat(this.getData());
   }

   public FloatTag(float var1) {
      this.value = var1;
   }

   public float getData() {
      return this.value;
   }

   @Override
   public String extraInfo() {
      return ": " + this.getData();
   }

   public String type() {
      return "TAG_Float";
   }

   @Override
   public String tagName() {
      return "TAG_Float";
   }

   @Override
   public int tagType() {
      return 5;
   }

   @Override
   public float floatValue() {
      return this.getData();
   }

   @Override
   public float floatValue(float var1) {
      return this.getData();
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof FloatTag && ((FloatTag)var1).value == this.value;
   }

   @Override
   public int hashCode() {
      return Float.floatToRawIntBits(this.value);
   }
}
