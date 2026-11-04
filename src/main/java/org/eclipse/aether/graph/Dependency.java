package org.eclipse.aether.graph;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import org.eclipse.aether.artifact.Artifact;

public final class Dependency {
   private final Artifact artifact;
   private final String scope;
   private final Boolean optional;
   private final Set<Exclusion> exclusions;

   public Dependency(Artifact var1, String var2) {
      this(var1, var2, false);
   }

   public Dependency(Artifact var1, String var2, Boolean var3) {
      this(var1, var2, var3, null);
   }

   public Dependency(Artifact var1, String var2, Boolean var3, Collection<Exclusion> var4) {
      this(var1, var2, Dependency.Exclusions.copy(var4), var3);
   }

   private Dependency(Artifact var1, String var2, Set<Exclusion> var3, Boolean var4) {
      this.artifact = Objects.requireNonNull(var1, "artifact cannot be null");
      this.scope = var2 != null ? var2 : "";
      this.optional = var4;
      this.exclusions = var3;
   }

   public Artifact getArtifact() {
      return this.artifact;
   }

   public Dependency setArtifact(Artifact var1) {
      return this.artifact.equals(var1) ? this : new Dependency(var1, this.scope, this.exclusions, this.optional);
   }

   public String getScope() {
      return this.scope;
   }

   public Dependency setScope(String var1) {
      return !this.scope.equals(var1) && (var1 != null || this.scope.length() > 0) ? new Dependency(this.artifact, var1, this.exclusions, this.optional) : this;
   }

   public boolean isOptional() {
      return Boolean.TRUE.equals(this.optional);
   }

   public Boolean getOptional() {
      return this.optional;
   }

   public Dependency setOptional(Boolean var1) {
      return Objects.equals(this.optional, var1) ? this : new Dependency(this.artifact, this.scope, this.exclusions, var1);
   }

   public Collection<Exclusion> getExclusions() {
      return this.exclusions;
   }

   public Dependency setExclusions(Collection<Exclusion> var1) {
      return this.hasEquivalentExclusions(var1) ? this : new Dependency(this.artifact, this.scope, this.optional, var1);
   }

   private boolean hasEquivalentExclusions(Collection<Exclusion> var1) {
      if (var1 == null || var1.isEmpty()) {
         return this.exclusions.isEmpty();
      } else {
         return var1 instanceof Set
            ? this.exclusions.equals(var1)
            : var1.size() >= this.exclusions.size() && this.exclusions.containsAll(var1) && var1.containsAll(this.exclusions);
      }
   }

   @Override
   public String toString() {
      return this.getArtifact() + " (" + this.getScope() + (this.isOptional() ? "?" : "") + ")";
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         Dependency var2 = (Dependency)var1;
         return Objects.equals(this.artifact, var2.artifact)
            && Objects.equals(this.scope, var2.scope)
            && Objects.equals(this.optional, var2.optional)
            && Objects.equals(this.exclusions, var2.exclusions);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + this.artifact.hashCode();
      var1 = var1 * 31 + this.scope.hashCode();
      var1 = var1 * 31 + (this.optional != null ? this.optional.hashCode() : 0);
      return var1 * 31 + this.exclusions.size();
   }

   private static class Exclusions extends AbstractSet<Exclusion> {
      private final Exclusion[] exclusions;

      public static Set<Exclusion> copy(Collection<Exclusion> var0) {
         return (Set<Exclusion>)(var0 != null && !var0.isEmpty() ? new Dependency.Exclusions(var0) : Collections.emptySet());
      }

      private Exclusions(Collection<Exclusion> var1) {
         if (var1.size() > 1 && !(var1 instanceof Set)) {
            var1 = new LinkedHashSet((Collection)var1);
         }

         this.exclusions = var1.toArray(new Exclusion[var1.size()]);
      }

      @Override
      public Iterator<Exclusion> iterator() {
         return new Iterator<Exclusion>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
               return this.cursor < Exclusions.this.exclusions.length;
            }

            public Exclusion next() {
               try {
                  Exclusion var1 = Exclusions.this.exclusions[this.cursor];
                  this.cursor++;
                  return var1;
               } catch (IndexOutOfBoundsException var2) {
                  throw new NoSuchElementException();
               }
            }

            @Override
            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }

      @Override
      public int size() {
         return this.exclusions.length;
      }
   }
}
