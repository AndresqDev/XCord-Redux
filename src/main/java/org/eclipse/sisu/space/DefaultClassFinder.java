package org.eclipse.sisu.space;

import java.net.URL;
import java.util.Enumeration;

public final class DefaultClassFinder implements ClassFinder {
   private final String path;
   private final boolean recurse;

   public DefaultClassFinder(String var1) {
      String var2 = var1.replace('.', '/');
      if (var2.endsWith("*")) {
         this.path = var2.substring(0, var2.length() - 1);
         this.recurse = true;
      } else {
         this.path = var2;
         this.recurse = false;
      }
   }

   public DefaultClassFinder() {
      this.path = null;
      this.recurse = true;
   }

   @Override
   public Enumeration<URL> findClasses(ClassSpace var1) {
      return var1.findEntries(this.path, "*.class", this.recurse);
   }
}
