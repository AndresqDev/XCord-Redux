package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.ProtocolVersion;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class HttpRequestExecutor {
   public static final int DEFAULT_WAIT_FOR_CONTINUE = 3000;
   private final int waitForContinue;

   public HttpRequestExecutor(int var1) {
      this.waitForContinue = Args.positive(var1, "Wait for continue time");
   }

   public HttpRequestExecutor() {
      this(3000);
   }

   protected boolean canResponseHaveBody(HttpRequest var1, HttpResponse var2) {
      if ("HEAD".equalsIgnoreCase(var1.getRequestLine().getMethod())) {
         return false;
      } else {
         int var3 = var2.getStatusLine().getStatusCode();
         return var3 >= 200 && var3 != 204 && var3 != 304 && var3 != 205;
      }
   }

   public HttpResponse execute(HttpRequest var1, HttpClientConnection var2, HttpContext var3) throws IOException, HttpException {
      Args.notNull(var1, "HTTP request");
      Args.notNull(var2, "Client connection");
      Args.notNull(var3, "HTTP context");

      try {
         HttpResponse var4 = this.doSendRequest(var1, var2, var3);
         if (var4 == null) {
            var4 = this.doReceiveResponse(var1, var2, var3);
         }

         return var4;
      } catch (IOException var5) {
         closeConnection(var2);
         throw var5;
      } catch (HttpException var6) {
         closeConnection(var2);
         throw var6;
      } catch (RuntimeException var7) {
         closeConnection(var2);
         throw var7;
      }
   }

   private static void closeConnection(HttpClientConnection var0) {
      try {
         var0.close();
      } catch (IOException var2) {
      }
   }

   public void preProcess(HttpRequest var1, HttpProcessor var2, HttpContext var3) throws HttpException, IOException {
      Args.notNull(var1, "HTTP request");
      Args.notNull(var2, "HTTP processor");
      Args.notNull(var3, "HTTP context");
      var3.setAttribute("http.request", var1);
      var2.process(var1, var3);
   }

   protected HttpResponse doSendRequest(HttpRequest var1, HttpClientConnection var2, HttpContext var3) throws IOException, HttpException {
      Args.notNull(var1, "HTTP request");
      Args.notNull(var2, "Client connection");
      Args.notNull(var3, "HTTP context");
      HttpResponse var4 = null;
      var3.setAttribute("http.connection", var2);
      var3.setAttribute("http.request_sent", Boolean.FALSE);
      var2.sendRequestHeader(var1);
      if (var1 instanceof HttpEntityEnclosingRequest) {
         boolean var5 = true;
         ProtocolVersion var6 = var1.getRequestLine().getProtocolVersion();
         if (((HttpEntityEnclosingRequest)var1).expectContinue() && !var6.lessEquals(HttpVersion.HTTP_1_0)) {
            var2.flush();
            if (var2.isResponseAvailable(this.waitForContinue)) {
               var4 = var2.receiveResponseHeader();
               if (this.canResponseHaveBody(var1, var4)) {
                  var2.receiveResponseEntity(var4);
               }

               int var7 = var4.getStatusLine().getStatusCode();
               if (var7 < 200) {
                  if (var7 != 100) {
                     throw new ProtocolException("Unexpected response: " + var4.getStatusLine());
                  }

                  var4 = null;
               } else {
                  var5 = false;
               }
            }
         }

         if (var5) {
            var2.sendRequestEntity((HttpEntityEnclosingRequest)var1);
         }
      }

      var2.flush();
      var3.setAttribute("http.request_sent", Boolean.TRUE);
      return var4;
   }

   protected HttpResponse doReceiveResponse(HttpRequest var1, HttpClientConnection var2, HttpContext var3) throws HttpException, IOException {
      Args.notNull(var1, "HTTP request");
      Args.notNull(var2, "Client connection");
      Args.notNull(var3, "HTTP context");
      HttpResponse var4 = null;
      int var5 = 0;

      while (var4 == null || var5 < 200) {
         var4 = var2.receiveResponseHeader();
         var5 = var4.getStatusLine().getStatusCode();
         if (var5 < 100) {
            throw new ProtocolException("Invalid response: " + var4.getStatusLine());
         }

         if (this.canResponseHaveBody(var1, var4)) {
            var2.receiveResponseEntity(var4);
         }
      }

      return var4;
   }

   public void postProcess(HttpResponse var1, HttpProcessor var2, HttpContext var3) throws HttpException, IOException {
      Args.notNull(var1, "HTTP response");
      Args.notNull(var2, "HTTP processor");
      Args.notNull(var3, "HTTP context");
      var3.setAttribute("http.response", var1);
      var2.process(var1, var3);
   }
}
