package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import java.lang.annotation.Annotation;
import org.eclipse.sisu.Description;
import org.eclipse.sisu.Hidden;
import org.eclipse.sisu.Priority;

public final class Sources {
   private Sources() {
   }

   public static Hidden hide() {
      return hide(null);
   }

   public static Hidden hide(Object var0) {
      return new HiddenSource(var0);
   }

   public static Description describe(String var0) {
      return describe(null, var0);
   }

   public static Description describe(Object var0, String var1) {
      return new DescriptionSource(var0, var1);
   }

   public static Priority prioritize(int var0) {
      return prioritize(null, var0);
   }

   public static Priority prioritize(Object var0, int var1) {
      return new PrioritySource(var0, var1);
   }

   public static <T extends Annotation> T getAnnotation(Binding<?> var0, Class<T> var1) {
      Annotation var2 = null;
      Object var3 = Guice4.getDeclaringSource(var0);
      if (var3 instanceof AnnotatedSource) {
         var2 = ((AnnotatedSource)var3).getAnnotation(var0, var1);
      }

      if (var2 == null) {
         var2 = Implementations.getAnnotation(var0, var1);
      }

      return (T)var2;
   }
}
