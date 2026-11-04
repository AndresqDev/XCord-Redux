package io.netty.handler.codec.spdy;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.ThrowableUtil;

public class SpdyProtocolException extends Exception {
   private static final long serialVersionUID = 7870000537743847264L;

   public SpdyProtocolException() {
   }

   public SpdyProtocolException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public SpdyProtocolException(String var1) {
      super(var1);
   }

   public SpdyProtocolException(Throwable var1) {
      super(var1);
   }

   static SpdyProtocolException newStatic(String var0, Class<?> var1, String var2) {
      SpdyProtocolException.StacklessSpdyProtocolException var3;
      if (PlatformDependent.javaVersion() >= 7) {
         var3 = new SpdyProtocolException.StacklessSpdyProtocolException(var0, true);
      } else {
         var3 = new SpdyProtocolException.StacklessSpdyProtocolException(var0);
      }

      return ThrowableUtil.unknownStackTrace(var3, var1, var2);
   }

   @SuppressJava6Requirement(
      reason = "uses Java 7+ Exception.<init>(String, Throwable, boolean, boolean) but is guarded by version checks"
   )
   private SpdyProtocolException(String var1, boolean var2) {
      super(var1, null, false, true);

      assert var2;
   }

   private static final class StacklessSpdyProtocolException extends SpdyProtocolException {
      private static final long serialVersionUID = -6302754207557485099L;

      StacklessSpdyProtocolException(String var1) {
         super(var1);
      }

      StacklessSpdyProtocolException(String var1, boolean var2) {
         super(var1, var2);
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   }
}
