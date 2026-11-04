package org.apache.http.impl.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.SSLException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class DefaultHttpRequestRetryHandler implements HttpRequestRetryHandler {
   public static final DefaultHttpRequestRetryHandler INSTANCE = new DefaultHttpRequestRetryHandler();
   private final int retryCount;
   private final boolean requestSentRetryEnabled;
   private final Set<Class<? extends IOException>> nonRetriableClasses;

   protected DefaultHttpRequestRetryHandler(int var1, boolean var2, Collection<Class<? extends IOException>> var3) {
      this.retryCount = var1;
      this.requestSentRetryEnabled = var2;
      this.nonRetriableClasses = new HashSet<>();

      for (Class var5 : var3) {
         this.nonRetriableClasses.add(var5);
      }
   }

   public DefaultHttpRequestRetryHandler(int var1, boolean var2) {
      this(var1, var2, Arrays.asList(InterruptedIOException.class, UnknownHostException.class, ConnectException.class, SSLException.class));
   }

   public DefaultHttpRequestRetryHandler() {
      this(3, false);
   }

   @Override
   public boolean retryRequest(IOException var1, int var2, HttpContext var3) {
      Args.notNull(var1, "Exception parameter");
      Args.notNull(var3, "HTTP context");
      if (var2 > this.retryCount) {
         return false;
      } else if (this.nonRetriableClasses.contains(var1.getClass())) {
         return false;
      } else {
         for (Class var5 : this.nonRetriableClasses) {
            if (var5.isInstance(var1)) {
               return false;
            }
         }

         HttpClientContext var6 = HttpClientContext.adapt(var3);
         HttpRequest var7 = var6.getRequest();
         if (this.requestIsAborted(var7)) {
            return false;
         } else {
            return this.handleAsIdempotent(var7) ? true : !var6.isRequestSent() || this.requestSentRetryEnabled;
         }
      }
   }

   public boolean isRequestSentRetryEnabled() {
      return this.requestSentRetryEnabled;
   }

   public int getRetryCount() {
      return this.retryCount;
   }

   protected boolean handleAsIdempotent(HttpRequest var1) {
      return !(var1 instanceof HttpEntityEnclosingRequest);
   }

   @Deprecated
   protected boolean requestIsAborted(HttpRequest var1) {
      HttpRequest var2 = var1;
      if (var1 instanceof RequestWrapper) {
         var2 = ((RequestWrapper)var1).getOriginal();
      }

      return var2 instanceof HttpUriRequest && ((HttpUriRequest)var2).isAborted();
   }
}
