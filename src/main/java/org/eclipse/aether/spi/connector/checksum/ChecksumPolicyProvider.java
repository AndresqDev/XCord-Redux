package org.eclipse.aether.spi.connector.checksum;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.transfer.TransferResource;

public interface ChecksumPolicyProvider {
   ChecksumPolicy newChecksumPolicy(RepositorySystemSession var1, RemoteRepository var2, TransferResource var3, String var4);

   String getEffectiveChecksumPolicy(RepositorySystemSession var1, String var2, String var3);
}
