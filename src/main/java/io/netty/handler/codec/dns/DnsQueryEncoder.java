package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;

final class DnsQueryEncoder {
   private final DnsRecordEncoder recordEncoder;

   DnsQueryEncoder(DnsRecordEncoder var1) {
      this.recordEncoder = ObjectUtil.checkNotNull(var1, "recordEncoder");
   }

   void encode(DnsQuery var1, ByteBuf var2) throws Exception {
      encodeHeader(var1, var2);
      this.encodeQuestions(var1, var2);
      this.encodeRecords(var1, DnsSection.ADDITIONAL, var2);
   }

   private static void encodeHeader(DnsQuery var0, ByteBuf var1) {
      var1.writeShort(var0.id());
      int var2 = 0;
      var2 |= (var0.opCode().byteValue() & 255) << 14;
      if (var0.isRecursionDesired()) {
         var2 |= 256;
      }

      var1.writeShort(var2);
      var1.writeShort(var0.count(DnsSection.QUESTION));
      var1.writeShort(0);
      var1.writeShort(0);
      var1.writeShort(var0.count(DnsSection.ADDITIONAL));
   }

   private void encodeQuestions(DnsQuery var1, ByteBuf var2) throws Exception {
      int var3 = var1.count(DnsSection.QUESTION);

      for (int var4 = 0; var4 < var3; var4++) {
         this.recordEncoder.encodeQuestion(var1.recordAt(DnsSection.QUESTION, var4), var2);
      }
   }

   private void encodeRecords(DnsQuery var1, DnsSection var2, ByteBuf var3) throws Exception {
      int var4 = var1.count(var2);

      for (int var5 = 0; var5 < var4; var5++) {
         this.recordEncoder.encodeRecord(var1.recordAt(var2, var5), var3);
      }
   }
}
