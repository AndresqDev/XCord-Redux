package org.apache.logging.log4j.core.config.plugins.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.logging.log4j.core.util.ReflectionUtil;

public final class ConstraintValidators {
   private ConstraintValidators() {
   }

   public static Collection<ConstraintValidator<?>> findValidators(Annotation... var0) {
      ArrayList var1 = new ArrayList();

      for (Annotation var5 : var0) {
         Class var6 = var5.annotationType();
         if (var6.isAnnotationPresent(Constraint.class)) {
            ConstraintValidator var7 = getValidator(var5, var6);
            if (var7 != null) {
               var1.add(var7);
            }
         }
      }

      return var1;
   }

   private static <A extends Annotation> ConstraintValidator<A> getValidator(A var0, Class<? extends A> var1) {
      Constraint var2 = var1.getAnnotation(Constraint.class);
      Class var3 = var2.value();
      if (var1.equals(getConstraintValidatorAnnotationType(var3))) {
         ConstraintValidator var4 = ReflectionUtil.instantiate(var3);
         var4.initialize(var0);
         return var4;
      } else {
         return null;
      }
   }

   private static Type getConstraintValidatorAnnotationType(Class<? extends ConstraintValidator<?>> var0) {
      for (Type var4 : var0.getGenericInterfaces()) {
         if (var4 instanceof ParameterizedType) {
            ParameterizedType var5 = (ParameterizedType)var4;
            if (ConstraintValidator.class.equals(var5.getRawType())) {
               return var5.getActualTypeArguments()[0];
            }
         }
      }

      return void.class;
   }
}
