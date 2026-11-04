package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NamedTag extends Tag {
   public final String name;
   public final SpecificTag tag;

   public NamedTag(String var1, SpecificTag var2) {
      this.name = var1;
      this.tag = var2;
   }

   public static Tag read(DataInputStream var0) {
      try {
         byte var1 = var0.readByte();
         if (var1 == 0) {
            return Tag.END;
         } else {
            SpecificTag var2 = StringTag.read(var0);
            SpecificTag var3 = SpecificTag.read(var1, var0);
            return new NamedTag(var2.stringValue(), var3);
         }
      } catch (IOException var4) {
         return new ErrorTag("IOException while reading tag type:\n" + var4.getMessage());
      }
   }

   @Override
   public void write(DataOutputStream var1) throws IOException {
      this.getTag().writeType(var1);
      StringTag.write(var1, this.name);
      this.getTag().write(var1);
   }

   public static Map<String, Tag> quickParse(DataInputStream var0, Set<String> var1) {
      HashMap var2 = new HashMap();
      HashSet var3 = new HashSet();

      for (String var5 : var1) {
         String[] var6 = var5.split("\\.");
         String var7 = "";

         for (int var8 = 0; var8 < var6.length - 1; var8++) {
            if (var8 > 0) {
               var7 = var7 + ".";
            }

            var7 = var7 + var6[var8];
            var3.add(var7);
         }
      }

      for (String var10 : var1) {
         var2.put(var10, new ErrorTag("[not loaded]"));
      }

      return partialParse(var0, var2, var1, var3);
   }

   private static Map<String, Tag> partialParse(DataInputStream var0, Map<String, Tag> var1, Set<String> var2, Set<String> var3) {
      try {
         byte var4 = var0.readByte();
         if (var4 != 0) {
            SpecificTag var5 = StringTag.read(var0);
            String var6 = var5.stringValue();
            partiallyParseTag(var0, var1, var2, var3, var4, var6);
         }
      } catch (IOException var7) {
      }

      return var1;
   }

   static boolean partiallyParseTag(DataInputStream var0, Map<String, Tag> var1, Set<String> var2, Set<String> var3, byte var4, String var5) {
      if (var2.contains(var5)) {
         SpecificTag var6 = SpecificTag.read(var4, var0);
         var1.put(var5, var6);
         var2.remove(var5);
         return true;
      } else if (var3.contains(var5)) {
         if (var4 == 9) {
            ListTag.partialParse(var0, var5, var1, var2, var3);
         } else if (var4 == 10) {
            CompoundTag.partialParse(var0, var5, var1, var2, var3);
         }

         return true;
      } else {
         return false;
      }
   }

   public String name() {
      return this.name;
   }

   public SpecificTag getTag() {
      return this.tag;
   }

   @Override
   public String tagName() {
      return "TAG:named";
   }

   @Override
   public Tag unpack() {
      return this.getTag();
   }

   @Override
   public boolean isNamed(String var1) {
      return this.name.equals(var1);
   }

   @Override
   public void printTag(StringBuilder var1, String var2) {
      var1.append(var2);
      this.printTagInfo(var1);
      var1.append(var2).append("  ").append("TAG_String: \"").append(this.name).append("\"\n");
      this.tag.printTag(var1, var2 + "  ");
   }

   @Override
   public ListTag asList() {
      return this.tag.asList();
   }

   @Override
   public CompoundTag asCompound() {
      return this.tag.asCompound();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof NamedTag)) {
         return false;
      } else {
         NamedTag var2 = (NamedTag)var1;
         return this.name.equals(var2.name) && this.tag.equals(var2.tag);
      }
   }

   @Override
   public int hashCode() {
      return this.name.hashCode() * 31 ^ this.tag.hashCode();
   }
}
