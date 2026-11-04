package org.yaml.snakeyaml.introspector;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

public class MissingProperty extends Property {
   public MissingProperty(String var1) {
      super(var1, Object.class);
   }

   @Override
   public Class<?>[] getActualTypeArguments() {
      return new Class[0];
   }

   @Override
   public void set(Object var1, Object var2) throws Exception {
   }

   @Override
   public Object get(Object var1) {
      return var1;
   }

   @Override
   public List<Annotation> getAnnotations() {
      return Collections.emptyList();
   }

   @Override
   public <A extends Annotation> A getAnnotation(Class<A> var1) {
      return null;
   }
}
