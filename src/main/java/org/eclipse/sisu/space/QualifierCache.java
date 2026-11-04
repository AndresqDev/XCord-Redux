package org.eclipse.sisu.space;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class QualifierCache implements ClassVisitor {
   private static final String QUALIFIER_DESC = "Ljavax/inject/Qualifier;";
   private static final String NAMED_DESC = "Ljavax/inject/Named;";
   private static final Map<String, Boolean> cachedResults = new ConcurrentHashMap<>(32, 0.75F, 1);
   private boolean isQualified;

   @Override
   public void enterClass(int var1, String var2, String var3, String[] var4) {
   }

   @Override
   public AnnotationVisitor visitAnnotation(String var1) {
      this.isQualified = this.isQualified | "Ljavax/inject/Qualifier;".equals(var1);
      return null;
   }

   @Override
   public void leaveClass() {
   }

   boolean qualify(ClassSpace var1, String var2) {
      if ("Ljavax/inject/Named;".equals(var2)) {
         return true;
      } else {
         Boolean var3 = cachedResults.get(var2);
         if (var3 == null) {
            this.isQualified = false;
            String var4 = var2.substring(1, var2.length() - 1);
            SpaceScanner.accept(this, var1.getResource(var4 + ".class"));
            cachedResults.put(var2, this.isQualified);
            return this.isQualified;
         } else {
            return var3;
         }
      }
   }
}
