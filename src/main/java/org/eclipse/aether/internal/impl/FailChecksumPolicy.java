package org.eclipse.aether.internal.impl;

import org.eclipse.aether.transfer.ChecksumFailureException;
import org.eclipse.aether.transfer.TransferResource;

final class FailChecksumPolicy extends AbstractChecksumPolicy {
   FailChecksumPolicy(TransferResource var1) {
      super(var1);
   }

   @Override
   public boolean onTransferChecksumFailure(ChecksumFailureException var1) {
      return false;
   }
}
