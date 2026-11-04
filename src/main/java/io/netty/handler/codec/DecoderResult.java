package io.netty.handler.codec;

import io.netty.util.Signal;
import io.netty.util.internal.ObjectUtil;

public class DecoderResult {
   protected static final Signal SIGNAL_UNFINISHED = Signal.valueOf(DecoderResult.class, "UNFINISHED");
   protected static final Signal SIGNAL_SUCCESS = Signal.valueOf(DecoderResult.class, "SUCCESS");
   public static final DecoderResult UNFINISHED = new DecoderResult(SIGNAL_UNFINISHED);
   public static final DecoderResult SUCCESS = new DecoderResult(SIGNAL_SUCCESS);
   private final Throwable cause;

   public static DecoderResult failure(Throwable var0) {
      return new DecoderResult(ObjectUtil.checkNotNull(var0, "cause"));
   }

   protected DecoderResult(Throwable var1) {
      this.cause = ObjectUtil.checkNotNull(var1, "cause");
   }

   public boolean isFinished() {
      return this.cause != SIGNAL_UNFINISHED;
   }

   public boolean isSuccess() {
      return this.cause == SIGNAL_SUCCESS;
   }

   public boolean isFailure() {
      return this.cause != SIGNAL_SUCCESS && this.cause != SIGNAL_UNFINISHED;
   }

   public Throwable cause() {
      return this.isFailure() ? this.cause : null;
   }

   @Override
   public String toString() {
      if (this.isFinished()) {
         if (this.isSuccess()) {
            return "success";
         } else {
            String var1 = this.cause().toString();
            return new StringBuilder(var1.length() + 17).append("failure(").append(var1).append(')').toString();
         }
      } else {
         return "unfinished";
      }
   }
}
