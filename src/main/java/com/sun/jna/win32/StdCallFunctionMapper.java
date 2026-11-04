package com.sun.jna.win32;

import com.sun.jna.Function;
import com.sun.jna.FunctionMapper;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.NativeMapped;
import com.sun.jna.NativeMappedConverter;
import com.sun.jna.Pointer;
import java.lang.reflect.Method;

public class StdCallFunctionMapper implements FunctionMapper {
   protected int getArgumentNativeStackSize(Class<?> var1) {
      if (NativeMapped.class.isAssignableFrom(var1)) {
         var1 = NativeMappedConverter.getInstance(var1).nativeType();
      }

      if (var1.isArray()) {
         return Pointer.SIZE;
      } else {
         try {
            return Native.getNativeSize(var1);
         } catch (IllegalArgumentException var3) {
            throw new IllegalArgumentException("Unknown native stack allocation size for " + var1);
         }
      }
   }

   @Override
   public String getFunctionName(NativeLibrary var1, Method var2) {
      String var3 = var2.getName();
      int var4 = 0;
      Class[] var5 = var2.getParameterTypes();

      for (Class var9 : var5) {
         var4 += this.getArgumentNativeStackSize(var9);
      }

      String var12 = var3 + "@" + var4;
      byte var13 = 63;

      try {
         Function var14 = var1.getFunction(var12, var13);
         var3 = var14.getName();
      } catch (UnsatisfiedLinkError var11) {
         try {
            Function var15 = var1.getFunction("_" + var12, var13);
            var3 = var15.getName();
         } catch (UnsatisfiedLinkError var10) {
         }
      }

      return var3;
   }
}
