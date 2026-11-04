package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;

public class DefaultDnsRecordDecoder implements DnsRecordDecoder {
   static final String ROOT = ".";

   protected DefaultDnsRecordDecoder() {
   }

   @Override
   public final DnsQuestion decodeQuestion(ByteBuf var1) throws Exception {
      String var2 = decodeName(var1);
      DnsRecordType var3 = DnsRecordType.valueOf(var1.readUnsignedShort());
      int var4 = var1.readUnsignedShort();
      return new DefaultDnsQuestion(var2, var3, var4);
   }

   @Override
   public final <T extends DnsRecord> T decodeRecord(ByteBuf var1) throws Exception {
      int var2 = var1.readerIndex();
      String var3 = decodeName(var1);
      int var4 = var1.writerIndex();
      if (var4 - var1.readerIndex() < 10) {
         var1.readerIndex(var2);
         return null;
      } else {
         DnsRecordType var5 = DnsRecordType.valueOf(var1.readUnsignedShort());
         int var6 = var1.readUnsignedShort();
         long var7 = var1.readUnsignedInt();
         int var9 = var1.readUnsignedShort();
         int var10 = var1.readerIndex();
         if (var4 - var10 < var9) {
            var1.readerIndex(var2);
            return null;
         } else {
            DnsRecord var11 = this.decodeRecord(var3, var5, var6, var7, var1, var10, var9);
            var1.readerIndex(var10 + var9);
            return (T)var11;
         }
      }
   }

   protected DnsRecord decodeRecord(String var1, DnsRecordType var2, int var3, long var4, ByteBuf var6, int var7, int var8) throws Exception {
      if (var2 == DnsRecordType.PTR) {
         return new DefaultDnsPtrRecord(var1, var3, var4, this.decodeName0(var6.duplicate().setIndex(var7, var7 + var8)));
      } else {
         return var2 != DnsRecordType.CNAME && var2 != DnsRecordType.NS
            ? new DefaultDnsRawRecord(var1, var2, var3, var4, var6.retainedDuplicate().setIndex(var7, var7 + var8))
            : new DefaultDnsRawRecord(var1, var2, var3, var4, DnsCodecUtil.decompressDomainName(var6.duplicate().setIndex(var7, var7 + var8)));
      }
   }

   protected String decodeName0(ByteBuf var1) {
      return decodeName(var1);
   }

   public static String decodeName(ByteBuf var0) {
      return DnsCodecUtil.decodeDomainName(var0);
   }
}
