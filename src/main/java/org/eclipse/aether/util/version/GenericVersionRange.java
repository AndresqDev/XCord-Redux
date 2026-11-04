package org.eclipse.aether.util.version;

import java.util.Objects;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionRange;

final class GenericVersionRange implements VersionRange {
   private final VersionRange.Bound lowerBound;
   private final VersionRange.Bound upperBound;

   GenericVersionRange(String var1) throws InvalidVersionSpecificationException {
      boolean var3;
      if (var1.startsWith("[")) {
         var3 = true;
      } else {
         if (!var1.startsWith("(")) {
            throw new InvalidVersionSpecificationException(var1, "Invalid version range " + var1 + ", a range must start with either [ or (");
         }

         var3 = false;
      }

      boolean var4;
      if (var1.endsWith("]")) {
         var4 = true;
      } else {
         if (!var1.endsWith(")")) {
            throw new InvalidVersionSpecificationException(var1, "Invalid version range " + var1 + ", a range must end with either [ or (");
         }

         var4 = false;
      }

      String var2 = var1.substring(1, var1.length() - 1);
      int var7 = var2.indexOf(",");
      Version var5;
      Version var6;
      if (var7 < 0) {
         if (!var3 || !var4) {
            throw new InvalidVersionSpecificationException(var1, "Invalid version range " + var1 + ", single version must be surrounded by []");
         }

         String var8 = var2.trim();
         if (var8.endsWith(".*")) {
            String var9 = var8.substring(0, var8.length() - 1);
            var5 = this.parse(var9 + "min");
            var6 = this.parse(var9 + "max");
         } else {
            var5 = this.parse(var8);
            var6 = var5;
         }
      } else {
         String var10 = var2.substring(0, var7).trim();
         String var11 = var2.substring(var7 + 1).trim();
         if (var11.contains(",")) {
            throw new InvalidVersionSpecificationException(var1, "Invalid version range " + var1 + ", bounds may not contain additional ','");
         }

         var5 = var10.length() > 0 ? this.parse(var10) : null;
         var6 = var11.length() > 0 ? this.parse(var11) : null;
         if (var6 != null && var5 != null && var6.compareTo(var5) < 0) {
            throw new InvalidVersionSpecificationException(var1, "Invalid version range " + var1 + ", lower bound must not be greater than upper bound");
         }
      }

      this.lowerBound = var5 != null ? new VersionRange.Bound(var5, var3) : null;
      this.upperBound = var6 != null ? new VersionRange.Bound(var6, var4) : null;
   }

   private Version parse(String var1) {
      return new GenericVersion(var1);
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
   public boolean containsVersion(Version var1) {
      if (this.lowerBound != null) {
         int var2 = this.lowerBound.getVersion().compareTo(var1);
         if (var2 == 0 && !this.lowerBound.isInclusive()) {
            return false;
         }

         if (var2 > 0) {
            return false;
         }
      }

      if (this.upperBound != null) {
         int var3 = this.upperBound.getVersion().compareTo(var1);
         if (var3 == 0 && !this.upperBound.isInclusive()) {
            return false;
         }

         if (var3 < 0) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         GenericVersionRange var2 = (GenericVersionRange)var1;
         return Objects.equals(this.upperBound, var2.upperBound) && Objects.equals(this.lowerBound, var2.lowerBound);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + hash(this.upperBound);
      return var1 * 31 + hash(this.lowerBound);
   }

   private static int hash(Object var0) {
      return var0 != null ? var0.hashCode() : 0;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(64);
      if (this.lowerBound != null) {
         var1.append((char)(this.lowerBound.isInclusive() ? '[' : '('));
         var1.append(this.lowerBound.getVersion());
      } else {
         var1.append('(');
      }

      var1.append(',');
      if (this.upperBound != null) {
         var1.append(this.upperBound.getVersion());
         var1.append((char)(this.upperBound.isInclusive() ? ']' : ')'));
      } else {
         var1.append(')');
      }

      return var1.toString();
   }
}
