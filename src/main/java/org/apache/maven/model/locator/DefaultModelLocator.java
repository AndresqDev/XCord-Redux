package org.apache.maven.model.locator;

import java.io.File;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultModelLocator implements ModelLocator {
   @Override
   public File locatePom(File var1) {
      return new File(var1, "pom.xml");
   }
}
