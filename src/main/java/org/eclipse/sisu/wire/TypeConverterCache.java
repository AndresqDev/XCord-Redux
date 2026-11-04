package org.eclipse.sisu.wire;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeConverterBinding;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import xcord.Inject;

@Singleton
final class TypeConverterCache {
   private final Map<TypeLiteral<?>, TypeConverter> converterMap = new ConcurrentHashMap<>(16, 0.75F, 1);
   private final Injector injector;

   @Inject
   TypeConverterCache(Injector var1) {
      this.injector = var1;
   }

   public TypeConverter getTypeConverter(TypeLiteral<?> var1) {
      TypeConverter var2 = this.converterMap.get(var1);
      if (var2 == null) {
         for (TypeConverterBinding var3 : this.injector.getTypeConverterBindings()) {
            if (var3.getTypeMatcher().matches(var1)) {
               var2 = var3.getTypeConverter();
               this.converterMap.put(var1, var2);
               break;
            }
         }
      }

      return var2;
   }
}
