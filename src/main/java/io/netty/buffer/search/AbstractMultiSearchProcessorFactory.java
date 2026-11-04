package io.netty.buffer.search;

public abstract class AbstractMultiSearchProcessorFactory implements MultiSearchProcessorFactory {
   public static AhoCorasicSearchProcessorFactory newAhoCorasicSearchProcessorFactory(byte[]... var0) {
      return new AhoCorasicSearchProcessorFactory(var0);
   }
}
