package joptsimple.internal;

import java.lang.reflect.Constructor;
import joptsimple.ValueConverter;

class ConstructorInvokingValueConverter<V> implements ValueConverter<V> {
   private final Constructor<V> ctor;

   ConstructorInvokingValueConverter(Constructor<V> var1) {
      this.ctor = var1;
   }

   @Override
   public V convert(String var1) {
      return Reflection.instantiate(this.ctor, var1);
   }

   @Override
   public Class<V> valueType() {
      return this.ctor.getDeclaringClass();
   }

   @Override
   public String valuePattern() {
      return null;
   }
}
