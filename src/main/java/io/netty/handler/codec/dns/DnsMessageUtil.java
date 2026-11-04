package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.internal.StringUtil;
import java.net.SocketAddress;

final class DnsMessageUtil {
   static StringBuilder appendQuery(StringBuilder var0, DnsQuery var1) {
      appendQueryHeader(var0, var1);
      appendAllRecords(var0, var1);
      return var0;
   }

   static StringBuilder appendResponse(StringBuilder var0, DnsResponse var1) {
      appendResponseHeader(var0, var1);
      appendAllRecords(var0, var1);
      return var0;
   }

   static StringBuilder appendRecordClass(StringBuilder var0, int var1) {
      String var2;
      switch (var1 = var1 & 65535) {
         case 1:
            var2 = "IN";
            break;
         case 2:
            var2 = "CSNET";
            break;
         case 3:
            var2 = "CHAOS";
            break;
         case 4:
            var2 = "HESIOD";
            break;
         case 254:
            var2 = "NONE";
            break;
         case 255:
            var2 = "ANY";
            break;
         default:
            var2 = null;
      }

      if (var2 != null) {
         var0.append(var2);
      } else {
         var0.append("UNKNOWN(").append(var1).append(')');
      }

      return var0;
   }

   private static void appendQueryHeader(StringBuilder var0, DnsQuery var1) {
      var0.append(StringUtil.simpleClassName(var1)).append('(');
      appendAddresses(var0, var1).append(var1.id()).append(", ").append(var1.opCode());
      if (var1.isRecursionDesired()) {
         var0.append(", RD");
      }

      if (var1.z() != 0) {
         var0.append(", Z: ").append(var1.z());
      }

      var0.append(')');
   }

   private static void appendResponseHeader(StringBuilder var0, DnsResponse var1) {
      var0.append(StringUtil.simpleClassName(var1)).append('(');
      appendAddresses(var0, var1).append(var1.id()).append(", ").append(var1.opCode()).append(", ").append(var1.code()).append(',');
      boolean var2 = true;
      if (var1.isRecursionDesired()) {
         var2 = false;
         var0.append(" RD");
      }

      if (var1.isAuthoritativeAnswer()) {
         var2 = false;
         var0.append(" AA");
      }

      if (var1.isTruncated()) {
         var2 = false;
         var0.append(" TC");
      }

      if (var1.isRecursionAvailable()) {
         var2 = false;
         var0.append(" RA");
      }

      if (var1.z() != 0) {
         if (!var2) {
            var0.append(',');
         }

         var0.append(" Z: ").append(var1.z());
      }

      if (var2) {
         var0.setCharAt(var0.length() - 1, ')');
      } else {
         var0.append(')');
      }
   }

   private static StringBuilder appendAddresses(StringBuilder var0, DnsMessage var1) {
      if (!(var1 instanceof AddressedEnvelope)) {
         return var0;
      } else {
         AddressedEnvelope var2 = (AddressedEnvelope)var1;
         SocketAddress var3 = var2.sender();
         if (var3 != null) {
            var0.append("from: ").append(var3).append(", ");
         }

         var3 = var2.recipient();
         if (var3 != null) {
            var0.append("to: ").append(var3).append(", ");
         }

         return var0;
      }
   }

   private static void appendAllRecords(StringBuilder var0, DnsMessage var1) {
      appendRecords(var0, var1, DnsSection.QUESTION);
      appendRecords(var0, var1, DnsSection.ANSWER);
      appendRecords(var0, var1, DnsSection.AUTHORITY);
      appendRecords(var0, var1, DnsSection.ADDITIONAL);
   }

   private static void appendRecords(StringBuilder var0, DnsMessage var1, DnsSection var2) {
      int var3 = var1.count(var2);

      for (int var4 = 0; var4 < var3; var4++) {
         var0.append(StringUtil.NEWLINE).append('\t').append(var1.recordAt(var2, var4));
      }
   }

   static DnsQuery decodeDnsQuery(DnsRecordDecoder var0, ByteBuf var1, DnsMessageUtil.DnsQueryFactory var2) throws Exception {
      DnsQuery var3 = newQuery(var1, var2);
      boolean var4 = false;

      DnsQuery var9;
      try {
         int var5 = var1.readUnsignedShort();
         int var6 = var1.readUnsignedShort();
         int var7 = var1.readUnsignedShort();
         int var8 = var1.readUnsignedShort();
         decodeQuestions(var0, var3, var1, var5);
         decodeRecords(var0, var3, DnsSection.ANSWER, var1, var6);
         decodeRecords(var0, var3, DnsSection.AUTHORITY, var1, var7);
         decodeRecords(var0, var3, DnsSection.ADDITIONAL, var1, var8);
         var4 = true;
         var9 = var3;
      } finally {
         if (!var4) {
            var3.release();
         }
      }

      return var9;
   }

   private static DnsQuery newQuery(ByteBuf var0, DnsMessageUtil.DnsQueryFactory var1) {
      int var2 = var0.readUnsignedShort();
      int var3 = var0.readUnsignedShort();
      if (var3 >> 15 == 1) {
         throw new CorruptedFrameException("not a query");
      } else {
         DnsQuery var4 = var1.newQuery(var2, DnsOpCode.valueOf((byte)(var3 >> 11 & 15)));
         var4.setRecursionDesired((var3 >> 8 & 1) == 1);
         var4.setZ(var3 >> 4 & 7);
         return var4;
      }
   }

   private static void decodeQuestions(DnsRecordDecoder var0, DnsQuery var1, ByteBuf var2, int var3) throws Exception {
      for (int var4 = var3; var4 > 0; var4--) {
         var1.addRecord(DnsSection.QUESTION, var0.decodeQuestion(var2));
      }
   }

   private static void decodeRecords(DnsRecordDecoder var0, DnsQuery var1, DnsSection var2, ByteBuf var3, int var4) throws Exception {
      for (int var5 = var4; var5 > 0; var5--) {
         DnsRecord var6 = var0.decodeRecord(var3);
         if (var6 == null) {
            break;
         }

         var1.addRecord(var2, var6);
      }
   }

   static void encodeDnsResponse(DnsRecordEncoder var0, DnsResponse var1, ByteBuf var2) throws Exception {
      boolean var3 = false;

      try {
         encodeHeader(var1, var2);
         encodeQuestions(var0, var1, var2);
         encodeRecords(var0, var1, DnsSection.ANSWER, var2);
         encodeRecords(var0, var1, DnsSection.AUTHORITY, var2);
         encodeRecords(var0, var1, DnsSection.ADDITIONAL, var2);
         var3 = true;
      } finally {
         if (!var3) {
            var2.release();
         }
      }
   }

   private static void encodeHeader(DnsResponse var0, ByteBuf var1) {
      var1.writeShort(var0.id());
      int var2 = 32768;
      var2 |= (var0.opCode().byteValue() & 255) << 11;
      if (var0.isAuthoritativeAnswer()) {
         var2 |= 1024;
      }

      if (var0.isTruncated()) {
         var2 |= 512;
      }

      if (var0.isRecursionDesired()) {
         var2 |= 256;
      }

      if (var0.isRecursionAvailable()) {
         var2 |= 128;
      }

      var2 |= var0.z() << 4;
      var2 |= var0.code().intValue();
      var1.writeShort(var2);
      var1.writeShort(var0.count(DnsSection.QUESTION));
      var1.writeShort(var0.count(DnsSection.ANSWER));
      var1.writeShort(var0.count(DnsSection.AUTHORITY));
      var1.writeShort(var0.count(DnsSection.ADDITIONAL));
   }

   private static void encodeQuestions(DnsRecordEncoder var0, DnsResponse var1, ByteBuf var2) throws Exception {
      int var3 = var1.count(DnsSection.QUESTION);

      for (int var4 = 0; var4 < var3; var4++) {
         var0.encodeQuestion(var1.recordAt(DnsSection.QUESTION, var4), var2);
      }
   }

   private static void encodeRecords(DnsRecordEncoder var0, DnsResponse var1, DnsSection var2, ByteBuf var3) throws Exception {
      int var4 = var1.count(var2);

      for (int var5 = 0; var5 < var4; var5++) {
         var0.encodeRecord(var1.recordAt(var2, var5), var3);
      }
   }

   private DnsMessageUtil() {
   }

   interface DnsQueryFactory {
      DnsQuery newQuery(int var1, DnsOpCode var2);
   }
}
