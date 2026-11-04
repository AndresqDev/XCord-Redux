package org.eclipse.aether.util.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import org.eclipse.aether.graph.DependencyFilter;

public final class DependencyFilterUtils {
   private DependencyFilterUtils() {
   }

   public static DependencyFilter notFilter(DependencyFilter var0) {
      return new NotDependencyFilter(var0);
   }

   public static DependencyFilter andFilter(DependencyFilter... var0) {
      return (DependencyFilter)(var0 != null && var0.length == 1 ? var0[0] : new AndDependencyFilter(var0));
   }

   public static DependencyFilter andFilter(Collection<DependencyFilter> var0) {
      return (DependencyFilter)(var0 != null && var0.size() == 1 ? (DependencyFilter)var0.iterator().next() : new AndDependencyFilter(var0));
   }

   public static DependencyFilter orFilter(DependencyFilter... var0) {
      return (DependencyFilter)(var0 != null && var0.length == 1 ? var0[0] : new OrDependencyFilter(var0));
   }

   public static DependencyFilter orFilter(Collection<DependencyFilter> var0) {
      return (DependencyFilter)(var0 != null && var0.size() == 1 ? (DependencyFilter)var0.iterator().next() : new OrDependencyFilter(var0));
   }

   public static DependencyFilter classpathFilter(String... var0) {
      return classpathFilter(var0 != null ? Arrays.asList(var0) : null);
   }

   public static DependencyFilter classpathFilter(Collection<String> var0) {
      HashSet var1 = new HashSet();
      if (var0 != null) {
         for (String var3 : var0) {
            String[] var4 = var3.split("[+,]");

            for (String var8 : var4) {
               var8 = var8.trim();
               if (var8.length() > 0) {
                  var1.add(var8);
               }
            }
         }
      }

      HashSet var9 = new HashSet();

      for (String var12 : var1) {
         if ("compile".equals(var12)) {
            Collections.addAll(var9, "compile", "provided", "system");
         } else if ("runtime".equals(var12)) {
            Collections.addAll(var9, "compile", "runtime");
         } else if ("test".equals(var12)) {
            Collections.addAll(var9, "compile", "provided", "system", "runtime", "test");
         } else {
            var9.add(var12);
         }
      }

      HashSet var11 = new HashSet();
      Collections.addAll(var11, "compile", "provided", "system", "runtime", "test");
      var11.removeAll(var9);
      return new ScopeDependencyFilter(null, var11);
   }
}
