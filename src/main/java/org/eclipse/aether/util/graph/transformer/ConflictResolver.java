package org.eclipse.aether.util.graph.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyGraphTransformationContext;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.util.ConfigUtils;

public final class ConflictResolver implements DependencyGraphTransformer {
   public static final String CONFIG_PROP_VERBOSE = "aether.conflictResolver.verbose";
   public static final String NODE_DATA_WINNER = "conflict.winner";
   public static final String NODE_DATA_ORIGINAL_SCOPE = "conflict.originalScope";
   public static final String NODE_DATA_ORIGINAL_OPTIONALITY = "conflict.originalOptionality";
   private final ConflictResolver.VersionSelector versionSelector;
   private final ConflictResolver.ScopeSelector scopeSelector;
   private final ConflictResolver.ScopeDeriver scopeDeriver;
   private final ConflictResolver.OptionalitySelector optionalitySelector;

   public ConflictResolver(
      ConflictResolver.VersionSelector var1, ConflictResolver.ScopeSelector var2, ConflictResolver.OptionalitySelector var3, ConflictResolver.ScopeDeriver var4
   ) {
      this.versionSelector = Objects.requireNonNull(var1, "version selector cannot be null");
      this.scopeSelector = Objects.requireNonNull(var2, "scope selector cannot be null");
      this.optionalitySelector = Objects.requireNonNull(var3, "optionality selector cannot be null");
      this.scopeDeriver = Objects.requireNonNull(var4, "scope deriver cannot be null");
   }

   @Override
   public DependencyNode transformGraph(DependencyNode var1, DependencyGraphTransformationContext var2) throws RepositoryException {
      List var3 = (List)var2.get(TransformationContextKeys.SORTED_CONFLICT_IDS);
      if (var3 == null) {
         ConflictIdSorter var4 = new ConflictIdSorter();
         var4.transformGraph(var1, var2);
         var3 = (List)var2.get(TransformationContextKeys.SORTED_CONFLICT_IDS);
      }

      Map var15 = (Map)var2.get(TransformationContextKeys.STATS);
      long var5 = System.nanoTime();
      Collection var7 = (Collection)var2.get(TransformationContextKeys.CYCLIC_CONFLICT_IDS);
      if (var7 == null) {
         throw new RepositoryException("conflict id cycles have not been identified");
      } else {
         Map var8 = (Map)var2.get(TransformationContextKeys.CONFLICT_IDS);
         if (var8 == null) {
            throw new RepositoryException("conflict groups have not been identified");
         } else {
            HashMap var9 = new HashMap();

            for (Collection var11 : var7) {
               for (Object var13 : var11) {
                  Object var14 = (Collection)var9.get(var13);
                  if (var14 == null) {
                     var14 = new HashSet();
                     var9.put(var13, var14);
                  }

                  var14.addAll(var11);
               }
            }

            ConflictResolver.State var16 = new ConflictResolver.State(var1, var8, var3.size(), var2);
            Iterator var17 = var3.iterator();

            while (var17.hasNext()) {
               Object var19 = var17.next();
               var16.prepare(var19, (Collection<Object>)var9.get(var19));
               this.gatherConflictItems(var1, var16);
               var16.finish();
               if (!var16.items.isEmpty()) {
                  ConflictResolver.ConflictContext var20 = var16.conflictCtx;
                  var16.versionSelector.selectVersion(var20);
                  if (var20.winner == null) {
                     throw new RepositoryException("conflict resolver did not select winner among " + var16.items);
                  }

                  DependencyNode var22 = var20.winner.node;
                  var16.scopeSelector.selectScope(var20);
                  if (var16.verbose) {
                     var22.setData("conflict.originalScope", var22.getDependency().getScope());
                  }

                  var22.setScope(var20.scope);
                  var16.optionalitySelector.selectOptionality(var20);
                  if (var16.verbose) {
                     var22.setData("conflict.originalOptionality", var22.getDependency().isOptional());
                  }

                  var22.setOptional(var20.optional);
                  this.removeLosers(var16);
               }

               var16.winner();
               if (!var17.hasNext() && !var7.isEmpty() && var16.conflictCtx.winner != null) {
                  DependencyNode var21 = var16.conflictCtx.winner.node;
                  var16.prepare(var16, null);
                  this.gatherConflictItems(var21, var16);
               }
            }

            if (var15 != null) {
               long var18 = System.nanoTime();
               var15.put("ConflictResolver.totalTime", var18 - var5);
               var15.put("ConflictResolver.conflictItemCount", var16.totalConflictItems);
            }

            return var1;
         }
      }
   }

   private boolean gatherConflictItems(DependencyNode var1, ConflictResolver.State var2) throws RepositoryException {
      Object var3 = var2.conflictIds.get(var1);
      if (var2.currentId.equals(var3)) {
         var2.add(var1);
      } else {
         if (var2.loser(var1, var3)) {
            return false;
         }

         if (var2.push(var1, var3)) {
            Iterator var4 = var1.getChildren().iterator();

            while (var4.hasNext()) {
               DependencyNode var5 = (DependencyNode)var4.next();
               if (!this.gatherConflictItems(var5, var2)) {
                  var4.remove();
               }
            }

            var2.pop();
         }
      }

      return true;
   }

   private void removeLosers(ConflictResolver.State var1) {
      ConflictResolver.ConflictItem var2 = var1.conflictCtx.winner;
      List var3 = null;
      ListIterator var4 = null;
      boolean var5 = false;

      for (ConflictResolver.ConflictItem var7 : var1.items) {
         if (var7 != var2) {
            if (var7.parent != var3) {
               var4 = var7.parent.listIterator();
               var3 = var7.parent;
               var5 = false;
            }

            while (var4.hasNext()) {
               DependencyNode var8 = (DependencyNode)var4.next();
               if (var8 == var7.node) {
                  if (var1.verbose && !var5 && var7.parent != var2.parent) {
                     var5 = true;
                     DefaultDependencyNode var9 = new DefaultDependencyNode(var8);
                     var9.setData("conflict.winner", var2.node);
                     var9.setData("conflict.originalScope", var9.getDependency().getScope());
                     var9.setData("conflict.originalOptionality", var9.getDependency().isOptional());
                     var9.setScope(var7.getScopes().iterator().next());
                     var9.setChildren(Collections.emptyList());
                     var4.set(var9);
                  } else {
                     var4.remove();
                  }
                  break;
               }
            }
         }
      }
   }

   public static final class ConflictContext {
      final DependencyNode root;
      final Map<?, ?> conflictIds;
      final Collection<ConflictResolver.ConflictItem> items;
      Object conflictId;
      ConflictResolver.ConflictItem winner;
      String scope;
      Boolean optional;

      ConflictContext(DependencyNode var1, Map<?, ?> var2, Collection<ConflictResolver.ConflictItem> var3) {
         this.root = var1;
         this.conflictIds = var2;
         this.items = Collections.unmodifiableCollection(var3);
      }

      public ConflictContext(DependencyNode var1, Object var2, Map<DependencyNode, Object> var3, Collection<ConflictResolver.ConflictItem> var4) {
         this(var1, var3, var4);
         this.conflictId = var2;
      }

      public DependencyNode getRoot() {
         return this.root;
      }

      public boolean isIncluded(DependencyNode var1) {
         return this.conflictId.equals(this.conflictIds.get(var1));
      }

      public Collection<ConflictResolver.ConflictItem> getItems() {
         return this.items;
      }

      public ConflictResolver.ConflictItem getWinner() {
         return this.winner;
      }

      public void setWinner(ConflictResolver.ConflictItem var1) {
         this.winner = var1;
      }

      public String getScope() {
         return this.scope;
      }

      public void setScope(String var1) {
         this.scope = var1;
      }

      public Boolean getOptional() {
         return this.optional;
      }

      public void setOptional(Boolean var1) {
         this.optional = var1;
      }

      @Override
      public String toString() {
         return this.winner + " @ " + this.scope + " < " + this.items;
      }
   }

   public static final class ConflictItem {
      final List<DependencyNode> parent;
      final Artifact artifact;
      final DependencyNode node;
      int depth;
      Object scopes;
      int optionalities;
      public static final int OPTIONAL_FALSE = 1;
      public static final int OPTIONAL_TRUE = 2;

      ConflictItem(DependencyNode var1, DependencyNode var2, String var3, boolean var4) {
         if (var1 != null) {
            this.parent = var1.getChildren();
            this.artifact = var1.getArtifact();
         } else {
            this.parent = null;
            this.artifact = null;
         }

         this.node = var2;
         this.scopes = var3;
         this.optionalities = var4 ? 2 : 1;
      }

      public ConflictItem(DependencyNode var1, DependencyNode var2, int var3, int var4, String... var5) {
         this.parent = var1 != null ? var1.getChildren() : null;
         this.artifact = var1 != null ? var1.getArtifact() : null;
         this.node = var2;
         this.depth = var3;
         this.optionalities = var4;
         this.scopes = Arrays.asList(var5);
      }

      public boolean isSibling(ConflictResolver.ConflictItem var1) {
         return this.parent == var1.parent;
      }

      public DependencyNode getNode() {
         return this.node;
      }

      public Dependency getDependency() {
         return this.node.getDependency();
      }

      public int getDepth() {
         return this.depth;
      }

      public Collection<String> getScopes() {
         return (Collection<String>)(this.scopes instanceof String ? Collections.singleton((String)this.scopes) : (Collection)this.scopes);
      }

      void addScope(String var1) {
         if (this.scopes instanceof Collection) {
            ((Collection)this.scopes).add(var1);
         } else if (!this.scopes.equals(var1)) {
            HashSet var2 = new HashSet();
            var2.add(this.scopes);
            var2.add(var1);
            this.scopes = var2;
         }
      }

      public int getOptionalities() {
         return this.optionalities;
      }

      void addOptional(boolean var1) {
         this.optionalities |= var1 ? 2 : 1;
      }

      @Override
      public String toString() {
         return this.node + " @ " + this.depth + " < " + this.artifact;
      }
   }

   static final class NodeInfo {
      int minDepth;
      Object derivedScopes;
      int derivedOptionalities;
      List<ConflictResolver.ConflictItem> children;
      static final int CHANGE_SCOPE = 1;
      static final int CHANGE_OPTIONAL = 2;
      private static final int OPT_FALSE = 1;
      private static final int OPT_TRUE = 2;

      NodeInfo(int var1, String var2, boolean var3) {
         this.minDepth = var1;
         this.derivedScopes = var2;
         this.derivedOptionalities = var3 ? 2 : 1;
      }

      int update(int var1, String var2, boolean var3) {
         if (var1 < this.minDepth) {
            this.minDepth = var1;
         }

         int var4;
         if (this.derivedScopes.equals(var2)) {
            var4 = 0;
         } else if (this.derivedScopes instanceof Collection) {
            var4 = ((Collection)this.derivedScopes).add(var2) ? 1 : 0;
         } else {
            HashSet var5 = new HashSet();
            var5.add((String)this.derivedScopes);
            var5.add(var2);
            this.derivedScopes = var5;
            var4 = 1;
         }

         int var6 = var3 ? 2 : 1;
         if ((this.derivedOptionalities & var6) == 0) {
            this.derivedOptionalities |= var6;
            var4 |= 2;
         }

         return var4;
      }

      void add(ConflictResolver.ConflictItem var1) {
         if (this.children == null) {
            this.children = new ArrayList<>(1);
         }

         this.children.add(var1);
      }
   }

   public abstract static class OptionalitySelector {
      public ConflictResolver.OptionalitySelector getInstance(DependencyNode var1, DependencyGraphTransformationContext var2) throws RepositoryException {
         return this;
      }

      public abstract void selectOptionality(ConflictResolver.ConflictContext var1) throws RepositoryException;
   }

   public static final class ScopeContext {
      String parentScope;
      String childScope;
      String derivedScope;

      public ScopeContext(String var1, String var2) {
         this.parentScope = var1 != null ? var1 : "";
         this.derivedScope = var2 != null ? var2 : "";
         this.childScope = var2 != null ? var2 : "";
      }

      public String getParentScope() {
         return this.parentScope;
      }

      public String getChildScope() {
         return this.childScope;
      }

      public String getDerivedScope() {
         return this.derivedScope;
      }

      public void setDerivedScope(String var1) {
         this.derivedScope = var1 != null ? var1 : "";
      }
   }

   public abstract static class ScopeDeriver {
      public ConflictResolver.ScopeDeriver getInstance(DependencyNode var1, DependencyGraphTransformationContext var2) throws RepositoryException {
         return this;
      }

      public abstract void deriveScope(ConflictResolver.ScopeContext var1) throws RepositoryException;
   }

   public abstract static class ScopeSelector {
      public ConflictResolver.ScopeSelector getInstance(DependencyNode var1, DependencyGraphTransformationContext var2) throws RepositoryException {
         return this;
      }

      public abstract void selectScope(ConflictResolver.ConflictContext var1) throws RepositoryException;
   }

   final class State {
      Object currentId;
      int totalConflictItems;
      final boolean verbose;
      final Map<Object, DependencyNode> resolvedIds;
      final Collection<Object> potentialAncestorIds;
      final Map<?, ?> conflictIds;
      final List<ConflictResolver.ConflictItem> items;
      final Map<List<DependencyNode>, ConflictResolver.NodeInfo> infos;
      final Map<List<DependencyNode>, Object> stack;
      final List<DependencyNode> parentNodes;
      final List<String> parentScopes;
      final List<Boolean> parentOptionals;
      final List<ConflictResolver.NodeInfo> parentInfos;
      final ConflictResolver.ConflictContext conflictCtx;
      final ConflictResolver.ScopeContext scopeCtx;
      final ConflictResolver.VersionSelector versionSelector;
      final ConflictResolver.ScopeSelector scopeSelector;
      final ConflictResolver.ScopeDeriver scopeDeriver;
      final ConflictResolver.OptionalitySelector optionalitySelector;

      State(DependencyNode var2, Map<?, ?> var3, int var4, DependencyGraphTransformationContext var5) throws RepositoryException {
         this.conflictIds = var3;
         this.verbose = ConfigUtils.getBoolean(var5.getSession(), false, "aether.conflictResolver.verbose");
         this.potentialAncestorIds = new HashSet<>(var4 * 2);
         this.resolvedIds = new HashMap<>(var4 * 2);
         this.items = new ArrayList<>(256);
         this.infos = new IdentityHashMap<>(64);
         this.stack = new IdentityHashMap<>(64);
         this.parentNodes = new ArrayList<>(64);
         this.parentScopes = new ArrayList<>(64);
         this.parentOptionals = new ArrayList<>(64);
         this.parentInfos = new ArrayList<>(64);
         this.conflictCtx = new ConflictResolver.ConflictContext(var2, var3, this.items);
         this.scopeCtx = new ConflictResolver.ScopeContext(null, null);
         this.versionSelector = ConflictResolver.this.versionSelector.getInstance(var2, var5);
         this.scopeSelector = ConflictResolver.this.scopeSelector.getInstance(var2, var5);
         this.scopeDeriver = ConflictResolver.this.scopeDeriver.getInstance(var2, var5);
         this.optionalitySelector = ConflictResolver.this.optionalitySelector.getInstance(var2, var5);
      }

      void prepare(Object var1, Collection<Object> var2) {
         this.currentId = var1;
         this.conflictCtx.conflictId = var1;
         this.conflictCtx.winner = null;
         this.conflictCtx.scope = null;
         this.conflictCtx.optional = null;
         this.items.clear();
         this.infos.clear();
         if (var2 != null) {
            this.potentialAncestorIds.addAll(var2);
         }
      }

      void finish() {
         List var1 = null;
         int var2 = 0;
         this.totalConflictItems = this.totalConflictItems + this.items.size();

         for (int var3 = this.items.size() - 1; var3 >= 0; var3--) {
            ConflictResolver.ConflictItem var4 = this.items.get(var3);
            if (var4.parent == var1) {
               var4.depth = var2;
            } else if (var4.parent != null) {
               var1 = var4.parent;
               ConflictResolver.NodeInfo var5 = this.infos.get(var1);
               var2 = var5.minDepth + 1;
               var4.depth = var2;
            }
         }

         this.potentialAncestorIds.add(this.currentId);
      }

      void winner() {
         this.resolvedIds.put(this.currentId, this.conflictCtx.winner != null ? this.conflictCtx.winner.node : null);
      }

      boolean loser(DependencyNode var1, Object var2) {
         DependencyNode var3 = this.resolvedIds.get(var2);
         return var3 != null && var3 != var1;
      }

      boolean push(DependencyNode var1, Object var2) throws RepositoryException {
         if (var2 == null) {
            if (var1.getDependency() != null) {
               if (var1.getData().get("conflict.winner") != null) {
                  return false;
               }

               throw new RepositoryException("missing conflict id for node " + var1);
            }
         } else if (!this.potentialAncestorIds.contains(var2)) {
            return false;
         }

         List var3 = var1.getChildren();
         if (this.stack.put(var3, Boolean.TRUE) != null) {
            return false;
         } else {
            int var4 = this.depth();
            String var5 = this.deriveScope(var1, var2);
            boolean var6 = this.deriveOptional(var1, var2);
            ConflictResolver.NodeInfo var7 = this.infos.get(var3);
            if (var7 == null) {
               var7 = new ConflictResolver.NodeInfo(var4, var5, var6);
               this.infos.put(var3, var7);
               this.parentInfos.add(var7);
               this.parentNodes.add(var1);
               this.parentScopes.add(var5);
               this.parentOptionals.add(var6);
            } else {
               int var8 = var7.update(var4, var5, var6);
               if (var8 == 0) {
                  this.stack.remove(var3);
                  return false;
               }

               this.parentInfos.add(null);
               this.parentNodes.add(var1);
               this.parentScopes.add(var5);
               this.parentOptionals.add(var6);
               if (var7.children != null) {
                  if ((var8 & 1) != 0) {
                     for (int var9 = var7.children.size() - 1; var9 >= 0; var9--) {
                        ConflictResolver.ConflictItem var10 = var7.children.get(var9);
                        String var11 = this.deriveScope(var10.node, null);
                        var10.addScope(var11);
                     }
                  }

                  if ((var8 & 2) != 0) {
                     for (int var13 = var7.children.size() - 1; var13 >= 0; var13--) {
                        ConflictResolver.ConflictItem var14 = var7.children.get(var13);
                        boolean var15 = this.deriveOptional(var14.node, null);
                        var14.addOptional(var15);
                     }
                  }
               }
            }

            return true;
         }
      }

      void pop() {
         int var1 = this.parentInfos.size() - 1;
         this.parentInfos.remove(var1);
         this.parentScopes.remove(var1);
         this.parentOptionals.remove(var1);
         DependencyNode var2 = this.parentNodes.remove(var1);
         this.stack.remove(var2.getChildren());
      }

      void add(DependencyNode var1) throws RepositoryException {
         DependencyNode var2 = this.parent();
         if (var2 == null) {
            ConflictResolver.ConflictItem var3 = this.newConflictItem(var2, var1);
            this.items.add(var3);
         } else {
            ConflictResolver.NodeInfo var5 = this.parentInfos.get(this.parentInfos.size() - 1);
            if (var5 != null) {
               ConflictResolver.ConflictItem var4 = this.newConflictItem(var2, var1);
               var5.add(var4);
               this.items.add(var4);
            }
         }
      }

      private ConflictResolver.ConflictItem newConflictItem(DependencyNode var1, DependencyNode var2) throws RepositoryException {
         return new ConflictResolver.ConflictItem(var1, var2, this.deriveScope(var2, null), this.deriveOptional(var2, null));
      }

      private int depth() {
         return this.parentNodes.size();
      }

      private DependencyNode parent() {
         int var1 = this.parentNodes.size();
         return var1 <= 0 ? null : this.parentNodes.get(var1 - 1);
      }

      private String deriveScope(DependencyNode var1, Object var2) throws RepositoryException {
         if ((var1.getManagedBits() & 2) == 0 && (var2 == null || !this.resolvedIds.containsKey(var2))) {
            int var3 = this.parentNodes.size();
            this.scopes(var3, var1.getDependency());
            if (var3 > 0) {
               this.scopeDeriver.deriveScope(this.scopeCtx);
            }

            return this.scopeCtx.derivedScope;
         } else {
            return this.scope(var1.getDependency());
         }
      }

      private void scopes(int var1, Dependency var2) {
         this.scopeCtx.parentScope = var1 > 0 ? this.parentScopes.get(var1 - 1) : null;
         this.scopeCtx.derivedScope = this.scope(var2);
         this.scopeCtx.childScope = this.scope(var2);
      }

      private String scope(Dependency var1) {
         return var1 != null ? var1.getScope() : null;
      }

      private boolean deriveOptional(DependencyNode var1, Object var2) {
         Dependency var3 = var1.getDependency();
         boolean var4 = var3 != null && var3.isOptional();
         if (!var4 && (var1.getManagedBits() & 4) == 0 && (var2 == null || !this.resolvedIds.containsKey(var2))) {
            int var5 = this.parentNodes.size();
            return var5 > 0 ? this.parentOptionals.get(var5 - 1) : false;
         } else {
            return var4;
         }
      }
   }

   public abstract static class VersionSelector {
      public ConflictResolver.VersionSelector getInstance(DependencyNode var1, DependencyGraphTransformationContext var2) throws RepositoryException {
         return this;
      }

      public abstract void selectVersion(ConflictResolver.ConflictContext var1) throws RepositoryException;
   }
}
