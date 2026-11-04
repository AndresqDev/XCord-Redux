package org.eclipse.sisu.space;

import java.io.File;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

final class FileEntryIterator implements Iterator<String> {
   private final String rootPath;
   private final Deque<String> entryNames = new ArrayDeque<>();
   private final boolean recurse;

   FileEntryIterator(URL var1, String var2, boolean var3) {
      this.rootPath = normalizePath(toFile(var1).getAbsoluteFile());
      this.recurse = var3;
      this.appendEntries(var2);
   }

   @Override
   public boolean hasNext() {
      return this.entryNames.size() > 0;
   }

   public String next() {
      String var1 = this.entryNames.removeFirst();
      if (this.recurse && var1.endsWith("/")) {
         this.appendEntries(var1);
      }

      return var1;
   }

   @Override
   public void remove() {
      throw new UnsupportedOperationException();
   }

   static File toFile(URL var0) {
      StringBuilder var1 = new StringBuilder();
      String var2 = var0.getAuthority();
      if (var2 != null && var2.length() > 0) {
         var1.append(File.separatorChar).append(File.separatorChar).append(var2);
      }

      String var3 = var0.getPath();
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;

      for (int var7 = var3.length(); var6 < var7; var6++) {
         char var8 = var3.charAt(var6);
         if ('/' == var8) {
            var1.append(File.separatorChar);
         } else if ('%' == var8 && var6 < var7 - 2) {
            int var9 = Character.digit(var3.charAt(var6 + 1), 16);
            int var10 = Character.digit(var3.charAt(var6 + 2), 16);
            if (var9 >= 0 && var10 >= 0) {
               if (var9 < 8) {
                  var1.append((char)(var9 << 4 | var10));
               } else if (var9 >= 12) {
                  var5 = 12 == var9 ? 1 : var9 - 12;
                  var4 = (13 == var9 ? 16 + var10 : var10) << 6 * var5;
               } else if (var5 > 0) {
                  var4 |= ((3 & var9) << 4 | var10) << 6 * --var5;
                  if (var5 == 0) {
                     var1.appendCodePoint(var4);
                  }
               }

               var6 += 2;
            } else {
               var1.append('%');
            }
         } else {
            var1.append(var8);
         }
      }

      return new File(var1.toString());
   }

   private void appendEntries(String var1) {
      File[] var2 = new File(this.rootPath + var1).listFiles();
      if (var2 != null) {
         for (File var3 : var2) {
            this.entryNames.add(normalizePath(var3).substring(this.rootPath.length()));
         }
      }
   }

   private static String normalizePath(File var0) {
      return var0.toURI().getPath();
   }
}
