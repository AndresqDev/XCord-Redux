package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeConverter;
import org.eclipse.sisu.inject.TypeArguments;

public abstract class AbstractTypeConverter<T> implements Module, TypeConverter {
   public final void configure(Binder var1) {
      TypeLiteral var2 = TypeLiteral.get(this.getClass()).getSupertype(AbstractTypeConverter.class);
      var1.convertToTypes(Matchers.only(TypeArguments.get(var2, 0)), this);
   }
}
