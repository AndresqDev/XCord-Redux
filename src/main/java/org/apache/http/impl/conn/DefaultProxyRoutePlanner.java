package org.apache.http.impl.conn;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.SchemePortResolver;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL
)
public class DefaultProxyRoutePlanner extends DefaultRoutePlanner {
   private final HttpHost proxy;

   public DefaultProxyRoutePlanner(HttpHost var1, SchemePortResolver var2) {
      super(var2);
      this.proxy = Args.notNull(var1, "Proxy host");
   }

   public DefaultProxyRoutePlanner(HttpHost var1) {
      this(var1, null);
   }

   @Override
   protected HttpHost determineProxy(HttpHost var1, HttpRequest var2, HttpContext var3) throws HttpException {
      return this.proxy;
   }
}
