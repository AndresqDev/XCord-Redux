package org.apache.maven.model.composition;

import java.util.List;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

public interface DependencyManagementImporter {
   void importManagement(Model var1, List<? extends DependencyManagement> var2, ModelBuildingRequest var3, ModelProblemCollector var4);
}
