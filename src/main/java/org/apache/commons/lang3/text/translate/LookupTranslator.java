package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;

@Deprecated
public class LookupTranslator extends CharSequenceTranslator {
   private final HashMap<String, String> lookupMap = new HashMap<>();
   private final HashSet<Character> prefixSet = new HashSet<>();
   private final int shortest;
   private final int longest;

   public LookupTranslator(CharSequence[]... var1) {
      int var2 = Integer.MAX_VALUE;
      int var3 = 0;
      if (var1 != null) {
         for (CharSequence[] var7 : var1) {
            this.lookupMap.put(var7[0].toString(), var7[1].toString());
            this.prefixSet.add(var7[0].charAt(0));
            int var8 = var7[0].length();
            if (var8 < var2) {
               var2 = var8;
            }

            if (var8 > var3) {
               var3 = var8;
            }
         }
      }

      this.shortest = var2;
      this.longest = var3;
   }

   @Override
   public int translate(CharSequence var1, int var2, Writer var3) throws IOException {
      if (this.prefixSet.contains(var1.charAt(var2))) {
         int var4 = this.longest;
         if (var2 + this.longest > var1.length()) {
            var4 = var1.length() - var2;
         }

         for (int var5 = var4; var5 >= this.shortest; var5--) {
            CharSequence var6 = var1.subSequence(var2, var2 + var5);
            String var7 = this.lookupMap.get(var6.toString());
            if (var7 != null) {
               var3.write(var7);
               return var5;
            }
         }
      }

      return 0;
   }
}
