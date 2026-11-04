package org.apache.http.client;

import org.apache.http.util.TextUtils;

public class HttpResponseException extends ClientProtocolException {
   private static final long serialVersionUID = -7186627969477257933L;
   private final int statusCode;
   private final String reasonPhrase;

   public HttpResponseException(int var1, String var2) {
      super(String.format("status code: %d" + (TextUtils.isBlank(var2) ? "" : ", reason phrase: %s"), var1, var2));
      this.statusCode = var1;
      this.reasonPhrase = var2;
   }

   public int getStatusCode() {
      return this.statusCode;
   }

   public String getReasonPhrase() {
      return this.reasonPhrase;
   }
}
