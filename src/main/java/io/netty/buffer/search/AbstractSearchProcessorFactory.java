package io.netty.buffer.search;

public abstract class AbstractSearchProcessorFactory implements SearchProcessorFactory {
   public static KmpSearchProcessorFactory newKmpSearchProcessorFactory(byte[] var0) {
      return new KmpSearchProcessorFactory(var0);
   }

   public static BitapSearchProcessorFactory newBitapSearchProcessorFactory(byte[] var0) {
      return new BitapSearchProcessorFactory(var0);
   }
}
