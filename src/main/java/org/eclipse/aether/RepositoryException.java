package org.eclipse.aether;

public class RepositoryException extends Exception {
   public RepositoryException(String var1) {
      super(var1);
   }

   public RepositoryException(String var1, Throwable var2) {
      super(var1, var2);
   }

   protected static String getMessage(String var0, Throwable var1) {
      String var2 = "";
      if (var1 != null) {
         var2 = var1.getMessage();
         if (var2 == null || var2.length() <= 0) {
            var2 = var1.getClass().getSimpleName();
         }

         var2 = var0 + var2;
      }

      return var2;
   }
}
