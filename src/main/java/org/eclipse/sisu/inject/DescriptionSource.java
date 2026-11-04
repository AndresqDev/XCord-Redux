package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import java.lang.annotation.Annotation;
import org.eclipse.sisu.Description;

final class DescriptionSource implements Description, AnnotatedSource {
   private final Object source;
   private final String value;

   DescriptionSource(Object var1, String var2) {
      this.source = var1;
      this.value = var2;
   }

   @Override
   public String value() {
      return this.value;
   }

   @Override
   public Class<? extends Annotation> annotationType() {
      return Description.class;
   }

   @Override
   public int hashCode() {
      return 127 * "value".hashCode() ^ this.value.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      return this == var1 || var1 instanceof Description && this.value.equals(((Description)var1).value());
   }

   @Override
   public String toString() {
      return this.source != null ? this.source.toString() : "@" + Description.class.getName() + "(value=" + this.value + ")";
   }

   @Override
   public <T extends Annotation> T getAnnotation(Binding<?> var1, Class<T> var2) {
      if (Description.class.equals(var2)) {
         return (T)this;
      } else {
         return this.source instanceof AnnotatedSource ? ((AnnotatedSource)this.source).getAnnotation(var1, var2) : null;
      }
   }
}
