package com.sun.jna;

import com.sun.jna.win32.DLLCallback;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class CallbackReference extends WeakReference<Callback> {
   static final Map<Callback, CallbackReference> callbackMap = new WeakHashMap<>();
   static final Map<Callback, CallbackReference> directCallbackMap = new WeakHashMap<>();
   static final Map<Pointer, Reference<Callback>> pointerCallbackMap = new WeakHashMap<>();
   static final Map<Object, Object> allocations = new WeakHashMap<>();
   private static final Map<CallbackReference, Reference<CallbackReference>> allocatedMemory = Collections.synchronizedMap(new WeakHashMap<>());
   private static final Method PROXY_CALLBACK_METHOD;
   private static final Map<Callback, CallbackThreadInitializer> initializers;
   Pointer cbstruct;
   Pointer trampoline;
   CallbackProxy proxy;
   Method method;
   int callingConvention;

   static CallbackThreadInitializer setCallbackThreadInitializer(Callback var0, CallbackThreadInitializer var1) {
      synchronized (initializers) {
         return var1 != null ? initializers.put(var0, var1) : initializers.remove(var0);
      }
   }

   private static ThreadGroup initializeThread(Callback var0, CallbackReference.AttachOptions var1) {
      CallbackThreadInitializer var2 = null;
      if (var0 instanceof CallbackReference.DefaultCallbackProxy) {
         var0 = ((CallbackReference.DefaultCallbackProxy)var0).getCallback();
      }

      synchronized (initializers) {
         var2 = initializers.get(var0);
      }

      ThreadGroup var7 = null;
      if (var2 != null) {
         var7 = var2.getThreadGroup(var0);
         var1.name = var2.getName(var0);
         var1.daemon = var2.isDaemon(var0);
         var1.detach = var2.detach(var0);
         var1.write();
      }

      return var7;
   }

   public static Callback getCallback(Class<?> var0, Pointer var1) {
      return getCallback(var0, var1, false);
   }

   private static Callback getCallback(Class<?> var0, Pointer var1, boolean var2) {
      if (var1 == null) {
         return null;
      } else if (!var0.isInterface()) {
         throw new IllegalArgumentException("Callback type must be an interface");
      } else {
         Map var3 = var2 ? directCallbackMap : callbackMap;
         synchronized (pointerCallbackMap) {
            Callback var5 = null;
            Reference var6 = pointerCallbackMap.get(var1);
            if (var6 != null) {
               var5 = (Callback)var6.get();
               if (var5 != null && !var0.isAssignableFrom(var5.getClass())) {
                  throw new IllegalStateException(
                     "Pointer "
                        + var1
                        + " already mapped to "
                        + var5
                        + ".\nNative code may be re-using a default function pointer, in which case you may need to use a common Callback class wherever the function pointer is reused."
                  );
               } else {
                  return var5;
               }
            } else {
               int var7 = AltCallingConvention.class.isAssignableFrom(var0) ? 63 : 0;
               HashMap var8 = new HashMap<>(Native.getLibraryOptions(var0));
               var8.put("invoking-method", getCallbackMethod(var0));
               CallbackReference.NativeFunctionHandler var9 = new CallbackReference.NativeFunctionHandler(var1, var7, var8);
               var5 = (Callback)Proxy.newProxyInstance(var0.getClassLoader(), new Class[]{var0}, var9);
               var3.remove(var5);
               pointerCallbackMap.put(var1, new WeakReference<>(var5));
               return var5;
            }
         }
      }
   }

   private CallbackReference(Callback var1, int var2, boolean var3) {
      super(var1);
      TypeMapper var4 = Native.getTypeMapper(var1.getClass());
      this.callingConvention = var2;
      boolean var7 = Platform.isPPC();
      if (var3) {
         Method var8 = getCallbackMethod(var1);
         Class[] var9 = var8.getParameterTypes();

         for (int var10 = 0; var10 < var9.length; var10++) {
            if (var7 && (var9[var10] == float.class || var9[var10] == double.class)) {
               var3 = false;
               break;
            }

            if (var4 != null && var4.getFromNativeConverter(var9[var10]) != null) {
               var3 = false;
               break;
            }
         }

         if (var4 != null && var4.getToNativeConverter(var8.getReturnType()) != null) {
            var3 = false;
         }
      }

      String var16 = Native.getStringEncoding(var1.getClass());
      long var17 = 0L;
      if (var3) {
         this.method = getCallbackMethod(var1);
         Class[] var5 = this.method.getParameterTypes();
         Class var6 = this.method.getReturnType();
         byte var11 = 1;
         if (var1 instanceof DLLCallback) {
            var11 |= 2;
         }

         var17 = Native.createNativeCallback(var1, this.method, var5, var6, var2, var11, var16);
      } else {
         if (var1 instanceof CallbackProxy) {
            this.proxy = (CallbackProxy)var1;
         } else {
            this.proxy = new CallbackReference.DefaultCallbackProxy(getCallbackMethod(var1), var4, var16);
         }

         Class[] var13 = this.proxy.getParameterTypes();
         Class var14 = this.proxy.getReturnType();
         if (var4 != null) {
            for (int var19 = 0; var19 < var13.length; var19++) {
               FromNativeConverter var12 = var4.getFromNativeConverter(var13[var19]);
               if (var12 != null) {
                  var13[var19] = var12.nativeType();
               }
            }

            ToNativeConverter var20 = var4.getToNativeConverter(var14);
            if (var20 != null) {
               var14 = var20.nativeType();
            }
         }

         for (int var21 = 0; var21 < var13.length; var21++) {
            var13[var21] = this.getNativeType(var13[var21]);
            if (!isAllowableNativeType(var13[var21])) {
               String var24 = "Callback argument " + var13[var21] + " requires custom type conversion";
               throw new IllegalArgumentException(var24);
            }
         }

         var14 = this.getNativeType(var14);
         if (!isAllowableNativeType(var14)) {
            String var23 = "Callback return type " + var14 + " requires custom type conversion";
            throw new IllegalArgumentException(var23);
         }

         int var22 = var1 instanceof DLLCallback ? 2 : 0;
         var17 = Native.createNativeCallback(this.proxy, PROXY_CALLBACK_METHOD, var13, var14, var2, var22, var16);
      }

      this.cbstruct = var17 != 0L ? new Pointer(var17) : null;
      allocatedMemory.put(this, new WeakReference<>(this));
   }

   private Class<?> getNativeType(Class<?> var1) {
      if (Structure.class.isAssignableFrom(var1)) {
         Structure.validate(var1);
         if (!Structure.ByValue.class.isAssignableFrom(var1)) {
            return Pointer.class;
         }
      } else {
         if (NativeMapped.class.isAssignableFrom(var1)) {
            return NativeMappedConverter.getInstance(var1).nativeType();
         }

         if (var1 == String.class || var1 == WString.class || var1 == String[].class || var1 == WString[].class || Callback.class.isAssignableFrom(var1)) {
            return Pointer.class;
         }
      }

      return var1;
   }

   private static Method checkMethod(Method var0) {
      if (var0.getParameterTypes().length > 256) {
         String var1 = "Method signature exceeds the maximum parameter count: " + var0;
         throw new UnsupportedOperationException(var1);
      } else {
         return var0;
      }
   }

   static Class<?> findCallbackClass(Class<?> var0) {
      if (!Callback.class.isAssignableFrom(var0)) {
         throw new IllegalArgumentException(var0.getName() + " is not derived from com.sun.jna.Callback");
      } else if (var0.isInterface()) {
         return var0;
      } else {
         Class[] var1 = var0.getInterfaces();

         for (int var2 = 0; var2 < var1.length; var2++) {
            if (Callback.class.isAssignableFrom(var1[var2])) {
               try {
                  getCallbackMethod(var1[var2]);
                  return var1[var2];
               } catch (IllegalArgumentException var4) {
                  break;
               }
            }
         }

         return Callback.class.isAssignableFrom(var0.getSuperclass()) ? findCallbackClass(var0.getSuperclass()) : var0;
      }
   }

   private static Method getCallbackMethod(Callback var0) {
      return getCallbackMethod(findCallbackClass(var0.getClass()));
   }

   private static Method getCallbackMethod(Class<?> var0) {
      Method[] var1 = var0.getDeclaredMethods();
      Method[] var2 = var0.getMethods();
      HashSet var3 = new HashSet<>(Arrays.asList(var1));
      var3.retainAll(Arrays.asList(var2));
      Iterator var4 = var3.iterator();

      while (var4.hasNext()) {
         Method var5 = (Method)var4.next();
         if (Callback.FORBIDDEN_NAMES.contains(var5.getName())) {
            var4.remove();
         }
      }

      Method[] var7 = var3.toArray(new Method[var3.size()]);
      if (var7.length == 1) {
         return checkMethod(var7[0]);
      } else {
         for (int var8 = 0; var8 < var7.length; var8++) {
            Method var6 = var7[var8];
            if ("callback".equals(var6.getName())) {
               return checkMethod(var6);
            }
         }

         String var9 = "Callback must implement a single public method, or one public method named 'callback'";
         throw new IllegalArgumentException(var9);
      }
   }

   private void setCallbackOptions(int var1) {
      this.cbstruct.setInt((long)Pointer.SIZE, var1);
   }

   public Pointer getTrampoline() {
      if (this.trampoline == null) {
         this.trampoline = this.cbstruct.getPointer(0L);
      }

      return this.trampoline;
   }

   @Override
   protected void finalize() {
      this.dispose();
   }

   protected synchronized void dispose() {
      if (this.cbstruct != null) {
         try {
            Native.freeNativeCallback(this.cbstruct.peer);
         } finally {
            this.cbstruct.peer = 0L;
            this.cbstruct = null;
            allocatedMemory.remove(this);
         }
      }
   }

   static void disposeAll() {
      for (CallbackReference var2 : new LinkedList<>(allocatedMemory.keySet())) {
         var2.dispose();
      }
   }

   private Callback getCallback() {
      return this.get();
   }

   private static Pointer getNativeFunctionPointer(Callback var0) {
      if (Proxy.isProxyClass(var0.getClass())) {
         InvocationHandler var1 = Proxy.getInvocationHandler(var0);
         if (var1 instanceof CallbackReference.NativeFunctionHandler) {
            return ((CallbackReference.NativeFunctionHandler)var1).getPointer();
         }
      }

      return null;
   }

   public static Pointer getFunctionPointer(Callback var0) {
      return getFunctionPointer(var0, false);
   }

   private static Pointer getFunctionPointer(Callback var0, boolean var1) {
      Pointer var2 = null;
      if (var0 == null) {
         return null;
      } else if ((var2 = getNativeFunctionPointer(var0)) != null) {
         return var2;
      } else {
         Map var3 = Native.getLibraryOptions(var0.getClass());
         int var4 = var0 instanceof AltCallingConvention
            ? 63
            : (var3 != null && var3.containsKey("calling-convention") ? (Integer)var3.get("calling-convention") : 0);
         Map var5 = var1 ? directCallbackMap : callbackMap;
         synchronized (pointerCallbackMap) {
            CallbackReference var7 = (CallbackReference)var5.get(var0);
            if (var7 == null) {
               var7 = new CallbackReference(var0, var4, var1);
               var5.put(var0, var7);
               pointerCallbackMap.put(var7.getTrampoline(), new WeakReference<>(var0));
               if (initializers.containsKey(var0)) {
                  var7.setCallbackOptions(1);
               }
            }

            return var7.getTrampoline();
         }
      }
   }

   private static boolean isAllowableNativeType(Class<?> var0) {
      return var0 == void.class
         || var0 == Void.class
         || var0 == boolean.class
         || var0 == Boolean.class
         || var0 == byte.class
         || var0 == Byte.class
         || var0 == short.class
         || var0 == Short.class
         || var0 == char.class
         || var0 == Character.class
         || var0 == int.class
         || var0 == Integer.class
         || var0 == long.class
         || var0 == Long.class
         || var0 == float.class
         || var0 == Float.class
         || var0 == double.class
         || var0 == Double.class
         || Structure.ByValue.class.isAssignableFrom(var0) && Structure.class.isAssignableFrom(var0)
         || Pointer.class.isAssignableFrom(var0);
   }

   private static Pointer getNativeString(Object var0, boolean var1) {
      if (var0 != null) {
         NativeString var2 = new NativeString(var0.toString(), var1);
         allocations.put(var0, var2);
         return var2.getPointer();
      } else {
         return null;
      }
   }

   static {
      try {
         PROXY_CALLBACK_METHOD = CallbackProxy.class.getMethod("callback", Object[].class);
      } catch (Exception var1) {
         throw new Error("Error looking up CallbackProxy.callback() method");
      }

      initializers = new WeakHashMap<>();
   }

   static class AttachOptions extends Structure {
      public static final List<String> FIELDS = createFieldsOrder(new String[]{"daemon", "detach", "name"});
      public boolean daemon;
      public boolean detach;
      public String name;

      AttachOptions() {
         this.setStringEncoding("utf8");
      }

      @Override
      protected List<String> getFieldOrder() {
         return FIELDS;
      }
   }

   private class DefaultCallbackProxy implements CallbackProxy {
      private final Method callbackMethod;
      private ToNativeConverter toNative;
      private final FromNativeConverter[] fromNative;
      private final String encoding;

      public DefaultCallbackProxy(Method var2, TypeMapper var3, String var4) {
         this.callbackMethod = var2;
         this.encoding = var4;
         Class[] var5 = var2.getParameterTypes();
         Class var6 = var2.getReturnType();
         this.fromNative = new FromNativeConverter[var5.length];
         if (NativeMapped.class.isAssignableFrom(var6)) {
            this.toNative = NativeMappedConverter.getInstance(var6);
         } else if (var3 != null) {
            this.toNative = var3.getToNativeConverter(var6);
         }

         for (int var7 = 0; var7 < this.fromNative.length; var7++) {
            if (NativeMapped.class.isAssignableFrom(var5[var7])) {
               this.fromNative[var7] = new NativeMappedConverter(var5[var7]);
            } else if (var3 != null) {
               this.fromNative[var7] = var3.getFromNativeConverter(var5[var7]);
            }
         }

         if (!var2.isAccessible()) {
            try {
               var2.setAccessible(true);
            } catch (SecurityException var8) {
               throw new IllegalArgumentException("Callback method is inaccessible, make sure the interface is public: " + var2);
            }
         }
      }

      public Callback getCallback() {
         return CallbackReference.this.getCallback();
      }

      private Object invokeCallback(Object[] var1) {
         Class[] var2 = this.callbackMethod.getParameterTypes();
         Object[] var3 = new Object[var1.length];

         for (int var4 = 0; var4 < var1.length; var4++) {
            Class var5 = var2[var4];
            Object var6 = var1[var4];
            if (this.fromNative[var4] != null) {
               CallbackParameterContext var7 = new CallbackParameterContext(var5, this.callbackMethod, var1, var4);
               var3[var4] = this.fromNative[var4].fromNative(var6, var7);
            } else {
               var3[var4] = this.convertArgument(var6, var5);
            }
         }

         Object var11 = null;
         Callback var12 = this.getCallback();
         if (var12 != null) {
            try {
               var11 = this.convertResult(this.callbackMethod.invoke(var12, var3));
            } catch (IllegalArgumentException var8) {
               Native.getCallbackExceptionHandler().uncaughtException(var12, var8);
            } catch (IllegalAccessException var9) {
               Native.getCallbackExceptionHandler().uncaughtException(var12, var9);
            } catch (InvocationTargetException var10) {
               Native.getCallbackExceptionHandler().uncaughtException(var12, var10.getTargetException());
            }
         }

         for (int var13 = 0; var13 < var3.length; var13++) {
            if (var3[var13] instanceof Structure && !(var3[var13] instanceof Structure.ByValue)) {
               ((Structure)var3[var13]).autoWrite();
            }
         }

         return var11;
      }

      @Override
      public Object callback(Object[] var1) {
         try {
            return this.invokeCallback(var1);
         } catch (Throwable var3) {
            Native.getCallbackExceptionHandler().uncaughtException(this.getCallback(), var3);
            return null;
         }
      }

      private Object convertArgument(Object var1, Class<?> var2) {
         if (var1 instanceof Pointer) {
            if (var2 == String.class) {
               var1 = ((Pointer)var1).getString(0L, this.encoding);
            } else if (var2 == WString.class) {
               var1 = new WString(((Pointer)var1).getWideString(0L));
            } else if (var2 == String[].class) {
               var1 = ((Pointer)var1).getStringArray(0L, this.encoding);
            } else if (var2 == WString[].class) {
               var1 = ((Pointer)var1).getWideStringArray(0L);
            } else if (Callback.class.isAssignableFrom(var2)) {
               var1 = CallbackReference.getCallback(var2, (Pointer)var1);
            } else if (Structure.class.isAssignableFrom(var2)) {
               if (Structure.ByValue.class.isAssignableFrom(var2)) {
                  Structure var3 = Structure.newInstance(var2);
                  byte[] var4 = new byte[var3.size()];
                  ((Pointer)var1).read(0L, var4, 0, var4.length);
                  var3.getPointer().write(0L, var4, 0, var4.length);
                  var3.read();
                  var1 = var3;
               } else {
                  Structure var5 = Structure.newInstance(var2, (Pointer)var1);
                  var5.conditionalAutoRead();
                  var1 = var5;
               }
            }
         } else if ((boolean.class == var2 || Boolean.class == var2) && var1 instanceof Number) {
            var1 = Function.valueOf(((Number)var1).intValue() != 0);
         }

         return var1;
      }

      private Object convertResult(Object var1) {
         if (this.toNative != null) {
            var1 = this.toNative.toNative(var1, new CallbackResultContext(this.callbackMethod));
         }

         if (var1 == null) {
            return null;
         } else {
            Class var2 = var1.getClass();
            if (Structure.class.isAssignableFrom(var2)) {
               return Structure.ByValue.class.isAssignableFrom(var2) ? var1 : ((Structure)var1).getPointer();
            } else if (var2 == boolean.class || var2 == Boolean.class) {
               return Boolean.TRUE.equals(var1) ? Function.INTEGER_TRUE : Function.INTEGER_FALSE;
            } else if (var2 == String.class || var2 == WString.class) {
               return CallbackReference.getNativeString(var1, var2 == WString.class);
            } else if (var2 != String[].class && var2 != WString.class) {
               return Callback.class.isAssignableFrom(var2) ? CallbackReference.getFunctionPointer((Callback)var1) : var1;
            } else {
               StringArray var3 = var2 == String[].class ? new StringArray((String[])var1, this.encoding) : new StringArray((WString[])var1);
               CallbackReference.allocations.put(var1, var3);
               return var3;
            }
         }
      }

      @Override
      public Class<?>[] getParameterTypes() {
         return this.callbackMethod.getParameterTypes();
      }

      @Override
      public Class<?> getReturnType() {
         return this.callbackMethod.getReturnType();
      }
   }

   private static class NativeFunctionHandler implements InvocationHandler {
      private final Function function;
      private final Map<String, ?> options;

      public NativeFunctionHandler(Pointer var1, int var2, Map<String, ?> var3) {
         this.options = var3;
         this.function = new Function(var1, var2, (String)var3.get("string-encoding"));
      }

      @Override
      public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
         if (Library.Handler.OBJECT_TOSTRING.equals(var2)) {
            String var7 = "Proxy interface to " + this.function;
            Method var5 = (Method)this.options.get("invoking-method");
            Class var6 = CallbackReference.findCallbackClass(var5.getDeclaringClass());
            return var7 + " (" + var6.getName() + ")";
         } else if (Library.Handler.OBJECT_HASHCODE.equals(var2)) {
            return this.hashCode();
         } else if (Library.Handler.OBJECT_EQUALS.equals(var2)) {
            Object var4 = var3[0];
            return var4 != null && Proxy.isProxyClass(var4.getClass()) ? Function.valueOf(Proxy.getInvocationHandler(var4) == this) : Boolean.FALSE;
         } else {
            if (Function.isVarArgs(var2)) {
               var3 = Function.concatenateVarArgs(var3);
            }

            return this.function.invoke(var2.getReturnType(), var3, this.options);
         }
      }

      public Pointer getPointer() {
         return this.function;
      }
   }
}
