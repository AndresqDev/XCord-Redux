package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

@ElementTypesAreNonnullByDefault
@Beta
public abstract class TypeParameter<T> extends TypeCapture<T> {
   final TypeVariable<?> typeVariable;

   protected TypeParameter() {
      Type var1 = this.capture();
      Preconditions.checkArgument(var1 instanceof TypeVariable, "%s should be a type variable.", var1);
      this.typeVariable = (TypeVariable<?>)var1;
   }

   @Override
   public final int hashCode() {
      return this.typeVariable.hashCode();
   }

   @Override
   public final boolean equals(Object var1) {
      if (var1 instanceof TypeParameter) {
         TypeParameter var2 = (TypeParameter)var1;
         return this.typeVariable.equals(var2.typeVariable);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return this.typeVariable.toString();
   }
}
