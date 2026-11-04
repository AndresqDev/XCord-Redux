package io.netty.handler.ssl;

import io.netty.util.internal.SuppressJava6Requirement;
import java.nio.ByteBuffer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;

@SuppressJava6Requirement(
   reason = "Usage guarded by java version check"
)
class JdkAlpnSslEngine extends JdkSslEngine {
   private final JdkApplicationProtocolNegotiator.ProtocolSelectionListener selectionListener;
   private final JdkAlpnSslEngine.AlpnSelector alpnSelector;

   JdkAlpnSslEngine(
      SSLEngine var1,
      JdkApplicationProtocolNegotiator var2,
      boolean var3,
      BiConsumer<SSLEngine, JdkAlpnSslEngine.AlpnSelector> var4,
      BiConsumer<SSLEngine, List<String>> var5
   ) {
      super(var1);
      if (var3) {
         this.selectionListener = null;
         this.alpnSelector = new JdkAlpnSslEngine.AlpnSelector(var2.protocolSelectorFactory().newSelector(this, new LinkedHashSet<>(var2.protocols())));
         var4.accept(var1, this.alpnSelector);
      } else {
         this.selectionListener = var2.protocolListenerFactory().newListener(this, var2.protocols());
         this.alpnSelector = null;
         var5.accept(var1, var2.protocols());
      }
   }

   JdkAlpnSslEngine(SSLEngine var1, JdkApplicationProtocolNegotiator var2, boolean var3) {
      this(var1, var2, var3, new BiConsumer<SSLEngine, JdkAlpnSslEngine.AlpnSelector>() {
         public void accept(SSLEngine var1, JdkAlpnSslEngine.AlpnSelector var2) {
            JdkAlpnSslUtils.setHandshakeApplicationProtocolSelector(var1, var2);
         }
      }, new BiConsumer<SSLEngine, List<String>>() {
         public void accept(SSLEngine var1, List<String> var2) {
            JdkAlpnSslUtils.setApplicationProtocols(var1, var2);
         }
      });
   }

   private SSLEngineResult verifyProtocolSelection(SSLEngineResult var1) throws SSLException {
      if (var1.getHandshakeStatus() != HandshakeStatus.FINISHED) {
         return var1;
      } else if (this.alpnSelector == null) {
         try {
            String var2 = this.getApplicationProtocol();

            assert var2 != null;

            if (var2.isEmpty()) {
               this.selectionListener.unsupported();
            } else {
               this.selectionListener.selected(var2);
            }

            return var1;
         } catch (Throwable var3) {
            throw SslUtils.toSSLHandshakeException(var3);
         }
      } else {
         assert this.selectionListener == null;

         this.alpnSelector.checkUnsupported();
         return var1;
      }
   }

   @Override
   public SSLEngineResult wrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      return this.verifyProtocolSelection(super.wrap(var1, var2));
   }

   @Override
   public SSLEngineResult wrap(ByteBuffer[] var1, ByteBuffer var2) throws SSLException {
      return this.verifyProtocolSelection(super.wrap(var1, var2));
   }

   @Override
   public SSLEngineResult wrap(ByteBuffer[] var1, int var2, int var3, ByteBuffer var4) throws SSLException {
      return this.verifyProtocolSelection(super.wrap(var1, var2, var3, var4));
   }

   @Override
   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      return this.verifyProtocolSelection(super.unwrap(var1, var2));
   }

   @Override
   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2) throws SSLException {
      return this.verifyProtocolSelection(super.unwrap(var1, var2));
   }

   @Override
   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2, int var3, int var4) throws SSLException {
      return this.verifyProtocolSelection(super.unwrap(var1, var2, var3, var4));
   }

   @Override
   void setNegotiatedApplicationProtocol(String var1) {
   }

   @Override
   public String getNegotiatedApplicationProtocol() {
      String var1 = this.getApplicationProtocol();
      if (var1 != null) {
         return var1.isEmpty() ? null : var1;
      } else {
         return null;
      }
   }

   @Override
   public String getApplicationProtocol() {
      return JdkAlpnSslUtils.getApplicationProtocol(this.getWrappedEngine());
   }

   @Override
   public String getHandshakeApplicationProtocol() {
      return JdkAlpnSslUtils.getHandshakeApplicationProtocol(this.getWrappedEngine());
   }

   @Override
   public void setHandshakeApplicationProtocolSelector(BiFunction<SSLEngine, List<String>, String> var1) {
      JdkAlpnSslUtils.setHandshakeApplicationProtocolSelector(this.getWrappedEngine(), var1);
   }

   @Override
   public BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector() {
      return JdkAlpnSslUtils.getHandshakeApplicationProtocolSelector(this.getWrappedEngine());
   }

   final class AlpnSelector implements BiFunction<SSLEngine, List<String>, String> {
      private final JdkApplicationProtocolNegotiator.ProtocolSelector selector;
      private boolean called;

      AlpnSelector(JdkApplicationProtocolNegotiator.ProtocolSelector var2) {
         this.selector = var2;
      }

      public String apply(SSLEngine var1, List<String> var2) {
         assert !this.called;

         this.called = true;

         try {
            String var3 = this.selector.select(var2);
            return var3 == null ? "" : var3;
         } catch (Exception var4) {
            return null;
         }
      }

      void checkUnsupported() {
         if (!this.called) {
            String var1 = JdkAlpnSslEngine.this.getApplicationProtocol();

            assert var1 != null;

            if (var1.isEmpty()) {
               this.selector.unsupported();
            }
         }
      }
   }
}
