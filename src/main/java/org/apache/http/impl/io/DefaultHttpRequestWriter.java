package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.HttpRequest;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.LineFormatter;

public class DefaultHttpRequestWriter extends AbstractMessageWriter<HttpRequest> {
   public DefaultHttpRequestWriter(SessionOutputBuffer var1, LineFormatter var2) {
      super(var1, var2);
   }

   public DefaultHttpRequestWriter(SessionOutputBuffer var1) {
      this(var1, null);
   }

   protected void writeHeadLine(HttpRequest var1) throws IOException {
      this.lineFormatter.formatRequestLine(this.lineBuf, var1.getRequestLine());
      this.sessionBuffer.writeLine(this.lineBuf);
   }
}
