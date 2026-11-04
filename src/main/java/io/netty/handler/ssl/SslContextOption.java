package io.netty.handler.ssl;

import io.netty.util.AbstractConstant;
import io.netty.util.ConstantPool;
import io.netty.util.internal.ObjectUtil;

public class SslContextOption<T> extends AbstractConstant<SslContextOption<T>> {
   private static final ConstantPool<SslContextOption<Object>> pool = new ConstantPool<SslContextOption<Object>>() {
      protected SslContextOption<Object> newConstant(int var1, String var2) {
         return new SslContextOption<>(var1, var2);
      }
   };

   public static <T> SslContextOption<T> valueOf(String var0) {
      return (SslContextOption<T>)pool.valueOf(var0);
   }

   public static <T> SslContextOption<T> valueOf(Class<?> var0, String var1) {
      return (SslContextOption<T>)pool.valueOf(var0, var1);
   }

   public static boolean exists(String var0) {
      return pool.exists(var0);
   }

   private SslContextOption(int var1, String var2) {
      super(var1, var2);
   }

   protected SslContextOption(String var1) {
      this(pool.nextId(), var1);
   }

   public void validate(T var1) {
      ObjectUtil.checkNotNull(var1, "value");
   }
}
