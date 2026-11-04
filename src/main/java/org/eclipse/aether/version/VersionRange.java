package org.eclipse.aether.version;

import java.util.Objects;

public interface VersionRange {
   boolean containsVersion(Version var1);

   VersionRange.Bound getLowerBound();

   VersionRange.Bound getUpperBound();

   public static final class Bound {
      private final Version version;
      private final boolean inclusive;

      public Bound(Version var1, boolean var2) {
         this.version = Objects.requireNonNull(var1, "version cannot be null");
         this.inclusive = var2;
      }

      public Version getVersion() {
         return this.version;
      }

      public boolean isInclusive() {
         return this.inclusive;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (var1 != null && this.getClass().equals(var1.getClass())) {
            VersionRange.Bound var2 = (VersionRange.Bound)var1;
            return this.inclusive == var2.inclusive && this.version.equals(var2.version);
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         int var1 = 17;
         var1 = var1 * 31 + this.version.hashCode();
         return var1 * 31 + (this.inclusive ? 1 : 0);
      }

      @Override
      public String toString() {
         return String.valueOf(this.version);
      }
   }
}
