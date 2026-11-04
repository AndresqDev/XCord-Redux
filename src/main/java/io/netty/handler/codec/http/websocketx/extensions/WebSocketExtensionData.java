package io.netty.handler.codec.http.websocketx.extensions;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.Map;

public final class WebSocketExtensionData {
   private final String name;
   private final Map<String, String> parameters;

   public WebSocketExtensionData(String var1, Map<String, String> var2) {
      this.name = ObjectUtil.checkNotNull(var1, "name");
      this.parameters = Collections.unmodifiableMap(ObjectUtil.checkNotNull(var2, "parameters"));
   }

   public String name() {
      return this.name;
   }

   public Map<String, String> parameters() {
      return this.parameters;
   }
}
