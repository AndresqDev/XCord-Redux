package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import java.lang.annotation.Annotation;
import org.eclipse.sisu.Priority;

final class PrioritySource implements Priority, AnnotatedSource {
   private final Object source;
   private final int value;

   PrioritySource(Object var1, int var2) {
      this.source = var1;
      this.value = var2;
   }

   @Override
   public int value() {
      return this.value;
   }

   @Override
   public Class<? extends Annotation> annotationType() {
      return Priority.class;
   }

   @Override
   public int hashCode() {
      return 127 * "value".hashCode() ^ Integer.valueOf(this.value).hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof Priority && this.value == ((Priority)var1).value();
   }

   @Override
   public String toString() {
      return this.source != null ? this.source.toString() : "@" + Priority.class.getName() + "(value=" + this.value + ")";
   }

   @Override
   public <T extends Annotation> T getAnnotation(Binding<?> var1, Class<T> var2) {
      if (Priority.class.equals(var2)) {
         return (T)this;
      } else {
         return this.source instanceof AnnotatedSource ? ((AnnotatedSource)this.source).getAnnotation(var1, var2) : null;
      }
   }
}
