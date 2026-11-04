package com.sun.jna;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class Function extends Pointer {
   public static final int MAX_NARGS = 256;
   public static final int C_CONVENTION = 0;
   public static final int ALT_CONVENTION = 63;
   private static final int MASK_CC = 63;
   public static final int THROW_LAST_ERROR = 64;
   public static final int USE_VARARGS = 384;
   static final Integer INTEGER_TRUE = -1;
   static final Integer INTEGER_FALSE = 0;
   private NativeLibrary library;
   private final String functionName;
   final String encoding;
   final int callFlags;
   final Map<String, ?> options;
   static final String OPTION_INVOKING_METHOD = "invoking-method";
   private static final VarArgsChecker IS_VARARGS = VarArgsChecker.create();

   public static Function getFunction(String var0, String var1) {
      return NativeLibrary.getInstance(var0).getFunction(var1);
   }

   public static Function getFunction(String var0, String var1, int var2) {
      return NativeLibrary.getInstance(var0).getFunction(var1, var2, null);
   }

   public static Function getFunction(String var0, String var1, int var2, String var3) {
      return NativeLibrary.getInstance(var0).getFunction(var1, var2, var3);
   }

   public static Function getFunction(Pointer var0) {
      return getFunction(var0, 0, null);
   }

   public static Function getFunction(Pointer var0, int var1) {
      return getFunction(var0, var1, null);
   }

   public static Function getFunction(Pointer var0, int var1, String var2) {
      return new Function(var0, var1, var2);
   }

   Function(NativeLibrary var1, String var2, int var3, String var4) {
      this.checkCallingConvention(var3 & 63);
      if (var2 == null) {
         throw new NullPointerException("Function name must not be null");
      } else {
         this.library = var1;
         this.functionName = var2;
         this.callFlags = var3;
         this.options = var1.options;
         this.encoding = var4 != null ? var4 : Native.getDefaultStringEncoding();

         try {
            this.peer = var1.getSymbolAddress(var2);
         } catch (UnsatisfiedLinkError var6) {
            throw new UnsatisfiedLinkError("Error looking up function '" + var2 + "': " + var6.getMessage());
         }
      }
   }

   Function(Pointer var1, int var2, String var3) {
      this.checkCallingConvention(var2 & 63);
      if (var1 != null && var1.peer != 0L) {
         this.functionName = var1.toString();
         this.callFlags = var2;
         this.peer = var1.peer;
         this.options = Collections.EMPTY_MAP;
         this.encoding = var3 != null ? var3 : Native.getDefaultStringEncoding();
      } else {
         throw new NullPointerException("Function address may not be null");
      }
   }

   private void checkCallingConvention(int var1) throws IllegalArgumentException {
      if ((var1 & 63) != var1) {
         throw new IllegalArgumentException("Unrecognized calling convention: " + var1);
      }
   }

   public String getName() {
      return this.functionName;
   }

   public int getCallingConvention() {
      return this.callFlags & 63;
   }

   public Object invoke(Class<?> var1, Object[] var2) {
      return this.invoke(var1, var2, this.options);
   }

   public Object invoke(Class<?> var1, Object[] var2, Map<String, ?> var3) {
      Method var4 = (Method)var3.get("invoking-method");
      Class[] var5 = var4 != null ? var4.getParameterTypes() : null;
      return this.invoke(var4, var5, var1, var2, var3);
   }

   Object invoke(Method var1, Class<?>[] var2, Class<?> var3, Object[] var4, Map<String, ?> var5) {
      Object[] var6 = new Object[0];
      if (var4 != null) {
         if (var4.length > 256) {
            throw new UnsupportedOperationException("Maximum argument count is 256");
         }

         var6 = new Object[var4.length];
         System.arraycopy(var4, 0, var6, 0, var6.length);
      }

      TypeMapper var7 = (TypeMapper)var5.get("type-mapper");
      boolean var8 = Boolean.TRUE.equals(var5.get("allow-objects"));
      boolean var9 = var6.length > 0 && var1 != null ? isVarArgs(var1) : false;
      int var10 = var6.length > 0 && var1 != null ? fixedArgs(var1) : 0;

      for (int var11 = 0; var11 < var6.length; var11++) {
         Class var12 = var1 != null ? (var9 && var11 >= var2.length - 1 ? var2[var2.length - 1].getComponentType() : var2[var11]) : null;
         var6[var11] = this.convertArgument(var6, var11, var1, var7, var8, var12);
      }

      Class var21 = var3;
      Object var22 = null;
      if (NativeMapped.class.isAssignableFrom(var3)) {
         NativeMappedConverter var13 = NativeMappedConverter.getInstance(var3);
         var22 = var13;
         var21 = var13.nativeType();
      } else if (var7 != null) {
         var22 = var7.getFromNativeConverter(var3);
         if (var22 != null) {
            var21 = ((FromNativeConverter)var22).nativeType();
         }
      }

      Object var23 = this.invoke(var6, var21, var8, var10);
      if (var22 != null) {
         Object var14;
         if (var1 != null) {
            var14 = new MethodResultContext(var3, this, var4, var1);
         } else {
            var14 = new FunctionResultContext(var3, this, var4);
         }

         var23 = ((FromNativeConverter)var22).fromNative(var23, (FromNativeContext)var14);
      }

      if (var4 != null) {
         for (int var24 = 0; var24 < var4.length; var24++) {
            Object var15 = var4[var24];
            if (var15 != null) {
               if (var15 instanceof Structure) {
                  if (!(var15 instanceof Structure.ByValue)) {
                     ((Structure)var15).autoRead();
                  }
               } else if (var6[var24] instanceof Function.PostCallRead) {
                  ((Function.PostCallRead)var6[var24]).read();
                  if (var6[var24] instanceof Function.PointerArray) {
                     Function.PointerArray var16 = (Function.PointerArray)var6[var24];
                     if (Structure.ByReference[].class.isAssignableFrom(var15.getClass())) {
                        Class var17 = var15.getClass().getComponentType();
                        Structure[] var18 = (Structure[])var15;

                        for (int var19 = 0; var19 < var18.length; var19++) {
                           Pointer var20 = var16.getPointer((long)(Pointer.SIZE * var19));
                           var18[var19] = Structure.updateStructureByReference(var17, var18[var19], var20);
                        }
                     }
                  }
               } else if (Structure[].class.isAssignableFrom(var15.getClass())) {
                  Structure.autoRead((Structure[])var15);
               }
            }
         }
      }

      return var23;
   }

   Object invoke(Object[] var1, Class<?> var2, boolean var3) {
      return this.invoke(var1, var2, var3, 0);
   }

   Object invoke(Object[] var1, Class<?> var2, boolean var3, int var4) {
      Object var5 = null;
      int var6 = this.callFlags | (var4 & 3) << 7;
      if (var2 == null || var2 == void.class || var2 == Void.class) {
         Native.invokeVoid(this, this.peer, var6, var1);
         var5 = null;
      } else if (var2 == boolean.class || var2 == Boolean.class) {
         var5 = valueOf(Native.invokeInt(this, this.peer, var6, var1) != 0);
      } else if (var2 == byte.class || var2 == Byte.class) {
         var5 = (byte)Native.invokeInt(this, this.peer, var6, var1);
      } else if (var2 == short.class || var2 == Short.class) {
         var5 = (short)Native.invokeInt(this, this.peer, var6, var1);
      } else if (var2 == char.class || var2 == Character.class) {
         var5 = (char)Native.invokeInt(this, this.peer, var6, var1);
      } else if (var2 == int.class || var2 == Integer.class) {
         var5 = Native.invokeInt(this, this.peer, var6, var1);
      } else if (var2 == long.class || var2 == Long.class) {
         var5 = Native.invokeLong(this, this.peer, var6, var1);
      } else if (var2 == float.class || var2 == Float.class) {
         var5 = Native.invokeFloat(this, this.peer, var6, var1);
      } else if (var2 == double.class || var2 == Double.class) {
         var5 = Native.invokeDouble(this, this.peer, var6, var1);
      } else if (var2 == String.class) {
         var5 = this.invokeString(var6, var1, false);
      } else if (var2 == WString.class) {
         String var7 = this.invokeString(var6, var1, true);
         if (var7 != null) {
            var5 = new WString(var7);
         }
      } else {
         if (Pointer.class.isAssignableFrom(var2)) {
            return this.invokePointer(var6, var1);
         }

         if (Structure.class.isAssignableFrom(var2)) {
            if (Structure.ByValue.class.isAssignableFrom(var2)) {
               Structure var11 = Native.invokeStructure(this, this.peer, var6, var1, Structure.newInstance(var2));
               var11.autoRead();
               var5 = var11;
            } else {
               var5 = this.invokePointer(var6, var1);
               if (var5 != null) {
                  Structure var12 = Structure.newInstance(var2, (Pointer)var5);
                  var12.conditionalAutoRead();
                  var5 = var12;
               }
            }
         } else if (Callback.class.isAssignableFrom(var2)) {
            var5 = this.invokePointer(var6, var1);
            if (var5 != null) {
               var5 = CallbackReference.getCallback(var2, (Pointer)var5);
            }
         } else if (var2 == String[].class) {
            Pointer var13 = this.invokePointer(var6, var1);
            if (var13 != null) {
               var5 = var13.getStringArray(0L, this.encoding);
            }
         } else if (var2 == WString[].class) {
            Pointer var14 = this.invokePointer(var6, var1);
            if (var14 != null) {
               String[] var8 = var14.getWideStringArray(0L);
               WString[] var9 = new WString[var8.length];

               for (int var10 = 0; var10 < var8.length; var10++) {
                  var9[var10] = new WString(var8[var10]);
               }

               var5 = var9;
            }
         } else if (var2 == Pointer[].class) {
            Pointer var15 = this.invokePointer(var6, var1);
            if (var15 != null) {
               var5 = var15.getPointerArray(0L);
            }
         } else {
            if (!var3) {
               throw new IllegalArgumentException("Unsupported return type " + var2 + " in function " + this.getName());
            }

            var5 = Native.invokeObject(this, this.peer, var6, var1);
            if (var5 != null && !var2.isAssignableFrom(var5.getClass())) {
               throw new ClassCastException("Return type " + var2 + " does not match result " + var5.getClass());
            }
         }
      }

      return var5;
   }

   private Pointer invokePointer(int var1, Object[] var2) {
      long var3 = Native.invokePointer(this, this.peer, var1, var2);
      return var3 == 0L ? null : new Pointer(var3);
   }

   private Object convertArgument(Object[] var1, int var2, Method var3, TypeMapper var4, boolean var5, Class<?> var6) {
      Object var7 = var1[var2];
      if (var7 != null) {
         Class var8 = var7.getClass();
         Object var9 = null;
         if (NativeMapped.class.isAssignableFrom(var8)) {
            var9 = NativeMappedConverter.getInstance(var8);
         } else if (var4 != null) {
            var9 = var4.getToNativeConverter(var8);
         }

         if (var9 != null) {
            Object var10;
            if (var3 != null) {
               var10 = new MethodParameterContext(this, var1, var2, var3);
            } else {
               var10 = new FunctionParameterContext(this, var1, var2);
            }

            var7 = ((ToNativeConverter)var9).toNative(var7, (ToNativeContext)var10);
         }
      }

      if (var7 != null && !this.isPrimitiveArray(var7.getClass())) {
         Class var14 = var7.getClass();
         if (var7 instanceof Structure) {
            Structure var16 = (Structure)var7;
            var16.autoWrite();
            if (var16 instanceof Structure.ByValue) {
               Class var18 = var16.getClass();
               if (var3 != null) {
                  Class[] var19 = var3.getParameterTypes();
                  if (IS_VARARGS.isVarArgs(var3)) {
                     if (var2 < var19.length - 1) {
                        var18 = var19[var2];
                     } else {
                        Class var21 = var19[var19.length - 1].getComponentType();
                        if (var21 != Object.class) {
                           var18 = var21;
                        }
                     }
                  } else {
                     var18 = var19[var2];
                  }
               }

               if (Structure.ByValue.class.isAssignableFrom(var18)) {
                  return var16;
               }
            }

            return var16.getPointer();
         } else if (var7 instanceof Callback) {
            return CallbackReference.getFunctionPointer((Callback)var7);
         } else if (var7 instanceof String) {
            return new NativeString((String)var7, false).getPointer();
         } else if (var7 instanceof WString) {
            return new NativeString(var7.toString(), true).getPointer();
         } else if (var7 instanceof Boolean) {
            return Boolean.TRUE.equals(var7) ? INTEGER_TRUE : INTEGER_FALSE;
         } else if (String[].class == var14) {
            return new StringArray((String[])var7, this.encoding);
         } else if (WString[].class == var14) {
            return new StringArray((WString[])var7);
         } else if (Pointer[].class == var14) {
            return new Function.PointerArray((Pointer[])var7);
         } else if (NativeMapped[].class.isAssignableFrom(var14)) {
            return new Function.NativeMappedArray((NativeMapped[])var7);
         } else if (Structure[].class.isAssignableFrom(var14)) {
            Structure[] var15 = (Structure[])var7;
            Class var17 = var14.getComponentType();
            boolean var11 = Structure.ByReference.class.isAssignableFrom(var17);
            if (var6 != null && !Structure.ByReference[].class.isAssignableFrom(var6)) {
               if (var11) {
                  throw new IllegalArgumentException(
                     "Function " + this.getName() + " declared Structure[] at parameter " + var2 + " but array of " + var17 + " was passed"
                  );
               }

               for (int var12 = 0; var12 < var15.length; var12++) {
                  if (var15[var12] instanceof Structure.ByReference) {
                     throw new IllegalArgumentException(
                        "Function "
                           + this.getName()
                           + " declared Structure[] at parameter "
                           + var2
                           + " but element "
                           + var12
                           + " is of Structure.ByReference type"
                     );
                  }
               }
            }

            if (var11) {
               Structure.autoWrite(var15);
               Pointer[] var20 = new Pointer[var15.length + 1];

               for (int var13 = 0; var13 < var15.length; var13++) {
                  var20[var13] = var15[var13] != null ? var15[var13].getPointer() : null;
               }

               return new Function.PointerArray(var20);
            } else if (var15.length == 0) {
               throw new IllegalArgumentException("Structure array must have non-zero length");
            } else if (var15[0] == null) {
               Structure.newInstance(var17).toArray(var15);
               return var15[0].getPointer();
            } else {
               Structure.autoWrite(var15);
               return var15[0].getPointer();
            }
         } else if (var14.isArray()) {
            throw new IllegalArgumentException("Unsupported array argument type: " + var14.getComponentType());
         } else if (var5) {
            return var7;
         } else if (!Native.isSupportedNativeType(var7.getClass())) {
            throw new IllegalArgumentException(
               "Unsupported argument type " + var7.getClass().getName() + " at parameter " + var2 + " of function " + this.getName()
            );
         } else {
            return var7;
         }
      } else {
         return var7;
      }
   }

   private boolean isPrimitiveArray(Class<?> var1) {
      return var1.isArray() && var1.getComponentType().isPrimitive();
   }

   public void invoke(Object[] var1) {
      this.invoke(Void.class, var1);
   }

   private String invokeString(int var1, Object[] var2, boolean var3) {
      Pointer var4 = this.invokePointer(var1, var2);
      String var5 = null;
      if (var4 != null) {
         if (var3) {
            var5 = var4.getWideString(0L);
         } else {
            var5 = var4.getString(0L, this.encoding);
         }
      }

      return var5;
   }

   @Override
   public String toString() {
      return this.library != null
         ? "native function " + this.functionName + "(" + this.library.getName() + ")@0x" + Long.toHexString(this.peer)
         : "native function@0x" + Long.toHexString(this.peer);
   }

   public Object invokeObject(Object[] var1) {
      return this.invoke(Object.class, var1);
   }

   public Pointer invokePointer(Object[] var1) {
      return (Pointer)this.invoke(Pointer.class, var1);
   }

   public String invokeString(Object[] var1, boolean var2) {
      Object var3 = this.invoke(var2 ? WString.class : String.class, var1);
      return var3 != null ? var3.toString() : null;
   }

   public int invokeInt(Object[] var1) {
      return (Integer)this.invoke(Integer.class, var1);
   }

   public long invokeLong(Object[] var1) {
      return (Long)this.invoke(Long.class, var1);
   }

   public float invokeFloat(Object[] var1) {
      return (Float)this.invoke(Float.class, var1);
   }

   public double invokeDouble(Object[] var1) {
      return (Double)this.invoke(Double.class, var1);
   }

   public void invokeVoid(Object[] var1) {
      this.invoke(Void.class, var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (var1.getClass() != this.getClass()) {
         return false;
      } else {
         Function var2 = (Function)var1;
         return var2.callFlags == this.callFlags && var2.options.equals(this.options) && var2.peer == this.peer;
      }
   }

   @Override
   public int hashCode() {
      return this.callFlags + this.options.hashCode() + super.hashCode();
   }

   static Object[] concatenateVarArgs(Object[] var0) {
      if (var0 != null && var0.length > 0) {
         Object var1 = var0[var0.length - 1];
         Class var2 = var1 != null ? var1.getClass() : null;
         if (var2 != null && var2.isArray()) {
            Object[] var3 = (Object[])var1;

            for (int var4 = 0; var4 < var3.length; var4++) {
               if (var3[var4] instanceof Float) {
                  var3[var4] = (double)((Float)var3[var4]).floatValue();
               }
            }

            Object[] var5 = new Object[var0.length + var3.length];
            System.arraycopy(var0, 0, var5, 0, var0.length - 1);
            System.arraycopy(var3, 0, var5, var0.length - 1, var3.length);
            var5[var5.length - 1] = null;
            var0 = var5;
         }
      }

      return var0;
   }

   static boolean isVarArgs(Method var0) {
      return IS_VARARGS.isVarArgs(var0);
   }

   static int fixedArgs(Method var0) {
      return IS_VARARGS.fixedArgs(var0);
   }

   static Boolean valueOf(boolean var0) {
      return var0 ? Boolean.TRUE : Boolean.FALSE;
   }

   private static class NativeMappedArray extends Memory implements Function.PostCallRead {
      private final NativeMapped[] original;

      public NativeMappedArray(NativeMapped[] var1) {
         super((long)Native.getNativeSize(var1.getClass(), var1));
         this.original = var1;
         this.setValue(0L, this.original, this.original.getClass());
      }

      @Override
      public void read() {
         this.getValue(0L, this.original.getClass(), this.original);
      }
   }

   private static class PointerArray extends Memory implements Function.PostCallRead {
      private final Pointer[] original;

      public PointerArray(Pointer[] var1) {
         super((long)(Pointer.SIZE * (var1.length + 1)));
         this.original = var1;

         for (int var2 = 0; var2 < var1.length; var2++) {
            this.setPointer((long)(var2 * Pointer.SIZE), var1[var2]);
         }

         this.setPointer((long)(Pointer.SIZE * var1.length), null);
      }

      @Override
      public void read() {
         this.read(0L, this.original, 0, this.original.length);
      }
   }

   public interface PostCallRead {
      void read();
   }
}
