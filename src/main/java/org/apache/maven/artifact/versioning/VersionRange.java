package org.apache.maven.artifact.versioning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import org.apache.maven.artifact.Artifact;

public class VersionRange {
   private static final Map<String, VersionRange> CACHE_SPEC = Collections.synchronizedMap(new WeakHashMap<>());
   private static final Map<String, VersionRange> CACHE_VERSION = Collections.synchronizedMap(new WeakHashMap<>());
   private final ArtifactVersion recommendedVersion;
   private final List<Restriction> restrictions;

   private VersionRange(ArtifactVersion var1, List<Restriction> var2) {
      this.recommendedVersion = var1;
      this.restrictions = var2;
   }

   public ArtifactVersion getRecommendedVersion() {
      return this.recommendedVersion;
   }

   public List<Restriction> getRestrictions() {
      return this.restrictions;
   }

   @Deprecated
   public VersionRange cloneOf() {
      ArrayList var1 = null;
      if (this.restrictions != null) {
         var1 = new ArrayList();
         if (!this.restrictions.isEmpty()) {
            var1.addAll(this.restrictions);
         }
      }

      return new VersionRange(this.recommendedVersion, var1);
   }

   public static VersionRange createFromVersionSpec(String var0) throws InvalidVersionSpecificationException {
      if (var0 == null) {
         return null;
      } else {
         VersionRange var1 = CACHE_SPEC.get(var0);
         if (var1 != null) {
            return var1;
         } else {
            ArrayList var2 = new ArrayList();
            String var3 = var0;
            DefaultArtifactVersion var4 = null;
            ArtifactVersion var5 = null;
            ArtifactVersion var6 = null;

            while (var3.startsWith("[") || var3.startsWith("(")) {
               int var7 = var3.indexOf(41);
               int var8 = var3.indexOf(93);
               int var9 = var8;
               if ((var8 < 0 || var7 < var8) && var7 >= 0) {
                  var9 = var7;
               }

               if (var9 < 0) {
                  throw new InvalidVersionSpecificationException("Unbounded range: " + var0);
               }

               Restriction var10 = parseRestriction(var3.substring(0, var9 + 1));
               if (var6 == null) {
                  var6 = var10.getLowerBound();
               }

               if (var5 != null && (var10.getLowerBound() == null || var10.getLowerBound().compareTo(var5) < 0)) {
                  throw new InvalidVersionSpecificationException("Ranges overlap: " + var0);
               }

               var2.add(var10);
               var5 = var10.getUpperBound();
               var3 = var3.substring(var9 + 1).trim();
               if (var3.length() > 0 && var3.startsWith(",")) {
                  var3 = var3.substring(1).trim();
               }
            }

            if (var3.length() > 0) {
               if (var2.size() > 0) {
                  throw new InvalidVersionSpecificationException("Only fully-qualified sets allowed in multiple set scenario: " + var0);
               }

               var4 = new DefaultArtifactVersion(var3);
               var2.add(Restriction.EVERYTHING);
            }

            var1 = new VersionRange(var4, var2);
            CACHE_SPEC.put(var0, var1);
            return var1;
         }
      }
   }

   private static Restriction parseRestriction(String var0) throws InvalidVersionSpecificationException {
      boolean var1 = var0.startsWith("[");
      boolean var2 = var0.endsWith("]");
      String var3 = var0.substring(1, var0.length() - 1).trim();
      int var5 = var3.indexOf(44);
      Restriction var4;
      if (var5 < 0) {
         if (!var1 || !var2) {
            throw new InvalidVersionSpecificationException("Single version must be surrounded by []: " + var0);
         }

         DefaultArtifactVersion var6 = new DefaultArtifactVersion(var3);
         var4 = new Restriction(var6, var1, var6, var2);
      } else {
         String var10 = var3.substring(0, var5).trim();
         String var7 = var3.substring(var5 + 1).trim();
         if (var10.equals(var7)) {
            throw new InvalidVersionSpecificationException("Range cannot have identical boundaries: " + var0);
         }

         DefaultArtifactVersion var8 = null;
         if (var10.length() > 0) {
            var8 = new DefaultArtifactVersion(var10);
         }

         DefaultArtifactVersion var9 = null;
         if (var7.length() > 0) {
            var9 = new DefaultArtifactVersion(var7);
         }

         if (var9 != null && var8 != null && var9.compareTo(var8) < 0) {
            throw new InvalidVersionSpecificationException("Range defies version ordering: " + var0);
         }

         var4 = new Restriction(var8, var1, var9, var2);
      }

      return var4;
   }

   public static VersionRange createFromVersion(String var0) {
      VersionRange var1 = CACHE_VERSION.get(var0);
      if (var1 == null) {
         List var2 = Collections.emptyList();
         var1 = new VersionRange(new DefaultArtifactVersion(var0), var2);
         CACHE_VERSION.put(var0, var1);
      }

      return var1;
   }

   public VersionRange restrict(VersionRange var1) {
      List var2 = this.restrictions;
      List var3 = var1.restrictions;
      List var4;
      if (!var2.isEmpty() && !var3.isEmpty()) {
         var4 = Collections.unmodifiableList(this.intersection(var2, var3));
      } else {
         var4 = Collections.emptyList();
      }

      ArtifactVersion var5 = null;
      if (var4.size() > 0) {
         for (Restriction var7 : var4) {
            if (this.recommendedVersion != null && var7.containsVersion(this.recommendedVersion)) {
               var5 = this.recommendedVersion;
               break;
            }

            if (var5 == null && var1.getRecommendedVersion() != null && var7.containsVersion(var1.getRecommendedVersion())) {
               var5 = var1.getRecommendedVersion();
            }
         }
      } else if (this.recommendedVersion != null) {
         var5 = this.recommendedVersion;
      } else if (var1.recommendedVersion != null) {
         var5 = var1.recommendedVersion;
      }

      return new VersionRange(var5, var4);
   }

   private List<Restriction> intersection(List<Restriction> var1, List<Restriction> var2) {
      ArrayList var3 = new ArrayList(var1.size() + var2.size());
      Iterator var4 = var1.iterator();
      Iterator var5 = var2.iterator();
      Restriction var6 = (Restriction)var4.next();
      Restriction var7 = (Restriction)var5.next();
      boolean var8 = false;

      while (!var8) {
         if (var6.getLowerBound() != null && var7.getUpperBound() != null && var6.getLowerBound().compareTo(var7.getUpperBound()) > 0) {
            if (var5.hasNext()) {
               var7 = (Restriction)var5.next();
            } else {
               var8 = true;
            }
         } else if (var6.getUpperBound() != null && var7.getLowerBound() != null && var6.getUpperBound().compareTo(var7.getLowerBound()) < 0) {
            if (var4.hasNext()) {
               var6 = (Restriction)var4.next();
            } else {
               var8 = true;
            }
         } else {
            ArtifactVersion var9;
            boolean var11;
            if (var6.getLowerBound() == null) {
               var9 = var7.getLowerBound();
               var11 = var7.isLowerBoundInclusive();
            } else if (var7.getLowerBound() == null) {
               var9 = var6.getLowerBound();
               var11 = var6.isLowerBoundInclusive();
            } else {
               int var13 = var6.getLowerBound().compareTo(var7.getLowerBound());
               if (var13 < 0) {
                  var9 = var7.getLowerBound();
                  var11 = var7.isLowerBoundInclusive();
               } else if (var13 == 0) {
                  var9 = var6.getLowerBound();
                  var11 = var6.isLowerBoundInclusive() && var7.isLowerBoundInclusive();
               } else {
                  var9 = var6.getLowerBound();
                  var11 = var6.isLowerBoundInclusive();
               }
            }

            ArtifactVersion var10;
            boolean var12;
            if (var6.getUpperBound() == null) {
               var10 = var7.getUpperBound();
               var12 = var7.isUpperBoundInclusive();
            } else if (var7.getUpperBound() == null) {
               var10 = var6.getUpperBound();
               var12 = var6.isUpperBoundInclusive();
            } else {
               int var14 = var6.getUpperBound().compareTo(var7.getUpperBound());
               if (var14 < 0) {
                  var10 = var6.getUpperBound();
                  var12 = var6.isUpperBoundInclusive();
               } else if (var14 == 0) {
                  var10 = var6.getUpperBound();
                  var12 = var6.isUpperBoundInclusive() && var7.isUpperBoundInclusive();
               } else {
                  var10 = var7.getUpperBound();
                  var12 = var7.isUpperBoundInclusive();
               }
            }

            if (var9 == null || var10 == null || var9.compareTo(var10) != 0) {
               var3.add(new Restriction(var9, var11, var10, var12));
            } else if (var11 && var12) {
               var3.add(new Restriction(var9, var11, var10, var12));
            }

            if (var10 == var7.getUpperBound()) {
               if (var5.hasNext()) {
                  var7 = (Restriction)var5.next();
               } else {
                  var8 = true;
               }
            } else if (var4.hasNext()) {
               var6 = (Restriction)var4.next();
            } else {
               var8 = true;
            }
         }
      }

      return var3;
   }

   public ArtifactVersion getSelectedVersion(Artifact var1) throws OverConstrainedVersionException {
      ArtifactVersion var2;
      if (this.recommendedVersion != null) {
         var2 = this.recommendedVersion;
      } else {
         if (this.restrictions.size() == 0) {
            throw new OverConstrainedVersionException("The artifact has no valid ranges", var1);
         }

         var2 = null;
      }

      return var2;
   }

   public boolean isSelectedVersionKnown(Artifact var1) throws OverConstrainedVersionException {
      boolean var2 = false;
      if (this.recommendedVersion != null) {
         var2 = true;
      } else if (this.restrictions.size() == 0) {
         throw new OverConstrainedVersionException("The artifact has no valid ranges", var1);
      }

      return var2;
   }

   @Override
   public String toString() {
      if (this.recommendedVersion != null) {
         return this.recommendedVersion.toString();
      } else {
         StringBuilder var1 = new StringBuilder();
         Iterator var2 = this.restrictions.iterator();

         while (var2.hasNext()) {
            Restriction var3 = (Restriction)var2.next();
            var1.append(var3.toString());
            if (var2.hasNext()) {
               var1.append(',');
            }
         }

         return var1.toString();
      }
   }

   public ArtifactVersion matchVersion(List<ArtifactVersion> var1) {
      ArtifactVersion var2 = null;

      for (ArtifactVersion var4 : var1) {
         if (this.containsVersion(var4) && (var2 == null || var4.compareTo(var2) > 0)) {
            var2 = var4;
         }
      }

      return var2;
   }

   public boolean containsVersion(ArtifactVersion var1) {
      for (Restriction var3 : this.restrictions) {
         if (var3.containsVersion(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasRestrictions() {
      return !this.restrictions.isEmpty() && this.recommendedVersion == null;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof VersionRange)) {
         return false;
      } else {
         VersionRange var2 = (VersionRange)var1;
         return Objects.equals(this.recommendedVersion, var2.recommendedVersion) && Objects.equals(this.restrictions, var2.restrictions);
      }
   }

   @Override
   public int hashCode() {
      int var1 = 7;
      var1 = 31 * var1 + (this.recommendedVersion == null ? 0 : this.recommendedVersion.hashCode());
      return 31 * var1 + (this.restrictions == null ? 0 : this.restrictions.hashCode());
   }
}
