package org.yaml.snakeyaml.introspector;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.util.ArrayUtils;

public class FieldProperty extends GenericProperty {
   private final Field field;

   public FieldProperty(Field var1) {
      super(var1.getName(), var1.getType(), var1.getGenericType());
      this.field = var1;
      var1.setAccessible(true);
   }

   @Override
   public void set(Object var1, Object var2) throws Exception {
      this.field.set(var1, var2);
   }

   @Override
   public Object get(Object var1) {
      try {
         return this.field.get(var1);
      } catch (Exception var3) {
         throw new YAMLException("Unable to access field " + this.field.getName() + " on object " + var1 + " : " + var3);
      }
   }

   @Override
   public List<Annotation> getAnnotations() {
      return ArrayUtils.toUnmodifiableList(this.field.getAnnotations());
   }

   @Override
   public <A extends Annotation> A getAnnotation(Class<A> var1) {
      return this.field.getAnnotation(var1);
   }
}
