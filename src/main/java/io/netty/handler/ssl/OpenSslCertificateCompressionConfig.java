package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class OpenSslCertificateCompressionConfig implements Iterable<OpenSslCertificateCompressionConfig.AlgorithmConfig> {
   private final List<OpenSslCertificateCompressionConfig.AlgorithmConfig> pairList;

   private OpenSslCertificateCompressionConfig(OpenSslCertificateCompressionConfig.AlgorithmConfig... var1) {
      this.pairList = Collections.unmodifiableList(Arrays.asList(var1));
   }

   @Override
   public Iterator<OpenSslCertificateCompressionConfig.AlgorithmConfig> iterator() {
      return this.pairList.iterator();
   }

   public static OpenSslCertificateCompressionConfig.Builder newBuilder() {
      return new OpenSslCertificateCompressionConfig.Builder();
   }

   public static final class AlgorithmConfig {
      private final OpenSslCertificateCompressionAlgorithm algorithm;
      private final OpenSslCertificateCompressionConfig.AlgorithmMode mode;

      private AlgorithmConfig(OpenSslCertificateCompressionAlgorithm var1, OpenSslCertificateCompressionConfig.AlgorithmMode var2) {
         this.algorithm = ObjectUtil.checkNotNull(var1, "algorithm");
         this.mode = ObjectUtil.checkNotNull(var2, "mode");
      }

      public OpenSslCertificateCompressionConfig.AlgorithmMode mode() {
         return this.mode;
      }

      public OpenSslCertificateCompressionAlgorithm algorithm() {
         return this.algorithm;
      }
   }

   public static enum AlgorithmMode {
      Compress,
      Decompress,
      Both;
   }

   public static final class Builder {
      private final List<OpenSslCertificateCompressionConfig.AlgorithmConfig> algorithmList = new ArrayList<>();

      private Builder() {
      }

      public OpenSslCertificateCompressionConfig.Builder addAlgorithm(
         OpenSslCertificateCompressionAlgorithm var1, OpenSslCertificateCompressionConfig.AlgorithmMode var2
      ) {
         this.algorithmList.add(new OpenSslCertificateCompressionConfig.AlgorithmConfig(var1, var2));
         return this;
      }

      public OpenSslCertificateCompressionConfig build() {
         return new OpenSslCertificateCompressionConfig(this.algorithmList.toArray(new OpenSslCertificateCompressionConfig.AlgorithmConfig[0]));
      }
   }
}
