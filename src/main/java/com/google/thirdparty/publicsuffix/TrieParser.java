package com.google.thirdparty.publicsuffix;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Queues;
import java.util.Deque;

@GwtCompatible
final class TrieParser {
   private static final Joiner PREFIX_JOINER = Joiner.on("");

   static ImmutableMap<String, PublicSuffixType> parseTrie(CharSequence var0) {
      ImmutableMap.Builder var1 = ImmutableMap.builder();
      int var2 = var0.length();
      int var3 = 0;

      while (var3 < var2) {
         var3 += doParseTrieToBuilder(Queues.newArrayDeque(), var0, var3, var1);
      }

      return var1.build();
   }

   private static int doParseTrieToBuilder(Deque<CharSequence> var0, CharSequence var1, int var2, ImmutableMap.Builder<String, PublicSuffixType> var3) {
      int var4 = var1.length();
      int var5 = var2;

      char var6;
      for (var6 = 0; var5 < var4; var5++) {
         var6 = var1.charAt(var5);
         if (var6 == '&' || var6 == '?' || var6 == '!' || var6 == ':' || var6 == ',') {
            break;
         }
      }

      var0.push(reverse(var1.subSequence(var2, var5)));
      if (var6 == '!' || var6 == '?' || var6 == ':' || var6 == ',') {
         String var7 = PREFIX_JOINER.join(var0);
         if (var7.length() > 0) {
            var3.put(var7, PublicSuffixType.fromCode(var6));
         }
      }

      var5++;
      if (var6 != '?' && var6 != ',') {
         while (var5 < var4) {
            var5 += doParseTrieToBuilder(var0, var1, var5, var3);
            if (var1.charAt(var5) == '?' || var1.charAt(var5) == ',') {
               var5++;
               break;
            }
         }
      }

      var0.pop();
      return var5 - var2;
   }

   private static CharSequence reverse(CharSequence var0) {
      return new StringBuilder(var0).reverse();
   }
}
