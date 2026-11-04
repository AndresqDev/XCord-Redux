package org.eclipse.sisu.bean;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;

public interface BeanBinder {
   <B> PropertyBinder bindBean(TypeLiteral<B> var1, TypeEncounter<B> var2);
}
