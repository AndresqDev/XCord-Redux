package com.google.protobuf;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.List;

public enum FieldType {
   DOUBLE(0, FieldType.Collection.SCALAR, JavaType.DOUBLE),
   FLOAT(1, FieldType.Collection.SCALAR, JavaType.FLOAT),
   INT64(2, FieldType.Collection.SCALAR, JavaType.LONG),
   UINT64(3, FieldType.Collection.SCALAR, JavaType.LONG),
   INT32(4, FieldType.Collection.SCALAR, JavaType.INT),
   FIXED64(5, FieldType.Collection.SCALAR, JavaType.LONG),
   FIXED32(6, FieldType.Collection.SCALAR, JavaType.INT),
   BOOL(7, FieldType.Collection.SCALAR, JavaType.BOOLEAN),
   STRING(8, FieldType.Collection.SCALAR, JavaType.STRING),
   MESSAGE(9, FieldType.Collection.SCALAR, JavaType.MESSAGE),
   BYTES(10, FieldType.Collection.SCALAR, JavaType.BYTE_STRING),
   UINT32(11, FieldType.Collection.SCALAR, JavaType.INT),
   ENUM(12, FieldType.Collection.SCALAR, JavaType.ENUM),
   SFIXED32(13, FieldType.Collection.SCALAR, JavaType.INT),
   SFIXED64(14, FieldType.Collection.SCALAR, JavaType.LONG),
   SINT32(15, FieldType.Collection.SCALAR, JavaType.INT),
   SINT64(16, FieldType.Collection.SCALAR, JavaType.LONG),
   GROUP(17, FieldType.Collection.SCALAR, JavaType.MESSAGE),
   DOUBLE_LIST(18, FieldType.Collection.VECTOR, JavaType.DOUBLE),
   FLOAT_LIST(19, FieldType.Collection.VECTOR, JavaType.FLOAT),
   INT64_LIST(20, FieldType.Collection.VECTOR, JavaType.LONG),
   UINT64_LIST(21, FieldType.Collection.VECTOR, JavaType.LONG),
   INT32_LIST(22, FieldType.Collection.VECTOR, JavaType.INT),
   FIXED64_LIST(23, FieldType.Collection.VECTOR, JavaType.LONG),
   FIXED32_LIST(24, FieldType.Collection.VECTOR, JavaType.INT),
   BOOL_LIST(25, FieldType.Collection.VECTOR, JavaType.BOOLEAN),
   STRING_LIST(26, FieldType.Collection.VECTOR, JavaType.STRING),
   MESSAGE_LIST(27, FieldType.Collection.VECTOR, JavaType.MESSAGE),
   BYTES_LIST(28, FieldType.Collection.VECTOR, JavaType.BYTE_STRING),
   UINT32_LIST(29, FieldType.Collection.VECTOR, JavaType.INT),
   ENUM_LIST(30, FieldType.Collection.VECTOR, JavaType.ENUM),
   SFIXED32_LIST(31, FieldType.Collection.VECTOR, JavaType.INT),
   SFIXED64_LIST(32, FieldType.Collection.VECTOR, JavaType.LONG),
   SINT32_LIST(33, FieldType.Collection.VECTOR, JavaType.INT),
   SINT64_LIST(34, FieldType.Collection.VECTOR, JavaType.LONG),
   DOUBLE_LIST_PACKED(35, FieldType.Collection.PACKED_VECTOR, JavaType.DOUBLE),
   FLOAT_LIST_PACKED(36, FieldType.Collection.PACKED_VECTOR, JavaType.FLOAT),
   INT64_LIST_PACKED(37, FieldType.Collection.PACKED_VECTOR, JavaType.LONG),
   UINT64_LIST_PACKED(38, FieldType.Collection.PACKED_VECTOR, JavaType.LONG),
   INT32_LIST_PACKED(39, FieldType.Collection.PACKED_VECTOR, JavaType.INT),
   FIXED64_LIST_PACKED(40, FieldType.Collection.PACKED_VECTOR, JavaType.LONG),
   FIXED32_LIST_PACKED(41, FieldType.Collection.PACKED_VECTOR, JavaType.INT),
   BOOL_LIST_PACKED(42, FieldType.Collection.PACKED_VECTOR, JavaType.BOOLEAN),
   UINT32_LIST_PACKED(43, FieldType.Collection.PACKED_VECTOR, JavaType.INT),
   ENUM_LIST_PACKED(44, FieldType.Collection.PACKED_VECTOR, JavaType.ENUM),
   SFIXED32_LIST_PACKED(45, FieldType.Collection.PACKED_VECTOR, JavaType.INT),
   SFIXED64_LIST_PACKED(46, FieldType.Collection.PACKED_VECTOR, JavaType.LONG),
   SINT32_LIST_PACKED(47, FieldType.Collection.PACKED_VECTOR, JavaType.INT),
   SINT64_LIST_PACKED(48, FieldType.Collection.PACKED_VECTOR, JavaType.LONG),
   GROUP_LIST(49, FieldType.Collection.VECTOR, JavaType.MESSAGE),
   MAP(50, FieldType.Collection.MAP, JavaType.VOID);

   private final JavaType javaType;
   private final int id;
   private final FieldType.Collection collection;
   private final Class<?> elementType;
   private final boolean primitiveScalar;
   private static final FieldType[] VALUES;
   private static final java.lang.reflect.Type[] EMPTY_TYPES = new java.lang.reflect.Type[0];

   private FieldType(int var3, FieldType.Collection var4, JavaType var5) {
      this.id = var3;
      this.collection = var4;
      this.javaType = var5;
      switch (var4) {
         case MAP:
            this.elementType = var5.getBoxedType();
            break;
         case VECTOR:
            this.elementType = var5.getBoxedType();
            break;
         case SCALAR:
         default:
            this.elementType = null;
      }

      boolean var6 = false;
      if (var4 == FieldType.Collection.SCALAR) {
         switch (var5) {
            case BYTE_STRING:
            case MESSAGE:
            case STRING:
               break;
            default:
               var6 = true;
         }
      }

      this.primitiveScalar = var6;
   }

   public int id() {
      return this.id;
   }

   public JavaType getJavaType() {
      return this.javaType;
   }

   public boolean isPacked() {
      return FieldType.Collection.PACKED_VECTOR.equals(this.collection);
   }

   public boolean isPrimitiveScalar() {
      return this.primitiveScalar;
   }

   public boolean isScalar() {
      return this.collection == FieldType.Collection.SCALAR;
   }

   public boolean isList() {
      return this.collection.isList();
   }

   public boolean isMap() {
      return this.collection == FieldType.Collection.MAP;
   }

   public boolean isValidForField(java.lang.reflect.Field var1) {
      return FieldType.Collection.VECTOR.equals(this.collection) ? this.isValidForList(var1) : this.javaType.getType().isAssignableFrom(var1.getType());
   }

   private boolean isValidForList(java.lang.reflect.Field var1) {
      Class var2 = var1.getType();
      if (!this.javaType.getType().isAssignableFrom(var2)) {
         return false;
      } else {
         java.lang.reflect.Type[] var3 = EMPTY_TYPES;
         java.lang.reflect.Type var4 = var1.getGenericType();
         if (var4 instanceof ParameterizedType) {
            var3 = ((ParameterizedType)var1.getGenericType()).getActualTypeArguments();
         }

         java.lang.reflect.Type var5 = getListParameter(var2, var3);
         return !(var5 instanceof Class) ? true : this.elementType.isAssignableFrom((Class<?>)var5);
      }
   }

   public static FieldType forId(int var0) {
      return var0 >= 0 && var0 < VALUES.length ? VALUES[var0] : null;
   }

   private static java.lang.reflect.Type getGenericSuperList(Class<?> var0) {
      java.lang.reflect.Type[] var1 = var0.getGenericInterfaces();

      for (java.lang.reflect.Type var5 : var1) {
         if (var5 instanceof ParameterizedType) {
            ParameterizedType var6 = (ParameterizedType)var5;
            Class var7 = (Class)var6.getRawType();
            if (List.class.isAssignableFrom(var7)) {
               return var5;
            }
         }
      }

      java.lang.reflect.Type var8 = var0.getGenericSuperclass();
      if (var8 instanceof ParameterizedType) {
         ParameterizedType var9 = (ParameterizedType)var8;
         Class var10 = (Class)var9.getRawType();
         if (List.class.isAssignableFrom(var10)) {
            return var8;
         }
      }

      return null;
   }

   private static java.lang.reflect.Type getListParameter(Class<?> var0, java.lang.reflect.Type[] var1) {
      label60:
      while (var0 != List.class) {
         java.lang.reflect.Type var2 = getGenericSuperList(var0);
         if (!(var2 instanceof ParameterizedType)) {
            var1 = EMPTY_TYPES;

            for (Class var15 : var0.getInterfaces()) {
               if (List.class.isAssignableFrom(var15)) {
                  var0 = var15;
                  continue label60;
               }
            }

            var0 = var0.getSuperclass();
         } else {
            ParameterizedType var3 = (ParameterizedType)var2;
            java.lang.reflect.Type[] var4 = var3.getActualTypeArguments();

            for (int var5 = 0; var5 < var4.length; var5++) {
               java.lang.reflect.Type var6 = var4[var5];
               if (var6 instanceof TypeVariable) {
                  TypeVariable[] var7 = var0.getTypeParameters();
                  if (var1.length != var7.length) {
                     throw new RuntimeException("Type array mismatch");
                  }

                  boolean var8 = false;

                  for (int var9 = 0; var9 < var7.length; var9++) {
                     if (var6 == var7[var9]) {
                        java.lang.reflect.Type var10 = var1[var9];
                        var4[var5] = var10;
                        var8 = true;
                        break;
                     }
                  }

                  if (!var8) {
                     throw new RuntimeException("Unable to find replacement for " + var6);
                  }
               }
            }

            Class var13 = (Class)var3.getRawType();
            var1 = var4;
            var0 = var13;
         }
      }

      if (var1.length != 1) {
         throw new RuntimeException("Unable to identify parameter type for List<T>");
      } else {
         return var1[0];
      }
   }

   static {
      FieldType[] var0 = values();
      VALUES = new FieldType[var0.length];

      for (FieldType var4 : var0) {
         VALUES[var4.id] = var4;
      }
   }

   static enum Collection {
      SCALAR(false),
      VECTOR(true),
      PACKED_VECTOR(true),
      MAP(false);

      private final boolean isList;

      private Collection(boolean var3) {
         this.isList = var3;
      }

      public boolean isList() {
         return this.isList;
      }
   }
}
