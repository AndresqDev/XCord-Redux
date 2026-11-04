package org.eclipse.aether.transport.http;

import java.net.URI;
import org.apache.http.client.methods.HttpRequestBase;

final class HttpMkCol extends HttpRequestBase {
   HttpMkCol(URI var1) {
      this.setURI(var1);
   }

   @Override
   public String getMethod() {
      return "MKCOL";
   }
}
