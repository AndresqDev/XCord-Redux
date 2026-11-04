package org.apache.maven.model.building;

import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.locator.ModelLocator;

public interface ModelProcessor extends ModelReader, ModelLocator {
   String SOURCE = "org.apache.maven.model.building.source";
}
