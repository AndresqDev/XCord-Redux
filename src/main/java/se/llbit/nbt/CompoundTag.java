package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CompoundTag extends SpecificTag implements Iterable<NamedTag> {
   final List<NamedTag> items;

   public void add(String var1, SpecificTag var2) {
      this.add(new NamedTag(var1, var2));
   }

   public static SpecificTag read(DataInputStream var0) {
      CompoundTag var1 = new CompoundTag();

      while (true) {
         Tag var2 = NamedTag.read(var0);
         if (var2.isEnd()) {
            return var1;
         }

         var1.add((NamedTag)var2);
      }
   }

   @Override
   public void write(DataOutputStream var1) throws IOException {
      for (Tag var3 : this.items) {
         var3.write(var1);
      }

      var1.writeByte(0);
   }

   static Map<String, Tag> partialParse(DataInputStream var0, String var1, Map<String, Tag> var2, Set<String> var3, Set<String> var4) {
      try {
         while (true) {
            byte var5 = var0.readByte();
            if (var5 != 0) {
               SpecificTag var6 = StringTag.read(var0);
               String var7 = var1 + "." + var6.stringValue();
               boolean var8 = NamedTag.partiallyParseTag(var0, var2, var3, var4, var5, var7);
               if (var8) {
                  if (!var3.isEmpty()) {
                     continue;
                  }

                  return var2;
               }

               SpecificTag.skip(var5, var0);
               continue;
            }
         }
      } catch (IOException var9) {
      }

      return var2;
   }

   static void skip(DataInputStream var0) {
      try {
         while (true) {
            byte var1 = var0.readByte();
            if (var1 != 0) {
               StringTag.skip(var0);
               SpecificTag.skip(var1, var0);
               continue;
            }
         }
      } catch (IOException var2) {
      }
   }

   public CompoundTag() {
      this.items = new ArrayList<>();
   }

   @Override
   public void printTag(StringBuilder var1, String var2) {
      var1.append(var2);
      this.printTagInfo(var1);

      for (NamedTag var4 : this.items) {
         var1.append(String.format("%s  %s:\n", var2, var4.name()));
         var4.tag.printTag(var1, var2 + "    ");
      }
   }

   public CompoundTag(List<? extends NamedTag> var1) {
      this.items = new ArrayList<>(var1);
   }

   public int size() {
      return this.items.size();
   }

   public void add(NamedTag var1) {
      this.items.add(var1);
   }

   @Override
   public String toString() {
      return this.dumpTree();
   }

   public String type() {
      return "TAG_Compound";
   }

   @Override
   public String tagName() {
      return "TAG_Compound";
   }

   @Override
   public int tagType() {
      return 10;
   }

   @Override
   public boolean isCompoundTag() {
      return true;
   }

   @Override
   public Tag get(String var1) {
      for (Tag var3 : this.items) {
         if (var3.isNamed(var1)) {
            return var3.unpack();
         }
      }

      return new ErrorTag("No item named \"" + var1 + "\" in this compound tag.");
   }

   @Override
   public void set(String var1, Tag var2) {
      throw new Error();
   }

   @Override
   public Iterator<NamedTag> iterator() {
      return this.items.iterator();
   }

   @Override
   public CompoundTag asCompound() {
      return this;
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof CompoundTag)) {
         return false;
      } else {
         CompoundTag var2 = (CompoundTag)var1;

         for (NamedTag var4 : this.items) {
            if (!var2.get(var4.name()).equals(var4.tag)) {
               return false;
            }
         }

         for (NamedTag var6 : var2.items) {
            if (!this.get(var6.name()).equals(var6.tag)) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;

      for (NamedTag var3 : this.items) {
         var1 ^= var3.hashCode();
      }

      return var1;
   }
}
