package org.eclipse.aether.util.graph.selector;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;

public final class ExclusionDependencySelector implements DependencySelector {
   private final Exclusion[] exclusions;
   private int hashCode;

   public ExclusionDependencySelector() {
      this.exclusions = new Exclusion[0];
   }

   public ExclusionDependencySelector(Collection<Exclusion> var1) {
      if (var1 != null && !var1.isEmpty()) {
         TreeSet var2 = new TreeSet<>(ExclusionDependencySelector.ExclusionComparator.INSTANCE);
         var2.addAll(var1);
         this.exclusions = var2.toArray(new Exclusion[var2.size()]);
      } else {
         this.exclusions = new Exclusion[0];
      }
   }

   private ExclusionDependencySelector(Exclusion[] var1) {
      this.exclusions = var1;
   }

   @Override
   public boolean selectDependency(Dependency var1) {
      Artifact var2 = var1.getArtifact();

      for (Exclusion var6 : this.exclusions) {
         if (this.matches(var6, var2)) {
            return false;
         }
      }

      return true;
   }

   private boolean matches(Exclusion var1, Artifact var2) {
      if (!this.matches(var1.getArtifactId(), var2.getArtifactId())) {
         return false;
      } else if (!this.matches(var1.getGroupId(), var2.getGroupId())) {
         return false;
      } else {
         return !this.matches(var1.getExtension(), var2.getExtension()) ? false : this.matches(var1.getClassifier(), var2.getClassifier());
      }
   }

   private boolean matches(String var1, String var2) {
      return "*".equals(var1) || var1.equals(var2);
   }

   @Override
   public DependencySelector deriveChildSelector(DependencyCollectionContext var1) {
      Dependency var2 = var1.getDependency();
      Collection var3 = var2 != null ? var2.getExclusions() : null;
      if (var3 != null && !var3.isEmpty()) {
         Exclusion[] var4 = this.exclusions;
         int var5 = var4.length;

         for (Exclusion var7 : var3) {
            int var8 = Arrays.binarySearch(var4, var7, ExclusionDependencySelector.ExclusionComparator.INSTANCE);
            if (var8 < 0) {
               var8 = -(var8 + 1);
               if (var5 >= var4.length) {
                  Exclusion[] var9 = new Exclusion[var4.length + var3.size()];
                  System.arraycopy(var4, 0, var9, 0, var8);
                  var9[var8] = var7;
                  System.arraycopy(var4, var8, var9, var8 + 1, var5 - var8);
                  var4 = var9;
               } else {
                  System.arraycopy(var4, var8, var4, var8 + 1, var5 - var8);
                  var4[var8] = var7;
               }

               var5++;
            }
         }

         if (var4 == this.exclusions) {
            return this;
         } else {
            if (var4.length != var5) {
               Exclusion[] var10 = new Exclusion[var5];
               System.arraycopy(var4, 0, var10, 0, var5);
               var4 = var10;
            }

            return new ExclusionDependencySelector(var4);
         }
      } else {
         return this;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (null != var1 && this.getClass().equals(var1.getClass())) {
         ExclusionDependencySelector var2 = (ExclusionDependencySelector)var1;
         return Arrays.equals((Object[])this.exclusions, (Object[])var2.exclusions);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      if (this.hashCode == 0) {
         int var1 = this.getClass().hashCode();
         var1 = var1 * 31 + Arrays.hashCode((Object[])this.exclusions);
         this.hashCode = var1;
      }

      return this.hashCode;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder().append(this.getClass().getSimpleName()).append('(');

      for (int var2 = 0; var2 < this.exclusions.length; var2++) {
         var1.append(this.exclusions[var2]);
         if (var2 < this.exclusions.length - 1) {
            var1.append(", ");
         }
      }

      return var1.append(')').toString();
   }

   private static class ExclusionComparator implements Comparator<Exclusion> {
      static final ExclusionDependencySelector.ExclusionComparator INSTANCE = new ExclusionDependencySelector.ExclusionComparator();

      public int compare(Exclusion var1, Exclusion var2) {
         if (var1 == null) {
            return var2 == null ? 0 : 1;
         } else if (var2 == null) {
            return -1;
         } else {
            int var3 = var1.getArtifactId().compareTo(var2.getArtifactId());
            if (var3 == 0) {
               var3 = var1.getGroupId().compareTo(var2.getGroupId());
               if (var3 == 0) {
                  var3 = var1.getExtension().compareTo(var2.getExtension());
                  if (var3 == 0) {
                     var3 = var1.getClassifier().compareTo(var2.getClassifier());
                  }
               }
            }

            return var3;
         }
      }
   }
}
