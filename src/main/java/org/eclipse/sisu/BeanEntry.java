package org.eclipse.sisu;

import java.lang.annotation.Annotation;
import java.util.Map.Entry;
import xcord.Provider;

public interface BeanEntry<Q extends Annotation, T> extends Entry<Q, T> {
   Q getKey();

   @Override
   T getValue();

   Provider<T> getProvider();

   String getDescription();

   Class<T> getImplementationClass();

   Object getSource();

   int getRank();
}
