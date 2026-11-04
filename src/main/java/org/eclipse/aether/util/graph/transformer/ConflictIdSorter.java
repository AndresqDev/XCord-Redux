package org.eclipse.aether.util.graph.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.collection.DependencyGraphTransformationContext;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.graph.DependencyNode;

public final class ConflictIdSorter implements DependencyGraphTransformer {
   @Override
   public DependencyNode transformGraph(DependencyNode var1, DependencyGraphTransformationContext var2) throws RepositoryException {
      Map var3 = (Map)var2.get(TransformationContextKeys.CONFLICT_IDS);
      if (var3 == null) {
         ConflictMarker var4 = new ConflictMarker();
         var4.transformGraph(var1, var2);
         var3 = (Map)var2.get(TransformationContextKeys.CONFLICT_IDS);
      }

      Map var16 = (Map)var2.get(TransformationContextKeys.STATS);
      long var5 = System.nanoTime();
      LinkedHashMap var7 = new LinkedHashMap(256);
      ConflictIdSorter.ConflictId var8 = null;
      Object var9 = var3.get(var1);
      if (var9 != null) {
         var8 = new ConflictIdSorter.ConflictId(var9, 0);
         var7.put(var9, var8);
      }

      IdentityHashMap var10 = new IdentityHashMap(var3.size());
      this.buildConflitIdDAG(var7, var1, var8, 0, var10, var3);
      long var11 = System.nanoTime();
      int var13 = this.topsortConflictIds(var7.values(), var2);
      if (var16 != null) {
         long var14 = System.nanoTime();
         var16.put("ConflictIdSorter.graphTime", var11 - var5);
         var16.put("ConflictIdSorter.topsortTime", var14 - var11);
         var16.put("ConflictIdSorter.conflictIdCount", var7.size());
         var16.put("ConflictIdSorter.conflictIdCycleCount", var13);
      }

      return var1;
   }

   private void buildConflitIdDAG(
      Map<Object, ConflictIdSorter.ConflictId> var1,
      DependencyNode var2,
      ConflictIdSorter.ConflictId var3,
      int var4,
      Map<DependencyNode, Object> var5,
      Map<?, ?> var6
   ) {
      if (var5.put(var2, Boolean.TRUE) == null) {
         var4++;

         for (DependencyNode var8 : var2.getChildren()) {
            Object var9 = var6.get(var8);
            ConflictIdSorter.ConflictId var10 = (ConflictIdSorter.ConflictId)var1.get(var9);
            if (var10 == null) {
               var10 = new ConflictIdSorter.ConflictId(var9, var4);
               var1.put(var9, var10);
            } else {
               var10.pullup(var4);
            }

            if (var3 != null) {
               var3.add(var10);
            }

            this.buildConflitIdDAG(var1, var8, var10, var4, var5, var6);
         }
      }
   }

   private int topsortConflictIds(Collection<ConflictIdSorter.ConflictId> var1, DependencyGraphTransformationContext var2) {
      ArrayList var3 = new ArrayList(var1.size());
      ConflictIdSorter.RootQueue var4 = new ConflictIdSorter.RootQueue(var1.size() / 2);

      for (ConflictIdSorter.ConflictId var6 : var1) {
         if (var6.inDegree <= 0) {
            var4.add(var6);
         }
      }

      this.processRoots(var3, var4);
      boolean var9 = var3.size() < var1.size();

      while (var3.size() < var1.size()) {
         ConflictIdSorter.ConflictId var10 = null;

         for (ConflictIdSorter.ConflictId var8 : var1) {
            if (var8.inDegree > 0 && (var10 == null || var8.minDepth < var10.minDepth || var8.minDepth == var10.minDepth && var8.inDegree < var10.inDegree)) {
               var10 = var8;
            }
         }

         var10.inDegree = 0;
         var4.add(var10);
         this.processRoots(var3, var4);
      }

      Object var11 = Collections.emptySet();
      if (var9) {
         var11 = this.findCycles(var1);
      }

      var2.put(TransformationContextKeys.SORTED_CONFLICT_IDS, var3);
      var2.put(TransformationContextKeys.CYCLIC_CONFLICT_IDS, var11);
      return var11.size();
   }

   private void processRoots(List<Object> var1, ConflictIdSorter.RootQueue var2) {
      while (!var2.isEmpty()) {
         ConflictIdSorter.ConflictId var3 = var2.remove();
         var1.add(var3.key);

         for (ConflictIdSorter.ConflictId var5 : var3.children) {
            var5.inDegree--;
            if (var5.inDegree == 0) {
               var2.add(var5);
            }
         }
      }
   }

   private Collection<Collection<Object>> findCycles(Collection<ConflictIdSorter.ConflictId> var1) {
      HashSet var2 = new HashSet();
      HashMap var3 = new HashMap(128);
      IdentityHashMap var4 = new IdentityHashMap(var1.size());

      for (ConflictIdSorter.ConflictId var6 : var1) {
         this.findCycles(var6, var4, var3, var2);
      }

      return var2;
   }

   private void findCycles(
      ConflictIdSorter.ConflictId var1, Map<ConflictIdSorter.ConflictId, Object> var2, Map<Object, Integer> var3, Collection<Collection<Object>> var4
   ) {
      Integer var5 = var3.put(var1.key, var3.size());
      if (var5 != null) {
         var3.put(var1.key, var5);
         HashSet var6 = new HashSet();

         for (Entry var8 : var3.entrySet()) {
            if ((Integer)var8.getValue() >= var5) {
               var6.add(var8.getKey());
            }
         }

         var4.add(var6);
      } else {
         if (var2.put(var1, Boolean.TRUE) == null) {
            for (ConflictIdSorter.ConflictId var10 : var1.children) {
               this.findCycles(var10, var2, var3, var4);
            }
         }

         var3.remove(var1.key);
      }
   }

   static final class ConflictId {
      final Object key;
      Collection<ConflictIdSorter.ConflictId> children = Collections.emptySet();
      int inDegree;
      int minDepth;

      ConflictId(Object var1, int var2) {
         this.key = var1;
         this.minDepth = var2;
      }

      public void add(ConflictIdSorter.ConflictId var1) {
         if (this.children.isEmpty()) {
            this.children = new HashSet<>();
         }

         if (this.children.add(var1)) {
            var1.inDegree++;
         }
      }

      public void pullup(int var1) {
         if (var1 < this.minDepth) {
            this.minDepth = var1++;

            for (ConflictIdSorter.ConflictId var3 : this.children) {
               var3.pullup(var1);
            }
         }
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof ConflictIdSorter.ConflictId)) {
            return false;
         } else {
            ConflictIdSorter.ConflictId var2 = (ConflictIdSorter.ConflictId)var1;
            return this.key.equals(var2.key);
         }
      }

      @Override
      public int hashCode() {
         return this.key.hashCode();
      }

      @Override
      public String toString() {
         return this.key + " @ " + this.minDepth + " <" + this.inDegree;
      }
   }

   static final class RootQueue {
      private int nextOut;
      private int nextIn;
      private ConflictIdSorter.ConflictId[] ids;

      RootQueue(int var1) {
         this.ids = new ConflictIdSorter.ConflictId[var1 + 16];
      }

      boolean isEmpty() {
         return this.nextOut >= this.nextIn;
      }

      void add(ConflictIdSorter.ConflictId var1) {
         if (this.nextOut >= this.nextIn && this.nextOut > 0) {
            this.nextIn = this.nextIn - this.nextOut;
            this.nextOut = 0;
         }

         if (this.nextIn >= this.ids.length) {
            ConflictIdSorter.ConflictId[] var2 = new ConflictIdSorter.ConflictId[this.ids.length + this.ids.length / 2 + 16];
            System.arraycopy(this.ids, this.nextOut, var2, 0, this.nextIn - this.nextOut);
            this.ids = var2;
            this.nextIn = this.nextIn - this.nextOut;
            this.nextOut = 0;
         }

         int var3;
         for (var3 = this.nextIn - 1; var3 >= this.nextOut && var1.minDepth < this.ids[var3].minDepth; var3--) {
            this.ids[var3 + 1] = this.ids[var3];
         }

         this.ids[var3 + 1] = var1;
         this.nextIn++;
      }

      ConflictIdSorter.ConflictId remove() {
         return this.ids[this.nextOut++];
      }
   }
}
