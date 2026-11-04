package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringTag extends SpecificTag {
   public final String value;

   public static SpecificTag read(DataInputStream var0) {
      try {
         return new StringTag(var0.readUTF());
      } catch (IOException var2) {
         return new ErrorTag("IOException while reading TAG_String:\n" + var2.getMessage());
      }
   }

   @Override
   public void write(DataOutputStream var1) throws IOException {
      write(var1, this.value);
   }

   static void write(DataOutputStream var0, String var1) throws IOException {
      var0.writeUTF(var1);
   }

   static void skip(DataInputStream var0) {
      try {
         short var1 = var0.readShort();
         var0.skipBytes(var1);
      } catch (IOException var2) {
      }
   }

   public StringTag(String var1) {
      this.value = var1;
   }

   public String getData() {
      return this.value != null ? this.value : "";
   }

   @Override
   public String extraInfo() {
      return ": \"" + this.getData() + '"';
   }

   public String type() {
      return "TAG_String";
   }

   @Override
   public String tagName() {
      return "TAG_String";
   }

   @Override
   public int tagType() {
      return 8;
   }

   @Override
   public String stringValue() {
      return this.getData();
   }

   @Override
   public String stringValue(String var1) {
      return this.getData();
   }

   @Override
   public boolean same(String var1) {
      return this.getData().equals(var1);
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof StringTag && ((StringTag)var1).value.equals(this.value);
   }

   @Override
   public int hashCode() {
      return this.value.hashCode();
   }
}
