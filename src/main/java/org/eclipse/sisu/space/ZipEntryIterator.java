package org.eclipse.sisu.space;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

final class ZipEntryIterator implements Iterator<String> {
   private String[] entryNames;
   private int index;

   ZipEntryIterator(URL var1) {
      try {
         if ("file".equals(var1.getProtocol())) {
            this.entryNames = getEntryNames(new ZipFile(FileEntryIterator.toFile(var1)));
         } else {
            this.entryNames = getEntryNames(new ZipInputStream(Streams.open(var1)));
         }
      } catch (IOException var2) {
         this.entryNames = new String[0];
      }
   }

   @Override
   public boolean hasNext() {
      return this.index < this.entryNames.length;
   }

   public String next() {
      return this.entryNames[this.index++];
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }

   private static String[] getEntryNames(ZipFile var0) throws IOException {
      String[] var5;
      try {
         String[] var1 = new String[var0.size()];
         Enumeration var2 = var0.entries();

         for (int var3 = 0; var3 < var1.length; var3++) {
            var1[var3] = ((ZipEntry)var2.nextElement()).getName();
         }

         var5 = var1;
      } finally {
         var0.close();
      }

      return var5;
   }

   private static String[] getEntryNames(ZipInputStream var0) throws IOException {
      String[] var4;
      try {
         ArrayList var1 = new ArrayList(64);

         for (ZipEntry var2 = var0.getNextEntry(); var2 != null; var2 = var0.getNextEntry()) {
            var1.add(var2.getName());
         }

         var4 = var1.toArray(new String[var1.size()]);
      } finally {
         var0.close();
      }

      return var4;
   }
}
