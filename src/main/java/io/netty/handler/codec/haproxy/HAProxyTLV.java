package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class HAProxyTLV extends DefaultByteBufHolder {
   private final HAProxyTLV.Type type;
   private final byte typeByteValue;

   int totalNumBytes() {
      return 3 + this.contentNumBytes();
   }

   int contentNumBytes() {
      return this.content().readableBytes();
   }

   public HAProxyTLV(byte var1, ByteBuf var2) {
      this(HAProxyTLV.Type.typeForByteValue(var1), var1, var2);
   }

   public HAProxyTLV(HAProxyTLV.Type var1, ByteBuf var2) {
      this(var1, HAProxyTLV.Type.byteValueForType(var1), var2);
   }

   HAProxyTLV(HAProxyTLV.Type var1, byte var2, ByteBuf var3) {
      super(var3);
      this.type = ObjectUtil.checkNotNull(var1, "type");
      this.typeByteValue = var2;
   }

   public HAProxyTLV.Type type() {
      return this.type;
   }

   public byte typeByteValue() {
      return this.typeByteValue;
   }

   public HAProxyTLV copy() {
      return this.replace(this.content().copy());
   }

   public HAProxyTLV duplicate() {
      return this.replace(this.content().duplicate());
   }

   public HAProxyTLV retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public HAProxyTLV replace(ByteBuf var1) {
      return new HAProxyTLV(this.type, this.typeByteValue, var1);
   }

   public HAProxyTLV retain() {
      super.retain();
      return this;
   }

   public HAProxyTLV retain(int var1) {
      super.retain(var1);
      return this;
   }

   public HAProxyTLV touch() {
      super.touch();
      return this;
   }

   public HAProxyTLV touch(Object var1) {
      super.touch(var1);
      return this;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this)
         + "(type: "
         + this.type()
         + ", typeByteValue: "
         + this.typeByteValue()
         + ", content: "
         + this.contentToString()
         + ')';
   }

   public static enum Type {
      PP2_TYPE_ALPN,
      PP2_TYPE_AUTHORITY,
      PP2_TYPE_SSL,
      PP2_TYPE_SSL_VERSION,
      PP2_TYPE_SSL_CN,
      PP2_TYPE_NETNS,
      OTHER;

      public static HAProxyTLV.Type typeForByteValue(byte var0) {
         switch (var0) {
            case 1:
               return PP2_TYPE_ALPN;
            case 2:
               return PP2_TYPE_AUTHORITY;
            case 32:
               return PP2_TYPE_SSL;
            case 33:
               return PP2_TYPE_SSL_VERSION;
            case 34:
               return PP2_TYPE_SSL_CN;
            case 48:
               return PP2_TYPE_NETNS;
            default:
               return OTHER;
         }
      }

      public static byte byteValueForType(HAProxyTLV.Type var0) {
         switch (var0) {
            case PP2_TYPE_ALPN:
               return 1;
            case PP2_TYPE_AUTHORITY:
               return 2;
            case PP2_TYPE_SSL:
               return 32;
            case PP2_TYPE_SSL_VERSION:
               return 33;
            case PP2_TYPE_SSL_CN:
               return 34;
            case PP2_TYPE_NETNS:
               return 48;
            default:
               throw new IllegalArgumentException("unknown type: " + var0);
         }
      }
   }
}
