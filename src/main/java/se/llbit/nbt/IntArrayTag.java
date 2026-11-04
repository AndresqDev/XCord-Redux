package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class IntArrayTag extends SpecificTag {
   public final int[] value;

   public static SpecificTag read(DataInputStream var0) {
      try {
         int var1 = var0.readInt();
         int[] var2 = new int[var1];

         for (int var3 = 0; var3 < var1; var3++) {
            var2[var3] = var0.readInt();
         }

         return new IntArrayTag(var2);
      } catch (IOException var4) {
         return new ErrorTag("IOException while reading TAG_Int_Array:\n" + var4.getMessage());
      }
   }

   @Override
   public void write(DataOutputStream var1) throws IOException {
      var1.writeInt(this.value.length);

      for (int var2 = 0; var2 < this.value.length; var2++) {
         var1.writeInt(this.value[var2]);
      }
   }

   static void skip(DataInputStream var0) {
      try {
         int var1 = var0.readInt();
         var0.skipBytes(var1 * 4);
      } catch (IOException var2) {
      }
   }

   public IntArrayTag(int[] var1) {
      this.value = var1;
   }

   public int[] getData() {
      return this.value;
   }

   @Override
   public String extraInfo() {
      return ": " + this.value.length;
   }

   @Override
   public String tagName() {
      return "TAG_Int_Array";
   }

   public String type() {
      return "TAG_Int_Array";
   }

   @Override
   public int tagType() {
      return 11;
   }

   @Override
   public int[] intArray() {
      return this.value;
   }

   @Override
   public int[] intArray(int[] var1) {
      return this.value;
   }

   @Override
   public boolean isIntArray(int var1) {
      return this.value.length >= var1;
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof IntArrayTag && Arrays.equals(((IntArrayTag)var1).value, this.value);
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(this.value);
   }
}
