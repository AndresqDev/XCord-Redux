package org.apache.maven.model.plugin;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblemCollector;

public interface PluginConfigurationExpander {
   void expandPluginConfiguration(Model var1, ModelBuildingRequest var2, ModelProblemCollector var3);
}
