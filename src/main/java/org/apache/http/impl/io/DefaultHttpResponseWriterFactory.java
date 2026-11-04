package org.apache.http.impl.io;

import org.apache.http.HttpResponse;
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
public class DefaultHttpResponseWriterFactory implements HttpMessageWriterFactory<HttpResponse> {
   public static final DefaultHttpResponseWriterFactory INSTANCE = new DefaultHttpResponseWriterFactory();
   private final LineFormatter lineFormatter;

   public DefaultHttpResponseWriterFactory(LineFormatter var1) {
      this.lineFormatter = (LineFormatter)(var1 != null ? var1 : BasicLineFormatter.INSTANCE);
   }

   public DefaultHttpResponseWriterFactory() {
      this(null);
   }

   @Override
   public HttpMessageWriter<HttpResponse> create(SessionOutputBuffer var1) {
      return new DefaultHttpResponseWriter(var1, this.lineFormatter);
   }
}
