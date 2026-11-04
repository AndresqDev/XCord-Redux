package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;

class JdkBaseApplicationProtocolNegotiator implements JdkApplicationProtocolNegotiator {
   private final List<String> protocols;
   private final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory selectorFactory;
   private final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory listenerFactory;
   private final JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory;
   static final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory FAIL_SELECTOR_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectorFactory() {
      @Override
      public JdkApplicationProtocolNegotiator.ProtocolSelector newSelector(SSLEngine var1, Set<String> var2) {
         return new JdkBaseApplicationProtocolNegotiator.FailProtocolSelector((JdkSslEngine)var1, var2);
      }
   };
   static final JdkApplicationProtocolNegotiator.ProtocolSelectorFactory NO_FAIL_SELECTOR_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectorFactory() {
      @Override
      public JdkApplicationProtocolNegotiator.ProtocolSelector newSelector(SSLEngine var1, Set<String> var2) {
         return new JdkBaseApplicationProtocolNegotiator.NoFailProtocolSelector((JdkSslEngine)var1, var2);
      }
   };
   static final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory FAIL_SELECTION_LISTENER_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory() {
      @Override
      public JdkApplicationProtocolNegotiator.ProtocolSelectionListener newListener(SSLEngine var1, List<String> var2) {
         return new JdkBaseApplicationProtocolNegotiator.FailProtocolSelectionListener((JdkSslEngine)var1, var2);
      }
   };
   static final JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory NO_FAIL_SELECTION_LISTENER_FACTORY = new JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory() {
      @Override
      public JdkApplicationProtocolNegotiator.ProtocolSelectionListener newListener(SSLEngine var1, List<String> var2) {
         return new JdkBaseApplicationProtocolNegotiator.NoFailProtocolSelectionListener((JdkSslEngine)var1, var2);
      }
   };

   JdkBaseApplicationProtocolNegotiator(
      JdkApplicationProtocolNegotiator.SslEngineWrapperFactory var1,
      JdkApplicationProtocolNegotiator.ProtocolSelectorFactory var2,
      JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory var3,
      Iterable<String> var4
   ) {
      this(var1, var2, var3, ApplicationProtocolUtil.toList(var4));
   }

   JdkBaseApplicationProtocolNegotiator(
      JdkApplicationProtocolNegotiator.SslEngineWrapperFactory var1,
      JdkApplicationProtocolNegotiator.ProtocolSelectorFactory var2,
      JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory var3,
      String... var4
   ) {
      this(var1, var2, var3, ApplicationProtocolUtil.toList(var4));
   }

   private JdkBaseApplicationProtocolNegotiator(
      JdkApplicationProtocolNegotiator.SslEngineWrapperFactory var1,
      JdkApplicationProtocolNegotiator.ProtocolSelectorFactory var2,
      JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory var3,
      List<String> var4
   ) {
      this.wrapperFactory = ObjectUtil.checkNotNull(var1, "wrapperFactory");
      this.selectorFactory = ObjectUtil.checkNotNull(var2, "selectorFactory");
      this.listenerFactory = ObjectUtil.checkNotNull(var3, "listenerFactory");
      this.protocols = Collections.unmodifiableList(ObjectUtil.checkNotNull(var4, "protocols"));
   }

   @Override
   public List<String> protocols() {
      return this.protocols;
   }

   @Override
   public JdkApplicationProtocolNegotiator.ProtocolSelectorFactory protocolSelectorFactory() {
      return this.selectorFactory;
   }

   @Override
   public JdkApplicationProtocolNegotiator.ProtocolSelectionListenerFactory protocolListenerFactory() {
      return this.listenerFactory;
   }

   @Override
   public JdkApplicationProtocolNegotiator.SslEngineWrapperFactory wrapperFactory() {
      return this.wrapperFactory;
   }

   private static final class FailProtocolSelectionListener extends JdkBaseApplicationProtocolNegotiator.NoFailProtocolSelectionListener {
      FailProtocolSelectionListener(JdkSslEngine var1, List<String> var2) {
         super(var1, var2);
      }

      @Override
      protected void noSelectedMatchFound(String var1) throws Exception {
         throw new SSLHandshakeException("No compatible protocols found");
      }
   }

   private static final class FailProtocolSelector extends JdkBaseApplicationProtocolNegotiator.NoFailProtocolSelector {
      FailProtocolSelector(JdkSslEngine var1, Set<String> var2) {
         super(var1, var2);
      }

      @Override
      public String noSelectMatchFound() throws Exception {
         throw new SSLHandshakeException("Selected protocol is not supported");
      }
   }

   private static class NoFailProtocolSelectionListener implements JdkApplicationProtocolNegotiator.ProtocolSelectionListener {
      private final JdkSslEngine engineWrapper;
      private final List<String> supportedProtocols;

      NoFailProtocolSelectionListener(JdkSslEngine var1, List<String> var2) {
         this.engineWrapper = var1;
         this.supportedProtocols = var2;
      }

      @Override
      public void unsupported() {
         this.engineWrapper.setNegotiatedApplicationProtocol(null);
      }

      @Override
      public void selected(String var1) throws Exception {
         if (this.supportedProtocols.contains(var1)) {
            this.engineWrapper.setNegotiatedApplicationProtocol(var1);
         } else {
            this.noSelectedMatchFound(var1);
         }
      }

      protected void noSelectedMatchFound(String var1) throws Exception {
      }
   }

   static class NoFailProtocolSelector implements JdkApplicationProtocolNegotiator.ProtocolSelector {
      private final JdkSslEngine engineWrapper;
      private final Set<String> supportedProtocols;

      NoFailProtocolSelector(JdkSslEngine var1, Set<String> var2) {
         this.engineWrapper = var1;
         this.supportedProtocols = var2;
      }

      @Override
      public void unsupported() {
         this.engineWrapper.setNegotiatedApplicationProtocol(null);
      }

      @Override
      public String select(List<String> var1) throws Exception {
         for (String var3 : this.supportedProtocols) {
            if (var1.contains(var3)) {
               this.engineWrapper.setNegotiatedApplicationProtocol(var3);
               return var3;
            }
         }

         return this.noSelectMatchFound();
      }

      public String noSelectMatchFound() throws Exception {
         this.engineWrapper.setNegotiatedApplicationProtocol(null);
         return null;
      }
   }
}
