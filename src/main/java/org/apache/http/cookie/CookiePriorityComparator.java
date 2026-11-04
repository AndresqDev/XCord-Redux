package org.apache.http.cookie;

import java.util.Comparator;
import java.util.Date;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.impl.cookie.BasicClientCookie;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class CookiePriorityComparator implements Comparator<Cookie> {
   public static final CookiePriorityComparator INSTANCE = new CookiePriorityComparator();

   private int getPathLength(Cookie var1) {
      String var2 = var1.getPath();
      return var2 != null ? var2.length() : 1;
   }

   public int compare(Cookie var1, Cookie var2) {
      int var3 = this.getPathLength(var1);
      int var4 = this.getPathLength(var2);
      int var5 = var4 - var3;
      if (var5 == 0 && var1 instanceof BasicClientCookie && var2 instanceof BasicClientCookie) {
         Date var6 = ((BasicClientCookie)var1).getCreationDate();
         Date var7 = ((BasicClientCookie)var2).getCreationDate();
         if (var6 != null && var7 != null) {
            return (int)(var6.getTime() - var7.getTime());
         }
      }

      return var5;
   }
}
