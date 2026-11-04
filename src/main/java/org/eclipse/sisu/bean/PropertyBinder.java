package org.eclipse.sisu.bean;

public interface PropertyBinder {
   PropertyBinding LAST_BINDING = new PropertyBinding() {
      @Override
      public <B> void injectProperty(B var1) {
         throw new UnsupportedOperationException("LAST_BINDING");
      }
   };

   <T> PropertyBinding bindProperty(BeanProperty<T> var1);
}
