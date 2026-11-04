package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.StringUtil;
import java.util.Collections;
import java.util.List;

public final class HAProxySSLTLV extends HAProxyTLV {
   private final int verify;
   private final List<HAProxyTLV> tlvs;
   private final byte clientBitField;

   public HAProxySSLTLV(int var1, byte var2, List<HAProxyTLV> var3) {
      this(var1, var2, var3, Unpooled.EMPTY_BUFFER);
   }

   HAProxySSLTLV(int var1, byte var2, List<HAProxyTLV> var3, ByteBuf var4) {
      super(HAProxyTLV.Type.PP2_TYPE_SSL, (byte)32, var4);
      this.verify = var1;
      this.tlvs = Collections.unmodifiableList(var3);
      this.clientBitField = var2;
   }

   public boolean isPP2ClientCertConn() {
      return (this.clientBitField & 2) != 0;
   }

   public boolean isPP2ClientSSL() {
      return (this.clientBitField & 1) != 0;
   }

   public boolean isPP2ClientCertSess() {
      return (this.clientBitField & 4) != 0;
   }

   public byte client() {
      return this.clientBitField;
   }

   public int verify() {
      return this.verify;
   }

   public List<HAProxyTLV> encapsulatedTLVs() {
      return this.tlvs;
   }

   @Override
   int contentNumBytes() {
      int var1 = 0;

      for (int var2 = 0; var2 < this.tlvs.size(); var2++) {
         var1 += this.tlvs.get(var2).totalNumBytes();
      }

      return 5 + var1;
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(this)
         + "(type: "
         + this.type()
         + ", typeByteValue: "
         + this.typeByteValue()
         + ", client: "
         + this.client()
         + ", verify: "
         + this.verify()
         + ", numEncapsulatedTlvs: "
         + this.tlvs.size()
         + ')';
   }
}
