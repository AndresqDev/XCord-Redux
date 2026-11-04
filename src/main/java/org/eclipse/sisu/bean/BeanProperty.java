package org.eclipse.sisu.bean;

import com.google.inject.TypeLiteral;
import java.lang.annotation.Annotation;

public interface BeanProperty<T> {
   <A extends Annotation> A getAnnotation(Class<A> var1);

   TypeLiteral<T> getType();

   String getName();

   <B> void set(B var1, T var2);
}
