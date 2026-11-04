package org.eclipse.sisu;

import java.lang.annotation.Annotation;

public interface Mediator<Q extends Annotation, T, W> {
   void add(BeanEntry<Q, T> var1, W var2) throws Exception;

   void remove(BeanEntry<Q, T> var1, W var2) throws Exception;
}
