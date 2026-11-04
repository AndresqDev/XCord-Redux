package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolException;
import org.apache.http.StatusLine;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class DefaultHttpResponseParser extends AbstractMessageParser<HttpResponse> {
   private final Log log = LogFactory.getLog(this.getClass());
   private final HttpResponseFactory responseFactory;
   private final CharArrayBuffer lineBuf;

   @Deprecated
   public DefaultHttpResponseParser(SessionInputBuffer var1, LineParser var2, HttpResponseFactory var3, HttpParams var4) {
      super(var1, var2, var4);
      Args.notNull(var3, "Response factory");
      this.responseFactory = var3;
      this.lineBuf = new CharArrayBuffer(128);
   }

   public DefaultHttpResponseParser(SessionInputBuffer var1, LineParser var2, HttpResponseFactory var3, MessageConstraints var4) {
      super(var1, var2, var4);
      this.responseFactory = (HttpResponseFactory)(var3 != null ? var3 : DefaultHttpResponseFactory.INSTANCE);
      this.lineBuf = new CharArrayBuffer(128);
   }

   public DefaultHttpResponseParser(SessionInputBuffer var1, MessageConstraints var2) {
      this(var1, null, null, var2);
   }

   public DefaultHttpResponseParser(SessionInputBuffer var1) {
      this(var1, null, null, MessageConstraints.DEFAULT);
   }

   protected HttpResponse parseHead(SessionInputBuffer var1) throws IOException, HttpException {
      int var2 = 0;
      Object var3 = null;

      while (true) {
         this.lineBuf.clear();
         int var4 = var1.readLine(this.lineBuf);
         if (var4 == -1 && var2 == 0) {
            throw new NoHttpResponseException("The target server failed to respond");
         }

         var3 = new ParserCursor(0, this.lineBuf.length());
         if (this.lineParser.hasProtocolVersion(this.lineBuf, (ParserCursor)var3)) {
            StatusLine var6 = this.lineParser.parseStatusLine(this.lineBuf, (ParserCursor)var3);
            return this.responseFactory.newHttpResponse(var6, null);
         }

         if (var4 == -1 || this.reject(this.lineBuf, var2)) {
            throw new ProtocolException("The server failed to respond with a valid HTTP response");
         }

         if (this.log.isDebugEnabled()) {
            this.log.debug("Garbage in response: " + this.lineBuf.toString());
         }

         var2++;
      }
   }

   protected boolean reject(CharArrayBuffer var1, int var2) {
      return false;
   }
}
