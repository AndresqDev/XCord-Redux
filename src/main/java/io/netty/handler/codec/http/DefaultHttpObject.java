package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.ObjectUtil;

public class DefaultHttpObject implements HttpObject {
   private static final int HASH_CODE_PRIME = 31;
   private DecoderResult decoderResult = DecoderResult.SUCCESS;

   protected DefaultHttpObject() {
   }

   @Override
   public DecoderResult decoderResult() {
      return this.decoderResult;
   }

   @Deprecated
   @Override
   public DecoderResult getDecoderResult() {
      return this.decoderResult();
   }

   @Override
   public void setDecoderResult(DecoderResult var1) {
      this.decoderResult = ObjectUtil.checkNotNull(var1, "decoderResult");
   }

   @Override
   public int hashCode() {
      byte var1 = 1;
      return 31 * var1 + this.decoderResult.hashCode();
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultHttpObject)) {
         return false;
      } else {
         DefaultHttpObject var2 = (DefaultHttpObject)var1;
         return this.decoderResult().equals(var2.decoderResult());
      }
   }
}
