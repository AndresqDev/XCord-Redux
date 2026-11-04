package org.apache.maven.artifact.versioning;

public class Restriction {
   private final ArtifactVersion lowerBound;
   private final boolean lowerBoundInclusive;
   private final ArtifactVersion upperBound;
   private final boolean upperBoundInclusive;
   public static final Restriction EVERYTHING = new Restriction(null, false, null, false);

   public Restriction(ArtifactVersion var1, boolean var2, ArtifactVersion var3, boolean var4) {
      this.lowerBound = var1;
      this.lowerBoundInclusive = var2;
      this.upperBound = var3;
      this.upperBoundInclusive = var4;
   }

   public ArtifactVersion getLowerBound() {
      return this.lowerBound;
   }

   public boolean isLowerBoundInclusive() {
      return this.lowerBoundInclusive;
   }

   public ArtifactVersion getUpperBound() {
      return this.upperBound;
   }

   public boolean isUpperBoundInclusive() {
      return this.upperBoundInclusive;
   }

   public boolean containsVersion(ArtifactVersion var1) {
      if (this.lowerBound != null) {
         int var2 = this.lowerBound.compareTo(var1);
         if (var2 == 0 && !this.lowerBoundInclusive) {
            return false;
         }

         if (var2 > 0) {
            return false;
         }
      }

      if (this.upperBound != null) {
         int var3 = this.upperBound.compareTo(var1);
         if (var3 == 0 && !this.upperBoundInclusive) {
            return false;
         }

         if (var3 < 0) {
            return false;
         }
      }

      return true;
   }

   @Override
   public int hashCode() {
      int var1 = 13;
      if (this.lowerBound == null) {
         var1++;
      } else {
         var1 += this.lowerBound.hashCode();
      }

      var1 *= this.lowerBoundInclusive ? 1 : 2;
      if (this.upperBound == null) {
         var1 -= 3;
      } else {
         var1 -= this.upperBound.hashCode();
      }

      return var1 * (this.upperBoundInclusive ? 2 : 3);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Restriction)) {
         return false;
      } else {
         Restriction var2 = (Restriction)var1;
         if (this.lowerBound != null) {
            if (!this.lowerBound.equals(var2.lowerBound)) {
               return false;
            }
         } else if (var2.lowerBound != null) {
            return false;
         }

         if (this.lowerBoundInclusive != var2.lowerBoundInclusive) {
            return false;
         } else {
            if (this.upperBound != null) {
               if (!this.upperBound.equals(var2.upperBound)) {
                  return false;
               }
            } else if (var2.upperBound != null) {
               return false;
            }

            return this.upperBoundInclusive == var2.upperBoundInclusive;
         }
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append((char)(this.isLowerBoundInclusive() ? '[' : '('));
      if (this.getLowerBound() != null) {
         var1.append(this.getLowerBound().toString());
      }

      var1.append(',');
      if (this.getUpperBound() != null) {
         var1.append(this.getUpperBound().toString());
      }

      var1.append((char)(this.isUpperBoundInclusive() ? ']' : ')'));
      return var1.toString();
   }
}
