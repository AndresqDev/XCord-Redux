package org.eclipse.aether.util.filter;

import java.util.Collection;
import org.eclipse.aether.version.VersionScheme;

public final class PatternInclusionsDependencyFilter extends AbstractPatternDependencyFilter {
   public PatternInclusionsDependencyFilter(String... var1) {
      super(var1);
   }

   public PatternInclusionsDependencyFilter(VersionScheme var1, String... var2) {
      super(var1, var2);
   }

   public PatternInclusionsDependencyFilter(Collection<String> var1) {
      super(var1);
   }

   public PatternInclusionsDependencyFilter(VersionScheme var1, Collection<String> var2) {
      super(var1, var2);
   }
}
