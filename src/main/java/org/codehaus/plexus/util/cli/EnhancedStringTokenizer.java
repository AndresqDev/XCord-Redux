package org.codehaus.plexus.util.cli;

import java.util.StringTokenizer;

public final class EnhancedStringTokenizer {
   private StringTokenizer cst = null;
   String cdelim;
   final boolean cdelimSingleChar;
   final char cdelimChar;
   boolean creturnDelims;
   String lastToken = null;
   boolean delimLast = true;

   public EnhancedStringTokenizer(String var1) {
      this(var1, " \t\n\r\f", false);
   }

   public EnhancedStringTokenizer(String var1, String var2) {
      this(var1, var2, false);
   }

   public EnhancedStringTokenizer(String var1, String var2, boolean var3) {
      this.cst = new StringTokenizer(var1, var2, true);
      this.cdelim = var2;
      this.creturnDelims = var3;
      this.cdelimSingleChar = var2.length() == 1;
      this.cdelimChar = var2.charAt(0);
   }

   public boolean hasMoreTokens() {
      return this.cst.hasMoreTokens();
   }

   private String internalNextToken() {
      if (this.lastToken != null) {
         String var2 = this.lastToken;
         this.lastToken = null;
         return var2;
      } else {
         String var1 = this.cst.nextToken();
         if (this.isDelim(var1)) {
            if (this.delimLast) {
               this.lastToken = var1;
               return "";
            } else {
               this.delimLast = true;
               return var1;
            }
         } else {
            this.delimLast = false;
            return var1;
         }
      }
   }

   public String nextToken() {
      String var1 = this.internalNextToken();
      if (this.creturnDelims) {
         return var1;
      } else if (this.isDelim(var1)) {
         return this.hasMoreTokens() ? this.internalNextToken() : "";
      } else {
         return var1;
      }
   }

   private boolean isDelim(String var1) {
      if (var1.length() == 1) {
         char var2 = var1.charAt(0);
         if (this.cdelimSingleChar) {
            if (this.cdelimChar == var2) {
               return true;
            }
         } else if (this.cdelim.indexOf(var2) >= 0) {
            return true;
         }
      }

      return false;
   }
}
