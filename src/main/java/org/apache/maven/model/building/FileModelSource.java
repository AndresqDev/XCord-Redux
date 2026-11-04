package org.apache.maven.model.building;

import java.io.File;
import java.net.URI;
import org.apache.maven.building.FileSource;

public class FileModelSource extends FileSource implements ModelSource2 {
   public FileModelSource(File var1) {
      super(var1);
   }

   @Deprecated
   public File getPomFile() {
      return this.getFile();
   }

   @Override
   public ModelSource2 getRelatedSource(String var1) {
      var1 = var1.replace('\\', File.separatorChar).replace('/', File.separatorChar);
      File var2 = new File(this.getFile().getParentFile(), var1);
      if (var2.isDirectory()) {
         var2 = new File(var2, "pom.xml");
      }

      return var2.isFile() && var2.canRead() ? new FileModelSource(new File(var2.toURI().normalize())) : null;
   }

   @Override
   public URI getLocationURI() {
      return this.getFile().toURI();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof FileModelSource)) {
         return false;
      } else {
         FileModelSource var2 = (FileModelSource)var1;
         return this.getFile().equals(var2.getFile());
      }
   }

   @Override
   public int hashCode() {
      return this.getFile().hashCode();
   }
}
