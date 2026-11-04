package org.eclipse.aether.impl;

import org.eclipse.aether.RepositorySystemSession;

public interface UpdatePolicyAnalyzer {
   String getEffectiveUpdatePolicy(RepositorySystemSession var1, String var2, String var3);

   boolean isUpdatedRequired(RepositorySystemSession var1, long var2, String var4);
}
