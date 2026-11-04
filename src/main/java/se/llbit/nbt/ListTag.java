package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListTag extends SpecificTag implements Iterable<SpecificTag> {
   public final int type;
   public final List<SpecificTag> items;

   public static SpecificTag read(DataInputStream var0) {
      try {
         byte var1 = var0.readByte();
         int var2 = var0.readInt();
         if (var1 == 0 && var2 > 0) {
            return new ErrorTag("Cannot create list of TAG_End");
         } else {
            ListTag var3 = new ListTag(var1, Collections.emptyList());

            for (int var4 = 0; var4 < var2; var4++) {
               SpecificTag var5 = SpecificTag.read(var1, var0);
               var3.add(var5);
            }

            return var3;
         }
      } catch (IOException var6) {
         return new ErrorTag("IOException while reading TAG_List:\n" + var6.getMessage());
      }
   }

   @Override
   public void write(DataOutputStream var1) throws IOException {
      var1.writeByte(this.getType());
      var1.writeInt(this.size());

      for (SpecificTag var3 : this.items) {
         var3.write(var1);
      }
   }

   static Map<String, Tag> partialParse(DataInputStream var0, String var1, Map<String, Tag> var2, Set<String> var3, Set<String> var4) {
      try {
         byte var5 = var0.readByte();
         int var6 = var0.readInt();
         if (var5 == 0 && var6 > 0) {
            return var2;
         }

         for (int var7 = 0; var7 < var6; var7++) {
            String var8 = var1 + "." + var7;
            boolean var9 = NamedTag.partiallyParseTag(var0, var2, var3, var4, var5, var8);
            if (var9) {
               if (var3.isEmpty()) {
                  return var2;
               }
            } else {
               SpecificTag.skip(var5, var0);
            }
         }
      } catch (IOException var10) {
      }

      return var2;
   }

   static void skip(DataInputStream var0) {
      try {
         byte var1 = var0.readByte();
         int var2 = var0.readInt();
         if (var1 == 0) {
            return;
         }

         for (int var3 = 0; var3 < var2; var3++) {
            SpecificTag.skip(var1, var0);
         }
      } catch (IOException var4) {
      }
   }

   public ListTag(int var1, List<? extends SpecificTag> var2) {
      this.type = var1;
      this.items = new ArrayList<>(var2);
   }

   public int getType() {
      return this.type;
   }

   public int size() {
      return this.items.size();
   }

   public void add(SpecificTag var1) {
      this.items.add(var1);
   }

   @Override
   public void set(int var1, SpecificTag var2) {
      this.items.set(var1, var2);
   }

   @Override
   public String toString() {
      return this.dumpTree();
   }

   public String type() {
      return "TAG_List";
   }

   @Override
   public void printTag(StringBuilder var1, String var2) {
      var1.append(var2);
      this.printTagInfo(var1);

      for (Tag var4 : this.items) {
         var4.printTag(var1, var2 + "  ");
      }
   }

   @Override
   public String tagName() {
      return "TAG_List";
   }

   @Override
   public int tagType() {
      return 9;
   }

   @Override
   public boolean isList() {
      return true;
   }

   @Override
   public Tag get(int var1) {
      return this.items.get(var1);
   }

   @Override
   public Iterator<SpecificTag> iterator() {
      return this.items.iterator();
   }

   @Override
   public ListTag asList() {
      return this;
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else {
         return !(var1 instanceof ListTag) ? false : this.items.equals(((ListTag)var1).items);
      }
   }

   @Override
   public int hashCode() {
      int var1 = 0;

      for (SpecificTag var3 : this.items) {
         var1 *= 31;
         var1 ^= var3.hashCode();
      }

      return var1;
   }
}
