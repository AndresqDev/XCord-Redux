package org.apache.maven.model.path;

import java.io.File;
import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultPathTranslator implements PathTranslator {
   @Override
   public String alignToBaseDirectory(String var1, File var2) {
      String var3 = var1;
      if (var1 != null && var2 != null) {
         var1 = var1.replace('\\', File.separatorChar).replace('/', File.separatorChar);
         File var4 = new File(var1);
         if (var4.isAbsolute()) {
            var3 = var4.getPath();
         } else if (var4.getPath().startsWith(File.separator)) {
            var3 = var4.getAbsolutePath();
         } else {
            var3 = new File(new File(var2, var1).toURI().normalize()).getAbsolutePath();
         }
      }

      return var3;
   }
}
