package org.eclipse.sisu.bean;

public interface BeanManager {
   boolean manage(Class<?> var1);

   PropertyBinding manage(BeanProperty<?> var1);

   boolean manage(Object var1);

   boolean unmanage(Object var1);

   boolean unmanage();
}
