package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class DumpNBT {
   public static void main(String[] var0) throws IOException {
      if (var0.length > 0) {
         try (
            FileInputStream var1 = new FileInputStream(var0[0]);
            GZIPInputStream var3 = new GZIPInputStream(var1);
            DataInputStream var5 = new DataInputStream(var3);
         ) {
            Tag var7 = NamedTag.read(var5);
            System.out.println(var7.dumpTree());
         }
      } else {
         System.err.println("Missing filename argument.");
         System.err.println("Usage: DumpNBT <GZipped NBT file>");
      }
   }
}
