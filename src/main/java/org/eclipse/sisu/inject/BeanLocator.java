package org.eclipse.sisu.inject;

import com.google.inject.ImplementedBy;
import com.google.inject.Key;
import java.lang.annotation.Annotation;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Mediator;

@ImplementedBy(MutableBeanLocator.class)
public interface BeanLocator {
   <Q extends Annotation, T> Iterable<? extends BeanEntry<Q, T>> locate(Key<T> var1);

   <Q extends Annotation, T, W> void watch(Key<T> var1, Mediator<Q, T, W> var2, W var3);
}
