package com.google.common.reflect;

import com.google.common.collect.Sets;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Set;

@ElementTypesAreNonnullByDefault
abstract class TypeVisitor {
   private final Set<Type> visited = Sets.newHashSet();

   public final void visit(Type... var1) {
      for (Type var5 : var1) {
         if (var5 != null && this.visited.add(var5)) {
            boolean var6 = false;

            try {
               if (var5 instanceof TypeVariable) {
                  this.visitTypeVariable((TypeVariable<?>)var5);
               } else if (var5 instanceof WildcardType) {
                  this.visitWildcardType((WildcardType)var5);
               } else if (var5 instanceof ParameterizedType) {
                  this.visitParameterizedType((ParameterizedType)var5);
               } else if (var5 instanceof Class) {
                  this.visitClass((Class<?>)var5);
               } else {
                  if (!(var5 instanceof GenericArrayType)) {
                     String var7 = String.valueOf(var5);
                     throw new AssertionError(new StringBuilder(14 + String.valueOf(var7).length()).append("Unknown type: ").append(var7).toString());
                  }

                  this.visitGenericArrayType((GenericArrayType)var5);
               }

               var6 = true;
            } finally {
               if (!var6) {
                  this.visited.remove(var5);
               }
            }
         }
      }
   }

   void visitClass(Class<?> var1) {
   }

   void visitGenericArrayType(GenericArrayType var1) {
   }

   void visitParameterizedType(ParameterizedType var1) {
   }

   void visitTypeVariable(TypeVariable<?> var1) {
   }

   void visitWildcardType(WildcardType var1) {
   }
}
