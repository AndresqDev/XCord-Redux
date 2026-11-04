package org.apache.http.impl.conn;

import java.io.IOException;
import org.apache.http.Consts;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.io.HttpTransportMetrics;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.util.CharArrayBuffer;

@Deprecated
@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class LoggingSessionOutputBuffer implements SessionOutputBuffer {
   private final SessionOutputBuffer out;
   private final Wire wire;
   private final String charset;

   public LoggingSessionOutputBuffer(SessionOutputBuffer var1, Wire var2, String var3) {
      this.out = var1;
      this.wire = var2;
      this.charset = var3 != null ? var3 : Consts.ASCII.name();
   }

   public LoggingSessionOutputBuffer(SessionOutputBuffer var1, Wire var2) {
      this(var1, var2, null);
   }

   @Override
   public void write(byte[] var1, int var2, int var3) throws IOException {
      this.out.write(var1, var2, var3);
      if (this.wire.enabled()) {
         this.wire.output(var1, var2, var3);
      }
   }

   @Override
   public void write(int var1) throws IOException {
      this.out.write(var1);
      if (this.wire.enabled()) {
         this.wire.output(var1);
      }
   }

   @Override
   public void write(byte[] var1) throws IOException {
      this.out.write(var1);
      if (this.wire.enabled()) {
         this.wire.output(var1);
      }
   }

   @Override
   public void flush() throws IOException {
      this.out.flush();
   }

   @Override
   public void writeLine(CharArrayBuffer var1) throws IOException {
      this.out.writeLine(var1);
      if (this.wire.enabled()) {
         String var2 = new String(var1.buffer(), 0, var1.length());
         String var3 = var2 + "\r\n";
         this.wire.output(var3.getBytes(this.charset));
      }
   }

   @Override
   public void writeLine(String var1) throws IOException {
      this.out.writeLine(var1);
      if (this.wire.enabled()) {
         String var2 = var1 + "\r\n";
         this.wire.output(var2.getBytes(this.charset));
      }
   }

   @Override
   public HttpTransportMetrics getMetrics() {
      return this.out.getMetrics();
   }
}
