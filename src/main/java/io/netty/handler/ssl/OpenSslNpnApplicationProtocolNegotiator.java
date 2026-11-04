package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.List;

@Deprecated
public final class OpenSslNpnApplicationProtocolNegotiator implements OpenSslApplicationProtocolNegotiator {
   private final List<String> protocols;

   public OpenSslNpnApplicationProtocolNegotiator(Iterable<String> var1) {
      this.protocols = ObjectUtil.checkNotNull(ApplicationProtocolUtil.toList(var1), "protocols");
   }

   public OpenSslNpnApplicationProtocolNegotiator(String... var1) {
      this.protocols = ObjectUtil.checkNotNull(ApplicationProtocolUtil.toList(var1), "protocols");
   }

   @Override
   public ApplicationProtocolConfig.Protocol protocol() {
      return ApplicationProtocolConfig.Protocol.NPN;
   }

   @Override
   public List<String> protocols() {
      return this.protocols;
   }

   @Override
   public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior() {
      return ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
   }

   @Override
   public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
      return ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT;
   }
}
