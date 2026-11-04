package org.eclipse.aether.util.version;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionRange;

final class UnionVersionRange implements VersionRange {
   private final Set<VersionRange> ranges;
   private final VersionRange.Bound lowerBound;
   private final VersionRange.Bound upperBound;

   public static VersionRange from(VersionRange... var0) {
      return var0 == null ? from(Collections.emptySet()) : from(Arrays.asList(var0));
   }

   public static VersionRange from(Collection<? extends VersionRange> var0) {
      return (VersionRange)(var0 != null && var0.size() == 1 ? (VersionRange)var0.iterator().next() : new UnionVersionRange(var0));
   }

   private UnionVersionRange(Collection<? extends VersionRange> var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.ranges = new HashSet<>(var1);
         VersionRange.Bound var2 = null;
         VersionRange.Bound var3 = null;

         for (VersionRange var5 : this.ranges) {
            VersionRange.Bound var6 = var5.getLowerBound();
            if (var6 == null) {
               var2 = null;
               break;
            }

            if (var2 == null) {
               var2 = var6;
            } else {
               int var7 = var6.getVersion().compareTo(var2.getVersion());
               if (var7 < 0 || var7 == 0 && !var2.isInclusive()) {
                  var2 = var6;
               }
            }
         }

         for (VersionRange var9 : this.ranges) {
            VersionRange.Bound var10 = var9.getUpperBound();
            if (var10 == null) {
               var3 = null;
               break;
            }

            if (var3 == null) {
               var3 = var10;
            } else {
               int var11 = var10.getVersion().compareTo(var3.getVersion());
               if (var11 > 0 || var11 == 0 && !var3.isInclusive()) {
                  var3 = var10;
               }
            }
         }

         this.lowerBound = var2;
         this.upperBound = var3;
      } else {
         this.ranges = Collections.emptySet();
         this.lowerBound = null;
         this.upperBound = null;
      }
   }

   @Override
   public boolean containsVersion(Version var1) {
      for (VersionRange var3 : this.ranges) {
         if (var3.containsVersion(var1)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public VersionRange.Bound getLowerBound() {
      return this.lowerBound;
   }

   @Override
   public VersionRange.Bound getUpperBound() {
      return this.upperBound;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         UnionVersionRange var2 = (UnionVersionRange)var1;
         return this.ranges.equals(var2.ranges);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 97 * this.ranges.hashCode();
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(128);

      for (VersionRange var3 : this.ranges) {
         if (var1.length() > 0) {
            var1.append(", ");
         }

         var1.append(var3);
      }

      return var1.toString();
   }
}
