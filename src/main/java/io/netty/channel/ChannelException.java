package io.netty.channel;

import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.ThrowableUtil;

public class ChannelException extends RuntimeException {
   private static final long serialVersionUID = 2908618315971075004L;

   public ChannelException() {
   }

   public ChannelException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public ChannelException(String var1) {
      super(var1);
   }

   public ChannelException(Throwable var1) {
      super(var1);
   }

   @SuppressJava6Requirement(
      reason = "uses Java 7+ RuntimeException.<init>(String, Throwable, boolean, boolean) but is guarded by version checks"
   )
   protected ChannelException(String var1, Throwable var2, boolean var3) {
      super(var1, var2, false, true);

      assert var3;
   }

   static ChannelException newStatic(String var0, Class<?> var1, String var2) {
      ChannelException.StacklessChannelException var3;
      if (PlatformDependent.javaVersion() >= 7) {
         var3 = new ChannelException.StacklessChannelException(var0, null, true);
      } else {
         var3 = new ChannelException.StacklessChannelException(var0, null);
      }

      return ThrowableUtil.unknownStackTrace(var3, var1, var2);
   }

   private static final class StacklessChannelException extends ChannelException {
      private static final long serialVersionUID = -6384642137753538579L;

      StacklessChannelException(String var1, Throwable var2) {
         super(var1, var2);
      }

      StacklessChannelException(String var1, Throwable var2, boolean var3) {
         super(var1, var2, var3);
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   }
}
