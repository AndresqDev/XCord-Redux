package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;

abstract class DnsResponseDecoder<A extends SocketAddress> {
   private final DnsRecordDecoder recordDecoder;

   DnsResponseDecoder(DnsRecordDecoder var1) {
      this.recordDecoder = ObjectUtil.checkNotNull(var1, "recordDecoder");
   }

   final DnsResponse decode(A var1, A var2, ByteBuf var3) throws Exception {
      int var4 = var3.readUnsignedShort();
      int var5 = var3.readUnsignedShort();
      if (var5 >> 15 == 0) {
         throw new CorruptedFrameException("not a response");
      } else {
         DnsResponse var6 = this.newResponse((A)var1, (A)var2, var4, DnsOpCode.valueOf((byte)(var5 >> 11 & 15)), DnsResponseCode.valueOf((byte)(var5 & 15)));
         var6.setRecursionDesired((var5 >> 8 & 1) == 1);
         var6.setAuthoritativeAnswer((var5 >> 10 & 1) == 1);
         var6.setTruncated((var5 >> 9 & 1) == 1);
         var6.setRecursionAvailable((var5 >> 7 & 1) == 1);
         var6.setZ(var5 >> 4 & 7);
         boolean var7 = false;

         DnsResponse var12;
         try {
            int var8 = var3.readUnsignedShort();
            int var9 = var3.readUnsignedShort();
            int var10 = var3.readUnsignedShort();
            int var11 = var3.readUnsignedShort();
            this.decodeQuestions(var6, var3, var8);
            if (!this.decodeRecords(var6, DnsSection.ANSWER, var3, var9)) {
               var7 = true;
               return var6;
            }

            if (this.decodeRecords(var6, DnsSection.AUTHORITY, var3, var10)) {
               this.decodeRecords(var6, DnsSection.ADDITIONAL, var3, var11);
               var7 = true;
               return var6;
            }

            var7 = true;
            var12 = var6;
         } finally {
            if (!var7) {
               var6.release();
            }
         }

         return var12;
      }
   }

   protected abstract DnsResponse newResponse(A var1, A var2, int var3, DnsOpCode var4, DnsResponseCode var5) throws Exception;

   private void decodeQuestions(DnsResponse var1, ByteBuf var2, int var3) throws Exception {
      for (int var4 = var3; var4 > 0; var4--) {
         var1.addRecord(DnsSection.QUESTION, this.recordDecoder.decodeQuestion(var2));
      }
   }

   private boolean decodeRecords(DnsResponse var1, DnsSection var2, ByteBuf var3, int var4) throws Exception {
      for (int var5 = var4; var5 > 0; var5--) {
         DnsRecord var6 = this.recordDecoder.decodeRecord(var3);
         if (var6 == null) {
            return false;
         }

         var1.addRecord(var2, var6);
      }

      return true;
   }
}
