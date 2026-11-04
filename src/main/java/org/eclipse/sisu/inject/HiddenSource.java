package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import java.lang.annotation.Annotation;
import org.eclipse.sisu.Hidden;

final class HiddenSource implements Hidden, AnnotatedSource {
   private final Object source;

   HiddenSource(Object var1) {
      this.source = var1;
   }

   @Override
   public Class<? extends Annotation> annotationType() {
      return Hidden.class;
   }

   @Override
   public int hashCode() {
      return 0;
   }

   @Override
   public boolean equals(Object var1) {
      return var1 instanceof Hidden;
   }

   @Override
   public String toString() {
      return this.source != null ? this.source.toString() : "@" + Hidden.class.getName();
   }

   @Override
   public <T extends Annotation> T getAnnotation(Binding<?> var1, Class<T> var2) {
      if (Hidden.class.equals(var2)) {
         return (T)this;
      } else {
         return this.source instanceof AnnotatedSource ? ((AnnotatedSource)this.source).getAnnotation(var1, var2) : null;
      }
   }
}
