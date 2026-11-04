package org.eclipse.aether.internal.impl;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicy;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicyProvider;
import org.eclipse.aether.transfer.TransferResource;
import xcord.Named;

@Named
public final class DefaultChecksumPolicyProvider implements ChecksumPolicyProvider {
   private static final int ORDINAL_IGNORE = 0;
   private static final int ORDINAL_WARN = 1;
   private static final int ORDINAL_FAIL = 2;

   @Override
   public ChecksumPolicy newChecksumPolicy(RepositorySystemSession var1, RemoteRepository var2, TransferResource var3, String var4) {
      if ("ignore".equals(var4)) {
         return null;
      } else {
         return (ChecksumPolicy)("fail".equals(var4) ? new FailChecksumPolicy(var3) : new WarnChecksumPolicy(var3));
      }
   }

   @Override
   public String getEffectiveChecksumPolicy(RepositorySystemSession var1, String var2, String var3) {
      if (var2 != null && var2.equals(var3)) {
         return var2;
      } else {
         int var4 = ordinalOfPolicy(var2);
         int var5 = ordinalOfPolicy(var3);
         if (var5 < var4) {
            return var5 != 1 ? var3 : "warn";
         } else {
            return var4 != 1 ? var2 : "warn";
         }
      }
   }

   private static int ordinalOfPolicy(String var0) {
      if ("fail".equals(var0)) {
         return 2;
      } else {
         return "ignore".equals(var0) ? 0 : 1;
      }
   }
}
