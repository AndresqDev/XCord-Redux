package org.eclipse.aether.util.version;

import java.util.ArrayList;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;
import org.eclipse.aether.version.VersionRange;
import org.eclipse.aether.version.VersionScheme;

public final class GenericVersionScheme implements VersionScheme {
   @Override
   public Version parseVersion(String var1) throws InvalidVersionSpecificationException {
      return new GenericVersion(var1);
   }

   @Override
   public VersionRange parseVersionRange(String var1) throws InvalidVersionSpecificationException {
      return new GenericVersionRange(var1);
   }

   @Override
   public VersionConstraint parseVersionConstraint(String var1) throws InvalidVersionSpecificationException {
      ArrayList var2 = new ArrayList();
      String var3 = var1;

      while (var3.startsWith("[") || var3.startsWith("(")) {
         int var4 = var3.indexOf(41);
         int var5 = var3.indexOf(93);
         int var6 = var5;
         if (var5 < 0 || var4 >= 0 && var4 < var5) {
            var6 = var4;
         }

         if (var6 < 0) {
            throw new InvalidVersionSpecificationException(var1, "Unbounded version range " + var1);
         }

         VersionRange var7 = this.parseVersionRange(var3.substring(0, var6 + 1));
         var2.add(var7);
         var3 = var3.substring(var6 + 1).trim();
         if (var3.length() > 0 && var3.startsWith(",")) {
            var3 = var3.substring(1).trim();
         }
      }

      if (var3.length() > 0 && !var2.isEmpty()) {
         throw new InvalidVersionSpecificationException(var1, "Invalid version range " + var1 + ", expected [ or ( but got " + var3);
      } else {
         GenericVersionConstraint var8;
         if (var2.isEmpty()) {
            var8 = new GenericVersionConstraint(this.parseVersion(var1));
         } else {
            var8 = new GenericVersionConstraint(UnionVersionRange.from(var2));
         }

         return var8;
      }
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 ? true : var1 != null && this.getClass().equals(var1.getClass());
   }

   @Override
   public int hashCode() {
      return this.getClass().hashCode();
   }
}
