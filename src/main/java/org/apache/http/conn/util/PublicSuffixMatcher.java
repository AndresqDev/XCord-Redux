package org.apache.http.conn.util;

import java.net.IDN;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public final class PublicSuffixMatcher {
   private final Map<String, DomainType> rules;
   private final Map<String, DomainType> exceptions;

   public PublicSuffixMatcher(Collection<String> var1, Collection<String> var2) {
      this(DomainType.UNKNOWN, var1, var2);
   }

   public PublicSuffixMatcher(DomainType var1, Collection<String> var2, Collection<String> var3) {
      Args.notNull(var1, "Domain type");
      Args.notNull(var2, "Domain suffix rules");
      this.rules = new ConcurrentHashMap<>(var2.size());

      for (String var5 : var2) {
         this.rules.put(var5, var1);
      }

      this.exceptions = new ConcurrentHashMap<>();
      if (var3 != null) {
         for (String var7 : var3) {
            this.exceptions.put(var7, var1);
         }
      }
   }

   public PublicSuffixMatcher(Collection<PublicSuffixList> var1) {
      Args.notNull(var1, "Domain suffix lists");
      this.rules = new ConcurrentHashMap<>();
      this.exceptions = new ConcurrentHashMap<>();

      for (PublicSuffixList var3 : var1) {
         DomainType var4 = var3.getType();

         for (String var7 : var3.getRules()) {
            this.rules.put(var7, var4);
         }

         List var9 = var3.getExceptions();
         if (var9 != null) {
            for (String var8 : var9) {
               this.exceptions.put(var8, var4);
            }
         }
      }
   }

   private static DomainType findEntry(Map<String, DomainType> var0, String var1) {
      return var0 == null ? null : (DomainType)var0.get(var1);
   }

   private static boolean match(DomainType var0, DomainType var1) {
      return var0 != null && (var1 == null || var0.equals(var1));
   }

   public String getDomainRoot(String var1) {
      return this.getDomainRoot(var1, null);
   }

   public String getDomainRoot(String var1, DomainType var2) {
      if (var1 == null) {
         return null;
      } else if (var1.startsWith(".")) {
         return null;
      } else {
         String var3 = DnsUtils.normalize(var1);
         String var4 = var3;
         String var5 = null;

         while (var4 != null) {
            String var6 = IDN.toUnicode(var4);
            DomainType var7 = findEntry(this.exceptions, var6);
            if (match(var7, var2)) {
               return var4;
            }

            DomainType var8 = findEntry(this.rules, var6);
            if (match(var8, var2)) {
               if (var8 == DomainType.PRIVATE) {
                  return var4;
               }

               return var5;
            }

            int var9 = var4.indexOf(46);
            String var10 = var9 != -1 ? var4.substring(var9 + 1) : null;
            if (var10 != null) {
               DomainType var11 = findEntry(this.rules, "*." + IDN.toUnicode(var10));
               if (match(var11, var2)) {
                  if (var11 == DomainType.PRIVATE) {
                     return var4;
                  }

                  return var5;
               }
            }

            var5 = var4;
            var4 = var10;
         }

         return var2 != null && var2 != DomainType.UNKNOWN ? null : var5;
      }
   }

   public boolean matches(String var1) {
      return this.matches(var1, null);
   }

   public boolean matches(String var1, DomainType var2) {
      if (var1 == null) {
         return false;
      } else {
         String var3 = this.getDomainRoot(var1.startsWith(".") ? var1.substring(1) : var1, var2);
         return var3 == null;
      }
   }
}
