package org.apache.http.impl.io;

import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.BasicLineFormatter;
import org.apache.http.message.LineFormatter;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class DefaultHttpRequestWriterFactory implements HttpMessageWriterFactory<HttpRequest> {
   public static final DefaultHttpRequestWriterFactory INSTANCE = new DefaultHttpRequestWriterFactory();
   private final LineFormatter lineFormatter;

   public DefaultHttpRequestWriterFactory(LineFormatter var1) {
      this.lineFormatter = (LineFormatter)(var1 != null ? var1 : BasicLineFormatter.INSTANCE);
   }

   public DefaultHttpRequestWriterFactory() {
      this(null);
   }

   @Override
   public HttpMessageWriter<HttpRequest> create(SessionOutputBuffer var1) {
      return new DefaultHttpRequestWriter(var1, this.lineFormatter);
   }
}
