package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.function.BiFunction;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

@SuppressJava6Requirement(
   reason = "Usage guarded by java version check"
)
final class BouncyCastleAlpnSslUtils {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(BouncyCastleAlpnSslUtils.class);
   private static final Class BC_SSL_PARAMETERS;
   private static final Method SET_PARAMETERS;
   private static final Method SET_APPLICATION_PROTOCOLS;
   private static final Method GET_APPLICATION_PROTOCOL;
   private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL;
   private static final Method SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
   private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
   private static final Class BC_APPLICATION_PROTOCOL_SELECTOR;
   private static final Method BC_APPLICATION_PROTOCOL_SELECTOR_SELECT;

   private BouncyCastleAlpnSslUtils() {
   }

   static String getApplicationProtocol(SSLEngine var0) {
      try {
         return (String)GET_APPLICATION_PROTOCOL.invoke(var0);
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static void setApplicationProtocols(SSLEngine var0, List<String> var1) {
      SSLParameters var2 = var0.getSSLParameters();
      String[] var3 = var1.toArray(EmptyArrays.EMPTY_STRINGS);

      try {
         Object var4 = BC_SSL_PARAMETERS.newInstance();
         SET_APPLICATION_PROTOCOLS.invoke(var4, var3);
         SET_PARAMETERS.invoke(var0, var4);
      } catch (UnsupportedOperationException var5) {
         throw var5;
      } catch (Exception var6) {
         throw new IllegalStateException(var6);
      }

      var0.setSSLParameters(var2);
   }

   static String getHandshakeApplicationProtocol(SSLEngine var0) {
      try {
         return (String)GET_HANDSHAKE_APPLICATION_PROTOCOL.invoke(var0);
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static void setHandshakeApplicationProtocolSelector(SSLEngine var0, final BiFunction<SSLEngine, List<String>, String> var1) {
      try {
         Object var2 = Proxy.newProxyInstance(
            BouncyCastleAlpnSslUtils.class.getClassLoader(), new Class[]{BC_APPLICATION_PROTOCOL_SELECTOR}, new InvocationHandler() {
               @Override
               public Object invoke(Object var1x, Method var2, Object[] var3) throws Throwable {
                  if (var2.getName().equals("select")) {
                     try {
                        return var1.apply((SSLEngine)var3[0], (List)var3[1]);
                     } catch (ClassCastException var5) {
                        throw new RuntimeException("BCApplicationProtocolSelector select method parameter of invalid type.", var5);
                     }
                  } else {
                     throw new UnsupportedOperationException(String.format("Method '%s' not supported.", var2.getName()));
                  }
               }
            }
         );
         SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(var0, var2);
      } catch (UnsupportedOperationException var3) {
         throw var3;
      } catch (Exception var4) {
         throw new IllegalStateException(var4);
      }
   }

   static BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector(SSLEngine var0) {
      try {
         final Object var1 = GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(var0);
         return new BiFunction<SSLEngine, List<String>, String>() {
            public String apply(SSLEngine var1x, List<String> var2) {
               try {
                  return (String)BouncyCastleAlpnSslUtils.BC_APPLICATION_PROTOCOL_SELECTOR_SELECT.invoke(var1, var1x, var2);
               } catch (Exception var4) {
                  throw new RuntimeException("Could not call getHandshakeApplicationProtocolSelector", var4);
               }
            }
         };
      } catch (UnsupportedOperationException var2) {
         throw var2;
      } catch (Exception var3) {
         throw new IllegalStateException(var3);
      }
   }

   static {
      final Class var1;
      Method var2;
      Method var3;
      Method var4;
      Method var5;
      Method var6;
      Method var7;
      Method var8;
      final Class var9;
      try {
         final Class var0 = Class.forName("org.bouncycastle.jsse.BCSSLEngine");
         var1 = Class.forName("org.bouncycastle.jsse.BCSSLParameters");
         Object var11 = var1.newInstance();
         var9 = Class.forName("org.bouncycastle.jsse.BCApplicationProtocolSelector");
         var8 = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return var9.getMethod("select", Object.class, List.class);
            }
         });
         SSLContext var14 = SslUtils.getSSLContext("BCJSSE");
         SSLEngine var15 = var14.createSSLEngine();
         var2 = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return var0.getMethod("setParameters", var1);
            }
         });
         var2.invoke(var15, var11);
         var3 = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return var1.getMethod("setApplicationProtocols", String[].class);
            }
         });
         var3.invoke(var11, EmptyArrays.EMPTY_STRINGS);
         var4 = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return var0.getMethod("getApplicationProtocol");
            }
         });
         var4.invoke(var15);
         var5 = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return var0.getMethod("getHandshakeApplicationProtocol");
            }
         });
         var5.invoke(var15);
         var6 = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return var0.getMethod("setBCHandshakeApplicationProtocolSelector", var9);
            }
         });
         var7 = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
               return var0.getMethod("getBCHandshakeApplicationProtocolSelector");
            }
         });
         var7.invoke(var15);
      } catch (Throwable var16) {
         logger.error("Unable to initialize BouncyCastleAlpnSslUtils.", var16);
         var1 = null;
         var2 = null;
         var3 = null;
         var4 = null;
         var5 = null;
         var6 = null;
         var7 = null;
         var8 = null;
         var9 = null;
      }

      BC_SSL_PARAMETERS = var1;
      SET_PARAMETERS = var2;
      SET_APPLICATION_PROTOCOLS = var3;
      GET_APPLICATION_PROTOCOL = var4;
      GET_HANDSHAKE_APPLICATION_PROTOCOL = var5;
      SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = var6;
      GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = var7;
      BC_APPLICATION_PROTOCOL_SELECTOR_SELECT = var8;
      BC_APPLICATION_PROTOCOL_SELECTOR = var9;
   }
}
