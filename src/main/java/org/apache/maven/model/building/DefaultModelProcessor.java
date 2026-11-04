package org.apache.maven.model.building;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.locator.ModelLocator;
import org.eclipse.sisu.Typed;
import xcord.Inject;
import xcord.Named;
import xcord.Singleton;

@Named("core-default")
@Singleton
@Typed({ModelProcessor.class})
public class DefaultModelProcessor implements ModelProcessor {
   @Inject
   private ModelLocator locator;
   @Inject
   private ModelReader reader;

   public DefaultModelProcessor setModelLocator(ModelLocator var1) {
      this.locator = var1;
      return this;
   }

   public DefaultModelProcessor setModelReader(ModelReader var1) {
      this.reader = var1;
      return this;
   }

   @Override
   public File locatePom(File var1) {
      return this.locator.locatePom(var1);
   }

   @Override
   public Model read(File var1, Map<String, ?> var2) throws IOException {
      return this.reader.read(var1, var2);
   }

   @Override
   public Model read(Reader var1, Map<String, ?> var2) throws IOException {
      return this.reader.read(var1, var2);
   }

   @Override
   public Model read(InputStream var1, Map<String, ?> var2) throws IOException {
      return this.reader.read(var1, var2);
   }
}
