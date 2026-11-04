package org.eclipse.aether.spi.connector.checksum;

import org.eclipse.aether.transfer.ChecksumFailureException;

public interface ChecksumPolicy {
   int KIND_UNOFFICIAL = 1;

   boolean onChecksumMatch(String var1, int var2);

   void onChecksumMismatch(String var1, int var2, ChecksumFailureException var3) throws ChecksumFailureException;

   void onChecksumError(String var1, int var2, ChecksumFailureException var3) throws ChecksumFailureException;

   void onNoMoreChecksums() throws ChecksumFailureException;

   void onTransferRetry();

   boolean onTransferChecksumFailure(ChecksumFailureException var1);
}
