package org.eclipse.aether.util.graph.transformer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyGraphTransformationContext;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;

public final class ConflictMarker implements DependencyGraphTransformer {
   @Override
   public DependencyNode transformGraph(DependencyNode var1, DependencyGraphTransformationContext var2) throws RepositoryException {
      Map var3 = (Map)var2.get(TransformationContextKeys.STATS);
      long var4 = System.nanoTime();
      IdentityHashMap var6 = new IdentityHashMap(1024);
      HashMap var7 = new HashMap(1024);
      this.analyze(var1, var6, var7, new int[]{0});
      long var8 = System.nanoTime();
      Map var10 = this.mark(var6.keySet(), var7);
      var2.put(TransformationContextKeys.CONFLICT_IDS, var10);
      if (var3 != null) {
         long var11 = System.nanoTime();
         var3.put("ConflictMarker.analyzeTime", var8 - var4);
         var3.put("ConflictMarker.markTime", var11 - var8);
         var3.put("ConflictMarker.nodeCount", var6.size());
      }

      return var1;
   }

   private void analyze(DependencyNode var1, Map<DependencyNode, Object> var2, Map<Object, ConflictMarker.ConflictGroup> var3, int[] var4) {
      if (var2.put(var1, Boolean.TRUE) == null) {
         Set var5 = this.getKeys(var1);
         if (!var5.isEmpty()) {
            ConflictMarker.ConflictGroup var6 = null;
            boolean var7 = false;

            for (Object var9 : var5) {
               ConflictMarker.ConflictGroup var10 = (ConflictMarker.ConflictGroup)var3.get(var9);
               if (var6 != var10) {
                  if (var6 == null) {
                     Set var11 = this.merge(var10.keys, var5);
                     if (var11 == var10.keys) {
                        var6 = var10;
                        break;
                     }

                     var6 = new ConflictMarker.ConflictGroup(var11, (int)(var4[0]++));
                     var7 = true;
                  } else if (var10 == null) {
                     var7 = true;
                  } else {
                     Set var16 = this.merge(var10.keys, var6.keys);
                     if (var16 == var10.keys) {
                        var6 = var10;
                        var7 = false;
                        break;
                     }

                     if (var16 != var6.keys) {
                        var6 = new ConflictMarker.ConflictGroup(var16, (int)(var4[0]++));
                        var7 = true;
                     }
                  }
               }
            }

            if (var6 == null) {
               var6 = new ConflictMarker.ConflictGroup(var5, (int)(var4[0]++));
               var7 = true;
            }

            if (var7) {
               for (Object var15 : var6.keys) {
                  var3.put(var15, var6);
               }
            }
         }

         for (DependencyNode var13 : var1.getChildren()) {
            this.analyze(var13, var2, var3, var4);
         }
      }
   }

   private Set<Object> merge(Set<Object> var1, Set<Object> var2) {
      int var3 = var1.size();
      int var4 = var2.size();
      if (var3 < var4) {
         if (var2.containsAll(var1)) {
            return var2;
         }
      } else if (var1.containsAll(var2)) {
         return var1;
      }

      HashSet var5 = new HashSet();
      var5.addAll(var1);
      var5.addAll(var2);
      return var5;
   }

   private Set<Object> getKeys(DependencyNode var1) {
      Dependency var3 = var1.getDependency();
      Object var2;
      if (var3 == null) {
         var2 = Collections.emptySet();
      } else {
         Object var4 = toKey(var3.getArtifact());
         if (var1.getRelocations().isEmpty() && var1.getAliases().isEmpty()) {
            var2 = Collections.singleton(var4);
         } else {
            var2 = new HashSet();
            var2.add(var4);

            for (Artifact var6 : var1.getRelocations()) {
               var4 = toKey(var6);
               var2.add(var4);
            }

            for (Artifact var10 : var1.getAliases()) {
               var4 = toKey(var10);
               var2.add(var4);
            }
         }
      }

      return (Set<Object>)var2;
   }

   private Map<DependencyNode, Object> mark(Collection<DependencyNode> var1, Map<Object, ConflictMarker.ConflictGroup> var2) {
      IdentityHashMap var3 = new IdentityHashMap(var1.size() + 1);

      for (DependencyNode var5 : var1) {
         Dependency var6 = var5.getDependency();
         if (var6 != null) {
            Object var7 = toKey(var6.getArtifact());
            var3.put(var5, ((ConflictMarker.ConflictGroup)var2.get(var7)).index);
         }
      }

      return var3;
   }

   private static Object toKey(Artifact var0) {
      return new ConflictMarker.Key(var0);
   }

   static class ConflictGroup {
      final Set<Object> keys;
      final int index;

      ConflictGroup(Set<Object> var1, int var2) {
         this.keys = var1;
         this.index = var2;
      }

      @Override
      public String toString() {
         return String.valueOf(this.keys);
      }
   }

   static class Key {
      private final Artifact artifact;

      Key(Artifact var1) {
         this.artifact = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ConflictMarker.Key)) {
            return false;
         } else {
            ConflictMarker.Key var2 = (ConflictMarker.Key)var1;
            return this.artifact.getArtifactId().equals(var2.artifact.getArtifactId())
               && this.artifact.getGroupId().equals(var2.artifact.getGroupId())
               && this.artifact.getExtension().equals(var2.artifact.getExtension())
               && this.artifact.getClassifier().equals(var2.artifact.getClassifier());
         }
      }

      @Override
      public int hashCode() {
         int var1 = 17;
         var1 = var1 * 31 + this.artifact.getArtifactId().hashCode();
         var1 = var1 * 31 + this.artifact.getGroupId().hashCode();
         var1 = var1 * 31 + this.artifact.getClassifier().hashCode();
         return var1 * 31 + this.artifact.getExtension().hashCode();
      }

      @Override
      public String toString() {
         return this.artifact.getGroupId() + ':' + this.artifact.getArtifactId() + ':' + this.artifact.getClassifier() + ':' + this.artifact.getExtension();
      }
   }
}
