package org.apache.maven.model.resolution;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.ModelSource;

public interface ModelResolver {
   ModelSource resolveModel(String var1, String var2, String var3) throws UnresolvableModelException;

   ModelSource resolveModel(Parent var1) throws UnresolvableModelException;

   ModelSource resolveModel(Dependency var1) throws UnresolvableModelException;

   void addRepository(Repository var1) throws InvalidRepositoryException;

   void addRepository(Repository var1, boolean var2) throws InvalidRepositoryException;

   ModelResolver newCopy();
}
