package org.eclipse.sisu.inject;

import com.google.inject.ImplementedBy;
import com.google.inject.Injector;

@ImplementedBy(DefaultBeanLocator.class)
public interface MutableBeanLocator extends BeanLocator {
   boolean add(BindingPublisher var1);

   boolean remove(BindingPublisher var1);

   Iterable<BindingPublisher> publishers();

   void clear();

   @Deprecated
   void add(Injector var1, int var2);

   @Deprecated
   void remove(Injector var1);
}
