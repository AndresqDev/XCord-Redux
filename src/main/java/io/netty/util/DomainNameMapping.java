package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.IDN;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

@Deprecated
public class DomainNameMapping<V> implements Mapping<String, V> {
   final V defaultValue;
   private final Map<String, V> map;
   private final Map<String, V> unmodifiableMap;

   @Deprecated
   public DomainNameMapping(V var1) {
      this(4, (V)var1);
   }

   @Deprecated
   public DomainNameMapping(int var1, V var2) {
      this(new LinkedHashMap<>(var1), (V)var2);
   }

   DomainNameMapping(Map<String, V> var1, V var2) {
      this.defaultValue = ObjectUtil.checkNotNull((V)var2, "defaultValue");
      this.map = var1;
      this.unmodifiableMap = var1 != null ? Collections.unmodifiableMap(var1) : null;
   }

   @Deprecated
   public DomainNameMapping<V> add(String var1, V var2) {
      this.map.put(normalizeHostname(ObjectUtil.checkNotNull(var1, "hostname")), ObjectUtil.checkNotNull((V)var2, "output"));
      return this;
   }

   static boolean matches(String var0, String var1) {
      return !var0.startsWith("*.")
         ? var0.equals(var1)
         : var0.regionMatches(2, var1, 0, var1.length()) || StringUtil.commonSuffixOfLength(var1, var0, var0.length() - 1);
   }

   static String normalizeHostname(String var0) {
      if (needsNormalization(var0)) {
         var0 = IDN.toASCII(var0, 1);
      }

      return var0.toLowerCase(Locale.US);
   }

   private static boolean needsNormalization(String var0) {
      int var1 = var0.length();

      for (int var2 = 0; var2 < var1; var2++) {
         char var3 = var0.charAt(var2);
         if (var3 > 127) {
            return true;
         }
      }

      return false;
   }

   public V map(String var1) {
      if (var1 != null) {
         var1 = normalizeHostname(var1);

         for (Entry var3 : this.map.entrySet()) {
            if (matches((String)var3.getKey(), var1)) {
               return (V)var3.getValue();
            }
         }
      }

      return this.defaultValue;
   }

   public Map<String, V> asMap() {
      return this.unmodifiableMap;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this) + "(default: " + this.defaultValue + ", map: " + this.map + ')';
   }
}
