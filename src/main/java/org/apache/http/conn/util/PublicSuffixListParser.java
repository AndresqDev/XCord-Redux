package org.apache.http.conn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public final class PublicSuffixListParser {
   public PublicSuffixList parse(Reader var1) throws IOException {
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();
      BufferedReader var4 = new BufferedReader(var1);

      String var5;
      while ((var5 = var4.readLine()) != null) {
         if (!var5.isEmpty() && !var5.startsWith("//")) {
            if (var5.startsWith(".")) {
               var5 = var5.substring(1);
            }

            boolean var6 = var5.startsWith("!");
            if (var6) {
               var5 = var5.substring(1);
            }

            if (var6) {
               var3.add(var5);
            } else {
               var2.add(var5);
            }
         }
      }

      return new PublicSuffixList(DomainType.UNKNOWN, var2, var3);
   }

   public List<PublicSuffixList> parseByType(Reader var1) throws IOException {
      ArrayList var2 = new ArrayList(2);
      BufferedReader var3 = new BufferedReader(var1);
      new StringBuilder(256);
      DomainType var5 = null;
      ArrayList var6 = null;
      ArrayList var7 = null;

      String var8;
      while ((var8 = var3.readLine()) != null) {
         if (!var8.isEmpty()) {
            if (var8.startsWith("//")) {
               if (var5 == null) {
                  if (var8.contains("===BEGIN ICANN DOMAINS===")) {
                     var5 = DomainType.ICANN;
                  } else if (var8.contains("===BEGIN PRIVATE DOMAINS===")) {
                     var5 = DomainType.PRIVATE;
                  }
               } else if (var8.contains("===END ICANN DOMAINS===") || var8.contains("===END PRIVATE DOMAINS===")) {
                  if (var6 != null) {
                     var2.add(new PublicSuffixList(var5, var6, var7));
                  }

                  var5 = null;
                  var6 = null;
                  var7 = null;
               }
            } else if (var5 != null) {
               if (var8.startsWith(".")) {
                  var8 = var8.substring(1);
               }

               boolean var9 = var8.startsWith("!");
               if (var9) {
                  var8 = var8.substring(1);
               }

               if (var9) {
                  if (var7 == null) {
                     var7 = new ArrayList();
                  }

                  var7.add(var8);
               } else {
                  if (var6 == null) {
                     var6 = new ArrayList();
                  }

                  var6.add(var8);
               }
            }
         }
      }

      return var2;
   }
}
