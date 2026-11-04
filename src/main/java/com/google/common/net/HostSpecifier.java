package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import java.net.InetAddress;
import java.text.ParseException;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class HostSpecifier {
   private final String canonicalForm;

   private HostSpecifier(String var1) {
      this.canonicalForm = var1;
   }

   public static HostSpecifier fromValid(String var0) {
      HostAndPort var1 = HostAndPort.fromString(var0);
      Preconditions.checkArgument(!var1.hasPort());
      String var2 = var1.getHost();
      InetAddress var3 = null;

      try {
         var3 = InetAddresses.forString(var2);
      } catch (IllegalArgumentException var5) {
      }

      if (var3 != null) {
         return new HostSpecifier(InetAddresses.toUriString(var3));
      } else {
         InternetDomainName var4 = InternetDomainName.from(var2);
         if (var4.hasPublicSuffix()) {
            return new HostSpecifier(var4.toString());
         } else {
            String var10003 = String.valueOf(var2);
            throw new IllegalArgumentException(
               var10003.length() != 0
                  ? "Domain name does not have a recognized public suffix: ".concat(var10003)
                  : new String("Domain name does not have a recognized public suffix: ")
            );
         }
      }
   }

   public static HostSpecifier from(String var0) throws ParseException {
      try {
         return fromValid(var0);
      } catch (IllegalArgumentException var3) {
         String var10003 = String.valueOf(var0);
         ParseException var2 = new ParseException(
            var10003.length() != 0 ? "Invalid host specifier: ".concat(var10003) : new String("Invalid host specifier: "), 0
         );
         var2.initCause(var3);
         throw var2;
      }
   }

   public static boolean isValid(String var0) {
      try {
         fromValid(var0);
         return true;
      } catch (IllegalArgumentException var2) {
         return false;
      }
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 instanceof HostSpecifier) {
         HostSpecifier var2 = (HostSpecifier)var1;
         return this.canonicalForm.equals(var2.canonicalForm);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.canonicalForm.hashCode();
   }

   @Override
   public String toString() {
      return this.canonicalForm;
   }
}
