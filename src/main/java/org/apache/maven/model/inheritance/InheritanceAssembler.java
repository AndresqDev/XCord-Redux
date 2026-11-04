package org.apache.maven.model.inheritance;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

public interface InheritanceAssembler {
   void assembleModelInheritance(Model var1, Model var2, ModelBuildingRequest var3, ModelProblemCollector var4);
}
