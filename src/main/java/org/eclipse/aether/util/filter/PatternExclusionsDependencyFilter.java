package org.eclipse.aether.util.filter;

import java.util.Collection;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.version.VersionScheme;

public final class PatternExclusionsDependencyFilter extends AbstractPatternDependencyFilter {
   public PatternExclusionsDependencyFilter(String... var1) {
      super(var1);
   }

   public PatternExclusionsDependencyFilter(VersionScheme var1, String... var2) {
      super(var1, var2);
   }

   public PatternExclusionsDependencyFilter(Collection<String> var1) {
      super(var1);
   }

   public PatternExclusionsDependencyFilter(VersionScheme var1, Collection<String> var2) {
      super(var1, var2);
   }

   @Override
   protected boolean accept(Artifact var1) {
      return !super.accept(var1);
   }
}
