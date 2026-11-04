package org.apache.http.impl;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.HttpConnectionFactory;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class DefaultBHttpClientConnectionFactory implements HttpConnectionFactory<DefaultBHttpClientConnection> {
   public static final DefaultBHttpClientConnectionFactory INSTANCE = new DefaultBHttpClientConnectionFactory();
   private final ConnectionConfig cconfig;
   private final ContentLengthStrategy incomingContentStrategy;
   private final ContentLengthStrategy outgoingContentStrategy;
   private final HttpMessageWriterFactory<HttpRequest> requestWriterFactory;
   private final HttpMessageParserFactory<HttpResponse> responseParserFactory;

   public DefaultBHttpClientConnectionFactory(
      ConnectionConfig var1,
      ContentLengthStrategy var2,
      ContentLengthStrategy var3,
      HttpMessageWriterFactory<HttpRequest> var4,
      HttpMessageParserFactory<HttpResponse> var5
   ) {
      this.cconfig = var1 != null ? var1 : ConnectionConfig.DEFAULT;
      this.incomingContentStrategy = var2;
      this.outgoingContentStrategy = var3;
      this.requestWriterFactory = var4;
      this.responseParserFactory = var5;
   }

   public DefaultBHttpClientConnectionFactory(ConnectionConfig var1, HttpMessageWriterFactory<HttpRequest> var2, HttpMessageParserFactory<HttpResponse> var3) {
      this(var1, null, null, var2, var3);
   }

   public DefaultBHttpClientConnectionFactory(ConnectionConfig var1) {
      this(var1, null, null, null, null);
   }

   public DefaultBHttpClientConnectionFactory() {
      this(null, null, null, null, null);
   }

   public DefaultBHttpClientConnection createConnection(Socket var1) throws IOException {
      DefaultBHttpClientConnection var2 = new DefaultBHttpClientConnection(
         this.cconfig.getBufferSize(),
         this.cconfig.getFragmentSizeHint(),
         ConnSupport.createDecoder(this.cconfig),
         ConnSupport.createEncoder(this.cconfig),
         this.cconfig.getMessageConstraints(),
         this.incomingContentStrategy,
         this.outgoingContentStrategy,
         this.requestWriterFactory,
         this.responseParserFactory
      );
      var2.bind(var1);
      return var2;
   }
}
