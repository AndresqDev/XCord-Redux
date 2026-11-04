package io.netty.channel;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;

public class DefaultAddressedEnvelope<M, A extends SocketAddress> implements AddressedEnvelope<M, A> {
   private final M message;
   private final A sender;
   private final A recipient;

   public DefaultAddressedEnvelope(M var1, A var2, A var3) {
      ObjectUtil.checkNotNull(var1, "message");
      if (var2 == null && var3 == null) {
         throw new NullPointerException("recipient and sender");
      } else {
         this.message = (M)var1;
         this.sender = (A)var3;
         this.recipient = (A)var2;
      }
   }

   public DefaultAddressedEnvelope(M var1, A var2) {
      this((M)var1, (A)var2, null);
   }

   @Override
   public M content() {
      return this.message;
   }

   @Override
   public A sender() {
      return this.sender;
   }

   @Override
   public A recipient() {
      return this.recipient;
   }

   @Override
   public int refCnt() {
      return this.message instanceof ReferenceCounted ? ((ReferenceCounted)this.message).refCnt() : 1;
   }

   @Override
   public AddressedEnvelope<M, A> retain() {
      ReferenceCountUtil.retain(this.message);
      return this;
   }

   @Override
   public AddressedEnvelope<M, A> retain(int var1) {
      ReferenceCountUtil.retain(this.message, var1);
      return this;
   }

   @Override
   public boolean release() {
      return ReferenceCountUtil.release(this.message);
   }

   @Override
   public boolean release(int var1) {
      return ReferenceCountUtil.release(this.message, var1);
   }

   @Override
   public AddressedEnvelope<M, A> touch() {
      ReferenceCountUtil.touch(this.message);
      return this;
   }

   @Override
   public AddressedEnvelope<M, A> touch(Object var1) {
      ReferenceCountUtil.touch(this.message, var1);
      return this;
   }

   @Override
   public String toString() {
      return this.sender != null
         ? StringUtil.simpleClassName(this) + '(' + this.sender + " => " + this.recipient + ", " + this.message + ')'
         : StringUtil.simpleClassName(this) + "(=> " + this.recipient + ", " + this.message + ')';
   }
}
