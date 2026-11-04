package io.netty.handler.codec.dns;

import io.netty.util.internal.StringUtil;

public abstract class AbstractDnsOptPseudoRrRecord extends AbstractDnsRecord implements DnsOptPseudoRecord {
   protected AbstractDnsOptPseudoRrRecord(int var1, int var2, int var3) {
      super("", DnsRecordType.OPT, var1, packIntoLong(var2, var3));
   }

   protected AbstractDnsOptPseudoRrRecord(int var1) {
      super("", DnsRecordType.OPT, var1, 0L);
   }

   private static long packIntoLong(int var0, int var1) {
      return (((long)var0 & 255L) << 24 | (long)((var1 & 0xFF) << 16)) & 4294967295L;
   }

   @Override
   public int extendedRcode() {
      return (short)((int)this.timeToLive() >> 24 & 0xFF);
   }

   @Override
   public int version() {
      return (short)((int)this.timeToLive() >> 16 & 0xFF);
   }

   @Override
   public int flags() {
      return (short)((short)((int)this.timeToLive()) & 0xFF);
   }

   @Override
   public String toString() {
      return this.toStringBuilder().toString();
   }

   final StringBuilder toStringBuilder() {
      return new StringBuilder(64)
         .append(StringUtil.simpleClassName(this))
         .append('(')
         .append("OPT flags:")
         .append(this.flags())
         .append(" version:")
         .append(this.version())
         .append(" extendedRecode:")
         .append(this.extendedRcode())
         .append(" udp:")
         .append(this.dnsClass())
         .append(')');
   }
}
