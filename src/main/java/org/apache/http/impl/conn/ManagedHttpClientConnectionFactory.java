package org.apache.http.impl.conn;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.entity.LaxContentLengthStrategy;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.HttpMessageWriterFactory;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class ManagedHttpClientConnectionFactory implements HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> {
   private static final AtomicLong COUNTER = new AtomicLong();
   public static final ManagedHttpClientConnectionFactory INSTANCE = new ManagedHttpClientConnectionFactory();
   private final Log log = LogFactory.getLog(DefaultManagedHttpClientConnection.class);
   private final Log headerLog = LogFactory.getLog("org.apache.http.headers");
   private final Log wireLog = LogFactory.getLog("org.apache.http.wire");
   private final HttpMessageWriterFactory<HttpRequest> requestWriterFactory;
   private final HttpMessageParserFactory<HttpResponse> responseParserFactory;
   private final ContentLengthStrategy incomingContentStrategy;
   private final ContentLengthStrategy outgoingContentStrategy;

   public ManagedHttpClientConnectionFactory(
      HttpMessageWriterFactory<HttpRequest> var1, HttpMessageParserFactory<HttpResponse> var2, ContentLengthStrategy var3, ContentLengthStrategy var4
   ) {
      this.requestWriterFactory = (HttpMessageWriterFactory<HttpRequest>)(var1 != null ? var1 : DefaultHttpRequestWriterFactory.INSTANCE);
      this.responseParserFactory = (HttpMessageParserFactory<HttpResponse>)(var2 != null ? var2 : DefaultHttpResponseParserFactory.INSTANCE);
      this.incomingContentStrategy = (ContentLengthStrategy)(var3 != null ? var3 : LaxContentLengthStrategy.INSTANCE);
      this.outgoingContentStrategy = (ContentLengthStrategy)(var4 != null ? var4 : StrictContentLengthStrategy.INSTANCE);
   }

   public ManagedHttpClientConnectionFactory(HttpMessageWriterFactory<HttpRequest> var1, HttpMessageParserFactory<HttpResponse> var2) {
      this(var1, var2, null, null);
   }

   public ManagedHttpClientConnectionFactory(HttpMessageParserFactory<HttpResponse> var1) {
      this(null, var1);
   }

   public ManagedHttpClientConnectionFactory() {
      this(null, null);
   }

   public ManagedHttpClientConnection create(HttpRoute var1, ConnectionConfig var2) {
      ConnectionConfig var3 = var2 != null ? var2 : ConnectionConfig.DEFAULT;
      CharsetDecoder var4 = null;
      CharsetEncoder var5 = null;
      Charset var6 = var3.getCharset();
      CodingErrorAction var7 = var3.getMalformedInputAction() != null ? var3.getMalformedInputAction() : CodingErrorAction.REPORT;
      CodingErrorAction var8 = var3.getUnmappableInputAction() != null ? var3.getUnmappableInputAction() : CodingErrorAction.REPORT;
      if (var6 != null) {
         var4 = var6.newDecoder();
         var4.onMalformedInput(var7);
         var4.onUnmappableCharacter(var8);
         var5 = var6.newEncoder();
         var5.onMalformedInput(var7);
         var5.onUnmappableCharacter(var8);
      }

      String var9 = "http-outgoing-" + Long.toString(COUNTER.getAndIncrement());
      return new LoggingManagedHttpClientConnection(
         var9,
         this.log,
         this.headerLog,
         this.wireLog,
         var3.getBufferSize(),
         var3.getFragmentSizeHint(),
         var4,
         var5,
         var3.getMessageConstraints(),
         this.incomingContentStrategy,
         this.outgoingContentStrategy,
         this.requestWriterFactory,
         this.responseParserFactory
      );
   }
}
