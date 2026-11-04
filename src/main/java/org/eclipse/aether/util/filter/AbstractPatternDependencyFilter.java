package org.eclipse.aether.util.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionRange;
import org.eclipse.aether.version.VersionScheme;

class AbstractPatternDependencyFilter implements DependencyFilter {
   private final Set<String> patterns = new HashSet<>();
   private final VersionScheme versionScheme;

   AbstractPatternDependencyFilter(String... var1) {
      this(null, var1);
   }

   AbstractPatternDependencyFilter(VersionScheme var1, String... var2) {
      this(var1, var2 == null ? null : Arrays.asList(var2));
   }

   AbstractPatternDependencyFilter(Collection<String> var1) {
      this(null, var1);
   }

   AbstractPatternDependencyFilter(VersionScheme var1, Collection<String> var2) {
      if (var2 != null) {
         this.patterns.addAll(var2);
      }

      this.versionScheme = var1;
   }

   @Override
   public boolean accept(DependencyNode var1, List<DependencyNode> var2) {
      Dependency var3 = var1.getDependency();
      return var3 == null ? true : this.accept(var3.getArtifact());
   }

   protected boolean accept(Artifact var1) {
      for (String var3 : this.patterns) {
         boolean var4 = this.accept(var1, var3);
         if (var4) {
            return true;
         }
      }

      return false;
   }

   private boolean accept(Artifact var1, String var2) {
      String[] var3 = new String[]{var1.getGroupId(), var1.getArtifactId(), var1.getExtension(), var1.getBaseVersion()};
      String[] var4 = var2.split(":");
      boolean var5 = var4.length <= var3.length;

      for (int var6 = 0; var5 && var6 < var4.length; var6++) {
         var5 = this.matches(var3[var6], var4[var6]);
      }

      return var5;
   }

   private boolean matches(String var1, String var2) {
      boolean var3;
      if ("*".equals(var2) || var2.length() == 0) {
         var3 = true;
      } else if (var2.startsWith("*") && var2.endsWith("*")) {
         String var6 = var2.substring(1, var2.length() - 1);
         var3 = var1.contains(var6);
      } else if (var2.startsWith("*")) {
         String var4 = var2.substring(1);
         var3 = var1.endsWith(var4);
      } else if (var2.endsWith("*")) {
         String var5 = var2.substring(0, var2.length() - 1);
         var3 = var1.startsWith(var5);
      } else if (!var2.startsWith("[") && !var2.startsWith("(")) {
         var3 = var1.equals(var2);
      } else {
         var3 = this.isVersionIncludedInRange(var1, var2);
      }

      return var3;
   }

   private boolean isVersionIncludedInRange(String var1, String var2) {
      if (this.versionScheme == null) {
         return false;
      } else {
         try {
            Version var3 = this.versionScheme.parseVersion(var1);
            VersionRange var4 = this.versionScheme.parseVersionRange(var2);
            return var4.containsVersion(var3);
         } catch (InvalidVersionSpecificationException var5) {
            return false;
         }
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass().equals(var1.getClass())) {
         AbstractPatternDependencyFilter var2 = (AbstractPatternDependencyFilter)var1;
         return Objects.equals(this.patterns, var2.patterns) && Objects.equals(this.versionScheme, var2.versionScheme);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = 17;
      var1 = var1 * 31 + this.patterns.hashCode();
      return var1 * 31 + (this.versionScheme == null ? 0 : this.versionScheme.hashCode());
   }
}
