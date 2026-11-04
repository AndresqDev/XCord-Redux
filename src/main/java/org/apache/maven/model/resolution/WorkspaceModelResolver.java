package org.apache.maven.model.resolution;

import org.apache.maven.model.Model;

public interface WorkspaceModelResolver {
   Model resolveRawModel(String var1, String var2, String var3) throws UnresolvableModelException;

   Model resolveEffectiveModel(String var1, String var2, String var3) throws UnresolvableModelException;
}
