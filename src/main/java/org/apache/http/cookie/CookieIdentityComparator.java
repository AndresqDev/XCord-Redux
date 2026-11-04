package org.apache.http.cookie;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class CookieIdentityComparator implements Serializable, Comparator<Cookie> {
   private static final long serialVersionUID = 4466565437490631532L;

   public int compare(Cookie var1, Cookie var2) {
      int var3 = var1.getName().compareTo(var2.getName());
      if (var3 == 0) {
         String var4 = var1.getDomain();
         if (var4 == null) {
            var4 = "";
         } else if (var4.indexOf(46) == -1) {
            var4 = var4 + ".local";
         }

         String var5 = var2.getDomain();
         if (var5 == null) {
            var5 = "";
         } else if (var5.indexOf(46) == -1) {
            var5 = var5 + ".local";
         }

         var3 = var4.compareToIgnoreCase(var5);
      }

      if (var3 == 0) {
         String var6 = var1.getPath();
         if (var6 == null) {
            var6 = "/";
         }

         String var7 = var2.getPath();
         if (var7 == null) {
            var7 = "/";
         }

         var3 = var6.compareTo(var7);
      }

      return var3;
   }
}
