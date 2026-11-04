package com.sun.jna;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public interface Library {
   String OPTION_TYPE_MAPPER = "type-mapper";
   String OPTION_FUNCTION_MAPPER = "function-mapper";
   String OPTION_INVOCATION_MAPPER = "invocation-mapper";
   String OPTION_STRUCTURE_ALIGNMENT = "structure-alignment";
   String OPTION_STRING_ENCODING = "string-encoding";
   String OPTION_ALLOW_OBJECTS = "allow-objects";
   String OPTION_CALLING_CONVENTION = "calling-convention";
   String OPTION_OPEN_FLAGS = "open-flags";
   String OPTION_CLASSLOADER = "classloader";

   public static class Handler implements InvocationHandler {
      static final Method OBJECT_TOSTRING;
      static final Method OBJECT_HASHCODE;
      static final Method OBJECT_EQUALS;
      private final NativeLibrary nativeLibrary;
      private final Class<?> interfaceClass;
      private final Map<String, Object> options;
      private final InvocationMapper invocationMapper;
      private final Map<Method, Library.Handler.FunctionInfo> functions = new WeakHashMap<>();

      public Handler(String var1, Class<?> var2, Map<String, ?> var3) {
         if (var1 != null && "".equals(var1.trim())) {
            throw new IllegalArgumentException("Invalid library name \"" + var1 + "\"");
         } else if (!var2.isInterface()) {
            throw new IllegalArgumentException(var1 + " does not implement an interface: " + var2.getName());
         } else {
            this.interfaceClass = var2;
            this.options = new HashMap<>(var3);
            int var4 = AltCallingConvention.class.isAssignableFrom(var2) ? 63 : 0;
            if (this.options.get("calling-convention") == null) {
               this.options.put("calling-convention", var4);
            }

            if (this.options.get("classloader") == null) {
               this.options.put("classloader", var2.getClassLoader());
            }

            this.nativeLibrary = NativeLibrary.getInstance(var1, this.options);
            this.invocationMapper = (InvocationMapper)this.options.get("invocation-mapper");
         }
      }

      public NativeLibrary getNativeLibrary() {
         return this.nativeLibrary;
      }

      public String getLibraryName() {
         return this.nativeLibrary.getName();
      }

      public Class<?> getInterfaceClass() {
         return this.interfaceClass;
      }

      @Override
      public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
         if (OBJECT_TOSTRING.equals(var2)) {
            return "Proxy interface to " + this.nativeLibrary;
         } else if (OBJECT_HASHCODE.equals(var2)) {
            return this.hashCode();
         } else if (OBJECT_EQUALS.equals(var2)) {
            Object var13 = var3[0];
            return var13 != null && Proxy.isProxyClass(var13.getClass()) ? Function.valueOf(Proxy.getInvocationHandler(var13) == this) : Boolean.FALSE;
         } else {
            Library.Handler.FunctionInfo var4 = this.functions.get(var2);
            if (var4 == null) {
               synchronized (this.functions) {
                  var4 = this.functions.get(var2);
                  if (var4 == null) {
                     boolean var6 = Function.isVarArgs(var2);
                     InvocationHandler var7 = null;
                     if (this.invocationMapper != null) {
                        var7 = this.invocationMapper.getInvocationHandler(this.nativeLibrary, var2);
                     }

                     Function var8 = null;
                     Class[] var9 = null;
                     HashMap var10 = null;
                     if (var7 == null) {
                        var8 = this.nativeLibrary.getFunction(var2.getName(), var2);
                        var9 = var2.getParameterTypes();
                        var10 = new HashMap<>(this.options);
                        var10.put("invoking-method", var2);
                     }

                     var4 = new Library.Handler.FunctionInfo(var7, var8, var9, var6, var10);
                     this.functions.put(var2, var4);
                  }
               }
            }

            if (var4.isVarArgs) {
               var3 = Function.concatenateVarArgs(var3);
            }

            return var4.handler != null
               ? var4.handler.invoke(var1, var2, var3)
               : var4.function.invoke(var2, var4.parameterTypes, var2.getReturnType(), var3, var4.options);
         }
      }

      static {
         try {
            OBJECT_TOSTRING = Object.class.getMethod("toString");
            OBJECT_HASHCODE = Object.class.getMethod("hashCode");
            OBJECT_EQUALS = Object.class.getMethod("equals", Object.class);
         } catch (Exception var1) {
            throw new Error("Error retrieving Object.toString() method");
         }
      }

      private static final class FunctionInfo {
         final InvocationHandler handler;
         final Function function;
         final boolean isVarArgs;
         final Map<String, ?> options;
         final Class<?>[] parameterTypes;

         FunctionInfo(InvocationHandler var1, Function var2, Class<?>[] var3, boolean var4, Map<String, ?> var5) {
            this.handler = var1;
            this.function = var2;
            this.isVarArgs = var4;
            this.options = var5;
            this.parameterTypes = var3;
         }
      }
   }
}
