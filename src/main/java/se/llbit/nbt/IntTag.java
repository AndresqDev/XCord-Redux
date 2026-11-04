package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IntTag extends SpecificTag {
   public final int value;

   public static SpecificTag read(DataInputStream var0) {
      try {
         return new IntTag(var0.readInt());
      } catch (IOException var2) {
         return new ErrorTag("IOException while reading TAG_Int:\n" + var2.getMessage());
      }
   }

   @Override
   public void write(DataOutputStream var1) throws IOException {
      var1.writeInt(this.value);
   }

   public IntTag(boolean var1) {
      this(var1 ? 1 : 0);
   }

   public IntTag(int var1) {
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
      return "TAG_Int";
   }

   @Override
   public String tagName() {
      return "TAG_Int";
   }

   @Override
   public int tagType() {
      return 3;
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
   public int intValue() {
      return this.getData();
   }

   @Override
   public int intValue(int var1) {
      return this.getData();
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof IntTag && ((IntTag)var1).value == this.value;
   }

   @Override
   public int hashCode() {
      return this.value;
   }
}
