package org.apache.http.impl.io;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.HttpMessageParserFactory;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.BasicLineParser;
import org.apache.http.message.LineParser;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class DefaultHttpRequestParserFactory implements HttpMessageParserFactory<HttpRequest> {
   public static final DefaultHttpRequestParserFactory INSTANCE = new DefaultHttpRequestParserFactory();
   private final LineParser lineParser;
   private final HttpRequestFactory requestFactory;

   public DefaultHttpRequestParserFactory(LineParser var1, HttpRequestFactory var2) {
      this.lineParser = (LineParser)(var1 != null ? var1 : BasicLineParser.INSTANCE);
      this.requestFactory = (HttpRequestFactory)(var2 != null ? var2 : DefaultHttpRequestFactory.INSTANCE);
   }

   public DefaultHttpRequestParserFactory() {
      this(null, null);
   }

   @Override
   public HttpMessageParser<HttpRequest> create(SessionInputBuffer var1, MessageConstraints var2) {
      return new DefaultHttpRequestParser(var1, this.lineParser, this.requestFactory, var2);
   }
}
