package org.yaml.snakeyaml.constructor;

public class CustomClassLoaderConstructor extends Constructor {
   private ClassLoader loader = CustomClassLoaderConstructor.class.getClassLoader();

   public CustomClassLoaderConstructor(ClassLoader var1) {
      this(Object.class, var1);
   }

   public CustomClassLoaderConstructor(Class<? extends Object> var1, ClassLoader var2) {
      super(var1);
      if (var2 == null) {
         throw new NullPointerException("Loader must be provided.");
      } else {
         this.loader = var2;
      }
   }

   @Override
   protected Class<?> getClassForName(String var1) throws ClassNotFoundException {
      return Class.forName(var1, true, this.loader);
   }
}
