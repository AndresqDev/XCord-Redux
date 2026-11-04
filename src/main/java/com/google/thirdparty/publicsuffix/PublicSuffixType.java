package com.google.thirdparty.publicsuffix;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;

@Beta
@GwtCompatible
public enum PublicSuffixType {
   PRIVATE(':', ','),
   REGISTRY('!', '?');

   private final char innerNodeCode;
   private final char leafNodeCode;

   private PublicSuffixType(char var3, char var4) {
      this.innerNodeCode = var3;
      this.leafNodeCode = var4;
   }

   char getLeafNodeCode() {
      return this.leafNodeCode;
   }

   char getInnerNodeCode() {
      return this.innerNodeCode;
   }

   static PublicSuffixType fromCode(char var0) {
      for (PublicSuffixType var4 : values()) {
         if (var4.getInnerNodeCode() == var0 || var4.getLeafNodeCode() == var0) {
            return var4;
         }
      }

      throw new IllegalArgumentException(new StringBuilder(38).append("No enum corresponding to given code: ").append(var0).toString());
   }
}
