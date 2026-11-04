package io.netty.handler.ssl;

import io.netty.util.internal.SuppressJava6Requirement;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import javax.net.ssl.SSLEngine;

@SuppressJava6Requirement(
   reason = "Usage guarded by java version check"
)
final class BouncyCastleAlpnSslEngine extends JdkAlpnSslEngine {
   BouncyCastleAlpnSslEngine(SSLEngine var1, JdkApplicationProtocolNegotiator var2, boolean var3) {
      super(var1, var2, var3, new BiConsumer<SSLEngine, JdkAlpnSslEngine.AlpnSelector>() {
         public void accept(SSLEngine var1, JdkAlpnSslEngine.AlpnSelector var2) {
            BouncyCastleAlpnSslUtils.setHandshakeApplicationProtocolSelector(var1, var2);
         }
      }, new BiConsumer<SSLEngine, List<String>>() {
         public void accept(SSLEngine var1, List<String> var2) {
            BouncyCastleAlpnSslUtils.setApplicationProtocols(var1, var2);
         }
      });
   }

   @Override
   public String getApplicationProtocol() {
      return BouncyCastleAlpnSslUtils.getApplicationProtocol(this.getWrappedEngine());
   }

   @Override
   public String getHandshakeApplicationProtocol() {
      return BouncyCastleAlpnSslUtils.getHandshakeApplicationProtocol(this.getWrappedEngine());
   }

   @Override
   public void setHandshakeApplicationProtocolSelector(BiFunction<SSLEngine, List<String>, String> var1) {
      BouncyCastleAlpnSslUtils.setHandshakeApplicationProtocolSelector(this.getWrappedEngine(), var1);
   }

   @Override
   public BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector() {
      return BouncyCastleAlpnSslUtils.getHandshakeApplicationProtocolSelector(this.getWrappedEngine());
   }
}
