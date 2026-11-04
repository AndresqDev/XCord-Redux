package org.yaml.snakeyaml.extensions.compactnotation;

public class PackageCompactConstructor extends CompactConstructor {
   private String packageName;

   public PackageCompactConstructor(String var1) {
      this.packageName = var1;
   }

   @Override
   protected Class<?> getClassForName(String var1) throws ClassNotFoundException {
      if (var1.indexOf(46) < 0) {
         try {
            return Class.forName(this.packageName + "." + var1);
         } catch (ClassNotFoundException var3) {
         }
      }

      return super.getClassForName(var1);
   }
}
