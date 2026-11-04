package org.eclipse.aether.internal.impl;

import org.eclipse.aether.transfer.ChecksumFailureException;
import org.eclipse.aether.transfer.TransferResource;

final class WarnChecksumPolicy extends AbstractChecksumPolicy {
   WarnChecksumPolicy(TransferResource var1) {
      super(var1);
   }

   @Override
   public boolean onTransferChecksumFailure(ChecksumFailureException var1) {
      this.logger.warn("Could not validate integrity of download from {}{}", this.resource.getRepositoryUrl(), this.resource.getResourceName(), var1);
      return true;
   }
}
