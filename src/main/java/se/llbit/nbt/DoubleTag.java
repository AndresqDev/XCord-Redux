package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleTag extends SpecificTag {
   public final double value;

   public static SpecificTag read(DataInputStream var0) {
      try {
         return new DoubleTag(var0.readDouble());
      } catch (IOException var2) {
         return new ErrorTag("IOException while reading TAG_Double:\n" + var2.getMessage());
      }
   }

   @Override
   public void write(DataOutputStream var1) throws IOException {
      var1.writeDouble(this.getData());
   }

   public DoubleTag(double var1) {
      this.value = var1;
   }

   public double getData() {
      return this.value;
   }

   @Override
   public String extraInfo() {
      return ": " + this.getData();
   }

   public String type() {
      return "TAG_Double";
   }

   @Override
   public String tagName() {
      return "TAG_Double";
   }

   @Override
   public int tagType() {
      return 6;
   }

   @Override
   public double doubleValue() {
      return this.getData();
   }

   @Override
   public double doubleValue(double var1) {
      return this.getData();
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof DoubleTag && ((DoubleTag)var1).value == this.value;
   }

   @Override
   public int hashCode() {
      long var1 = Double.doubleToRawLongBits(this.value);
      return (int)(var1 >> 32 ^ var1);
   }
}
