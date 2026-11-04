package org.apache.http.impl;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

@Deprecated
public class DefaultHttpServerConnection extends SocketHttpServerConnection {
   @Override
   public void bind(Socket var1, HttpParams var2) throws IOException {
      Args.notNull(var1, "Socket");
      Args.notNull(var2, "HTTP parameters");
      this.assertNotOpen();
      var1.setTcpNoDelay(var2.getBooleanParameter("http.tcp.nodelay", true));
      var1.setSoTimeout(var2.getIntParameter("http.socket.timeout", 0));
      var1.setKeepAlive(var2.getBooleanParameter("http.socket.keepalive", false));
      int var3 = var2.getIntParameter("http.socket.linger", -1);
      if (var3 >= 0) {
         var1.setSoLinger(var3 > 0, var3);
      }

      if (var3 >= 0) {
         var1.setSoLinger(var3 > 0, var3);
      }

      super.bind(var1, var2);
   }
}
