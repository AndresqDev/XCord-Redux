package io.netty.handler.codec.dns;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import java.net.IDN;

public abstract class AbstractDnsRecord implements DnsRecord {
   private final String name;
   private final DnsRecordType type;
   private final short dnsClass;
   private final long timeToLive;
   private int hashCode;

   protected AbstractDnsRecord(String var1, DnsRecordType var2, long var3) {
      this(var1, var2, 1, var3);
   }

   protected AbstractDnsRecord(String var1, DnsRecordType var2, int var3, long var4) {
      ObjectUtil.checkPositiveOrZero(var4, "timeToLive");
      this.name = appendTrailingDot(IDNtoASCII(var1));
      this.type = ObjectUtil.checkNotNull(var2, "type");
      this.dnsClass = (short)var3;
      this.timeToLive = var4;
   }

   private static String IDNtoASCII(String var0) {
      ObjectUtil.checkNotNull(var0, "name");
      return PlatformDependent.isAndroid() && ".".equals(var0) ? var0 : IDN.toASCII(var0);
   }

   private static String appendTrailingDot(String var0) {
      return var0.length() > 0 && var0.charAt(var0.length() - 1) != '.' ? var0 + '.' : var0;
   }

   @Override
   public String name() {
      return this.name;
   }

   @Override
   public DnsRecordType type() {
      return this.type;
   }

   @Override
   public int dnsClass() {
      return this.dnsClass & 65535;
   }

   @Override
   public long timeToLive() {
      return this.timeToLive;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof DnsRecord)) {
         return false;
      } else {
         DnsRecord var2 = (DnsRecord)var1;
         int var3 = this.hashCode;
         return var3 != 0 && var3 != var2.hashCode()
            ? false
            : this.type().intValue() == var2.type().intValue() && this.dnsClass() == var2.dnsClass() && this.name().equals(var2.name());
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.hashCode;
      return var1 != 0 ? var1 : (this.hashCode = this.name.hashCode() * 31 + this.type().intValue() * 31 + this.dnsClass());
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder(64);
      var1.append(StringUtil.simpleClassName(this)).append('(').append(this.name()).append(' ').append(this.timeToLive()).append(' ');
      DnsMessageUtil.appendRecordClass(var1, this.dnsClass()).append(' ').append(this.type().name()).append(')');
      return var1.toString();
   }
}
