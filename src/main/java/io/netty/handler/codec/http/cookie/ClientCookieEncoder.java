package io.netty.handler.codec.http.cookie;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

public final class ClientCookieEncoder extends CookieEncoder {
   public static final ClientCookieEncoder STRICT = new ClientCookieEncoder(true);
   public static final ClientCookieEncoder LAX = new ClientCookieEncoder(false);
   static final Comparator<Cookie> COOKIE_COMPARATOR = new Comparator<Cookie>() {
      public int compare(Cookie var1, Cookie var2) {
         String var3 = var1.path();
         String var4 = var2.path();
         int var5 = var3 == null ? Integer.MAX_VALUE : var3.length();
         int var6 = var4 == null ? Integer.MAX_VALUE : var4.length();
         return var6 - var5;
      }
   };

   private ClientCookieEncoder(boolean var1) {
      super(var1);
   }

   public String encode(String var1, String var2) {
      return this.encode(new DefaultCookie(var1, var2));
   }

   public String encode(Cookie var1) {
      StringBuilder var2 = CookieUtil.stringBuilder();
      this.encode(var2, ObjectUtil.checkNotNull(var1, "cookie"));
      return CookieUtil.stripTrailingSeparator(var2);
   }

   public String encode(Cookie... var1) {
      if (ObjectUtil.checkNotNull(var1, "cookies").length == 0) {
         return null;
      } else {
         StringBuilder var2 = CookieUtil.stringBuilder();
         if (this.strict) {
            if (var1.length == 1) {
               this.encode(var2, var1[0]);
            } else {
               Cookie[] var3 = Arrays.copyOf(var1, var1.length);
               Arrays.sort(var3, COOKIE_COMPARATOR);

               for (Cookie var7 : var3) {
                  this.encode(var2, var7);
               }
            }
         } else {
            for (Cookie var11 : var1) {
               this.encode(var2, var11);
            }
         }

         return CookieUtil.stripTrailingSeparatorOrNull(var2);
      }
   }

   public String encode(Collection<? extends Cookie> var1) {
      if (ObjectUtil.checkNotNull(var1, "cookies").isEmpty()) {
         return null;
      } else {
         StringBuilder var2 = CookieUtil.stringBuilder();
         if (this.strict) {
            if (var1.size() == 1) {
               this.encode(var2, (Cookie)var1.iterator().next());
            } else {
               Cookie[] var3 = var1.toArray(new Cookie[0]);
               Arrays.sort(var3, COOKIE_COMPARATOR);

               for (Cookie var7 : var3) {
                  this.encode(var2, var7);
               }
            }
         } else {
            for (Cookie var9 : var1) {
               this.encode(var2, var9);
            }
         }

         return CookieUtil.stripTrailingSeparatorOrNull(var2);
      }
   }

   public String encode(Iterable<? extends Cookie> var1) {
      Iterator var2 = ObjectUtil.checkNotNull(var1, "cookies").iterator();
      if (!var2.hasNext()) {
         return null;
      } else {
         StringBuilder var3 = CookieUtil.stringBuilder();
         if (this.strict) {
            Cookie var4 = (Cookie)var2.next();
            if (!var2.hasNext()) {
               this.encode(var3, var4);
            } else {
               ArrayList var5 = InternalThreadLocalMap.get().arrayList();
               var5.add(var4);

               while (var2.hasNext()) {
                  var5.add(var2.next());
               }

               Cookie[] var6 = var5.toArray(new Cookie[0]);
               Arrays.sort(var6, COOKIE_COMPARATOR);

               for (Cookie var10 : var6) {
                  this.encode(var3, var10);
               }
            }
         } else {
            while (var2.hasNext()) {
               this.encode(var3, (Cookie)var2.next());
            }
         }

         return CookieUtil.stripTrailingSeparatorOrNull(var3);
      }
   }

   private void encode(StringBuilder var1, Cookie var2) {
      String var3 = var2.name();
      String var4 = var2.value() != null ? var2.value() : "";
      this.validateCookie(var3, var4);
      if (var2.wrap()) {
         CookieUtil.addQuoted(var1, var3, var4);
      } else {
         CookieUtil.add(var1, var3, var4);
      }
   }
}
