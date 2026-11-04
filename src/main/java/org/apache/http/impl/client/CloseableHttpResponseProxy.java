package org.apache.http.impl.client;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

@Deprecated
class CloseableHttpResponseProxy implements InvocationHandler {
   private static final Constructor<?> CONSTRUCTOR;
   private final HttpResponse original;

   CloseableHttpResponseProxy(HttpResponse var1) {
      this.original = var1;
   }

   public void close() throws IOException {
      HttpEntity var1 = this.original.getEntity();
      EntityUtils.consume(var1);
   }

   @Override
   public Object invoke(Object var1, Method var2, Object[] var3) throws Throwable {
      String var4 = var2.getName();
      if (var4.equals("close")) {
         this.close();
         return null;
      } else {
         try {
            return var2.invoke(this.original, var3);
         } catch (InvocationTargetException var7) {
            Throwable var6 = var7.getCause();
            if (var6 != null) {
               throw var6;
            } else {
               throw var7;
            }
         }
      }
   }

   public static CloseableHttpResponse newProxy(HttpResponse var0) {
      try {
         return (CloseableHttpResponse)CONSTRUCTOR.newInstance(new CloseableHttpResponseProxy(var0));
      } catch (InstantiationException var2) {
         throw new IllegalStateException(var2);
      } catch (InvocationTargetException var3) {
         throw new IllegalStateException(var3);
      } catch (IllegalAccessException var4) {
         throw new IllegalStateException(var4);
      }
   }

   static {
      try {
         CONSTRUCTOR = Proxy.getProxyClass(CloseableHttpResponseProxy.class.getClassLoader(), CloseableHttpResponse.class)
            .getConstructor(InvocationHandler.class);
      } catch (NoSuchMethodException var1) {
         throw new IllegalStateException(var1);
      }
   }
}
