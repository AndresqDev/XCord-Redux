package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.LineFormatter;

public class DefaultHttpResponseWriter extends AbstractMessageWriter<HttpResponse> {
   public DefaultHttpResponseWriter(SessionOutputBuffer var1, LineFormatter var2) {
      super(var1, var2);
   }

   public DefaultHttpResponseWriter(SessionOutputBuffer var1) {
      super(var1, null);
   }

   protected void writeHeadLine(HttpResponse var1) throws IOException {
      this.lineFormatter.formatStatusLine(this.lineBuf, var1.getStatusLine());
      this.sessionBuffer.writeLine(this.lineBuf);
   }
}
