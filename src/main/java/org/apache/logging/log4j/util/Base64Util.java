package org.apache.logging.log4j.util;

import java.lang.reflect.Method;
import org.apache.logging.log4j.LoggingException;

public final class Base64Util {
   private static Method encodeMethod = null;
   private static Object encoder = null;

   private Base64Util() {
   }

   public static String encode(String var0) {
      if (var0 == null) {
         return null;
      } else {
         byte[] var1 = var0.getBytes();
         if (encodeMethod != null) {
            try {
               return (String)encodeMethod.invoke(encoder, var1);
            } catch (Exception var3) {
               throw new LoggingException("Unable to encode String", var3);
            }
         } else {
            throw new LoggingException("No Encoder, unable to encode string");
         }
      }
   }

   static {
      try {
         Class var0 = LoaderUtil.loadClass("java.util.Base64");
         Class var5 = LoaderUtil.loadClass("java.util.Base64$Encoder");
         Method var2 = var0.getMethod("getEncoder");
         encoder = var2.invoke(null);
         encodeMethod = var5.getMethod("encodeToString", byte[].class);
      } catch (Exception var4) {
         try {
            Class var1 = LoaderUtil.loadClass("javax.xml.bind.DataTypeConverter");
            encodeMethod = var1.getMethod("printBase64Binary");
         } catch (Exception var3) {
            LowLevelLogUtil.logException("Unable to create a Base64 Encoder", var3);
         }
      }
   }
}
