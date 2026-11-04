package org.apache.logging.log4j.core.appender;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TlsSyslogFrame {
   private final String message;
   private final int byteLength;

   public TlsSyslogFrame(String var1) {
      this.message = var1;
      byte[] var2 = var1.getBytes(StandardCharsets.UTF_8);
      this.byteLength = var2.length;
   }

   public String getMessage() {
      return this.message;
   }

   @Override
   public String toString() {
      return Integer.toString(this.byteLength) + ' ' + this.message;
   }

   @Override
   public int hashCode() {
      return 31 + Objects.hashCode(this.message);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof TlsSyslogFrame)) {
         return false;
      } else {
         TlsSyslogFrame var2 = (TlsSyslogFrame)var1;
         return Objects.equals(this.message, var2.message);
      }
   }
}
