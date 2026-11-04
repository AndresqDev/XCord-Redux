package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DomainWildcardMappingBuilder<V> {
   private final V defaultValue;
   private final Map<String, V> map;

   public DomainWildcardMappingBuilder(V var1) {
      this(4, (V)var1);
   }

   public DomainWildcardMappingBuilder(int var1, V var2) {
      this.defaultValue = ObjectUtil.checkNotNull((V)var2, "defaultValue");
      this.map = new LinkedHashMap<>(var1);
   }

   public DomainWildcardMappingBuilder<V> add(String var1, V var2) {
      this.map.put(this.normalizeHostName(var1), ObjectUtil.checkNotNull((V)var2, "output"));
      return this;
   }

   private String normalizeHostName(String var1) {
      ObjectUtil.checkNotNull(var1, "hostname");
      if (!var1.isEmpty() && var1.charAt(0) != '.') {
         var1 = DomainWildcardMappingBuilder.ImmutableDomainWildcardMapping.normalize(ObjectUtil.checkNotNull(var1, "hostname"));
         if (var1.charAt(0) != '*') {
            return var1;
         } else if (var1.length() >= 3 && var1.charAt(1) == '.') {
            return var1.substring(1);
         } else {
            throw new IllegalArgumentException("Wildcard Hostname '" + var1 + "'not valid");
         }
      } else {
         throw new IllegalArgumentException("Hostname '" + var1 + "' not valid");
      }
   }

   public Mapping<String, V> build() {
      return new DomainWildcardMappingBuilder.ImmutableDomainWildcardMapping<>(this.defaultValue, this.map);
   }

   private static final class ImmutableDomainWildcardMapping<V> implements Mapping<String, V> {
      private static final String REPR_HEADER = "ImmutableDomainWildcardMapping(default: ";
      private static final String REPR_MAP_OPENING = ", map: ";
      private static final String REPR_MAP_CLOSING = ")";
      private final V defaultValue;
      private final Map<String, V> map;

      ImmutableDomainWildcardMapping(V var1, Map<String, V> var2) {
         this.defaultValue = (V)var1;
         this.map = new LinkedHashMap<>(var2);
      }

      public V map(String var1) {
         if (var1 != null) {
            var1 = normalize(var1);
            Object var2 = this.map.get(var1);
            if (var2 != null) {
               return (V)var2;
            }

            int var3 = var1.indexOf(46);
            if (var3 != -1) {
               var2 = this.map.get(var1.substring(var3));
               if (var2 != null) {
                  return (V)var2;
               }
            }
         }

         return this.defaultValue;
      }

      static String normalize(String var0) {
         return DomainNameMapping.normalizeHostname(var0);
      }

      @Override
      public String toString() {
         StringBuilder var1 = new StringBuilder();
         var1.append("ImmutableDomainWildcardMapping(default: ").append(this.defaultValue).append(", map: ").append('{');

         for (Entry var3 : this.map.entrySet()) {
            String var4 = (String)var3.getKey();
            if (var4.charAt(0) == '.') {
               var4 = '*' + var4;
            }

            var1.append(var4).append('=').append(var3.getValue()).append(", ");
         }

         var1.setLength(var1.length() - 2);
         return var1.append('}').append(")").toString();
      }
   }
}
