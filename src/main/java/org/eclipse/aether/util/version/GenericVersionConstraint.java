package org.eclipse.aether.util.version;

import java.util.Objects;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;
import org.eclipse.aether.version.VersionRange;

final class GenericVersionConstraint implements VersionConstraint {
   private final VersionRange range;
   private final Version version;

   GenericVersionConstraint(VersionRange var1) {
      this.range = Objects.requireNonNull(var1, "version range cannot be null");
      this.version = null;
   }

   GenericVersionConstraint(Version var1) {
      this.version = Objects.requireNonNull(var1, "version cannot be null");
      this.range = null;
   }

   @Override
   public VersionRange getRange() {
      return this.range;
   }

   @Override
   public Version getVersion() {
      return this.version;
   }

   @Override
   public boolean containsVersion(Version var1) {
      return this.range == null ? var1.equals(this.version) : this.range.containsVersion(var1);
   }

   @Override
   public String toString() {
      return String.valueOf(this.range == null ? this.version : this.range);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         GenericVersionConstraint var2 = (GenericVersionConstraint)var1;
         return Objects.equals(this.range, var2.range) && Objects.equals(this.version, var2.getVersion());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + hash(this.getRange());
      return var1 * 31 + hash(this.getVersion());
   }

   private static int hash(Object var0) {
      return var0 != null ? var0.hashCode() : 0;
   }
}
