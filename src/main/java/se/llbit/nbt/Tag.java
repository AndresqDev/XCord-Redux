package se.llbit.nbt;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;

public abstract class Tag {
   public static final int TAG_END = 0;
   public static final int TAG_BYTE = 1;
   public static final int TAG_SHORT = 2;
   public static final int TAG_INT = 3;
   public static final int TAG_LONG = 4;
   public static final int TAG_FLOAT = 5;
   public static final int TAG_DOUBLE = 6;
   public static final int TAG_BYTE_ARRAY = 7;
   public static final int TAG_STRING = 8;
   public static final int TAG_LIST = 9;
   public static final int TAG_COMPOUND = 10;
   public static final int TAG_INT_ARRAY = 11;
   public static final int TAG_LONG_ARRAY = 12;
   private static final ListTag EMPTY_LIST = new ListTag(0, Collections.emptyList());
   private static final CompoundTag EMPTY_COMPOUND = new CompoundTag();
   public static final SpecificTag END = new SpecificTag() {
      @Override
      public void write(DataOutputStream var1) throws IOException {
         this.writeType(var1);
      }

      @Override
      public String tagName() {
         return "TAG_End";
      }

      @Override
      public int tagType() {
         return 0;
      }

      @Override
      public boolean isEnd() {
         return true;
      }
   };

   protected Tag() {
   }

   public abstract String tagName();

   public boolean isError() {
      return false;
   }

   public boolean isNamed(String var1) {
      return false;
   }

   public boolean same(String var1) {
      return false;
   }

   public String dumpTree() {
      StringBuilder var1 = new StringBuilder(4096);
      this.printTag(var1, "");
      return var1.toString();
   }

   public void printTagInfo(StringBuilder var1) {
      var1.append(this.tagName()).append(this.extraInfo()).append('\n');
   }

   public void printTag(StringBuilder var1, String var2) {
      var1.append(var2);
      this.printTagInfo(var1);
   }

   public String extraInfo() {
      return "";
   }

   public abstract void write(DataOutputStream var1) throws IOException;

   @Override
   public String toString() {
      return this.tagName() + this.extraInfo();
   }

   public boolean isEnd() {
      return false;
   }

   public Tag unpack() {
      return this;
   }

   public String error() {
      return "";
   }

   public boolean boolValue() {
      return false;
   }

   public boolean boolValue(boolean var1) {
      return var1;
   }

   public int byteValue() {
      return 0;
   }

   public int byteValue(int var1) {
      return var1;
   }

   public short shortValue() {
      return 0;
   }

   public short shortValue(short var1) {
      return var1;
   }

   public int intValue() {
      return 0;
   }

   public int intValue(int var1) {
      return var1;
   }

   public long longValue() {
      return 0L;
   }

   public long longValue(long var1) {
      return var1;
   }

   public float floatValue() {
      return 0.0F;
   }

   public float floatValue(float var1) {
      return var1;
   }

   public double doubleValue() {
      return 0.0;
   }

   public double doubleValue(double var1) {
      return var1;
   }

   public String stringValue() {
      return "";
   }

   public String stringValue(String var1) {
      return var1;
   }

   public byte[] byteArray() {
      return new byte[0];
   }

   public byte[] byteArray(byte[] var1) {
      return var1;
   }

   public int[] intArray() {
      return new int[0];
   }

   public int[] intArray(int[] var1) {
      return var1;
   }

   public long[] longArray() {
      return new long[0];
   }

   public long[] longArray(long[] var1) {
      return var1;
   }

   public boolean isCompoundTag() {
      return false;
   }

   public boolean isList() {
      return false;
   }

   public boolean isByteArray(int var1) {
      return false;
   }

   public boolean isIntArray(int var1) {
      return false;
   }

   public boolean isLongArray(int var1) {
      return false;
   }

   public Tag get(String var1) {
      return new ErrorTag("Can not index-by-name in a non-CompoundTag");
   }

   public void set(String var1, Tag var2) {
      throw new Error("Can not set item in non-compound tag.");
   }

   public void set(int var1, SpecificTag var2) {
      throw new Error("Can not set item in non-list tag.");
   }

   public Tag get(int var1) {
      return new ErrorTag("Can not index a non-ListTag");
   }

   public ListTag asList() {
      return EMPTY_LIST;
   }

   public CompoundTag asCompound() {
      return EMPTY_COMPOUND;
   }
}
