package org.apache.http.client.methods;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.utils.CloneUtils;

public abstract class HttpEntityEnclosingRequestBase extends HttpRequestBase implements HttpEntityEnclosingRequest {
   private HttpEntity entity;

   @Override
   public HttpEntity getEntity() {
      return this.entity;
   }

   @Override
   public void setEntity(HttpEntity var1) {
      this.entity = var1;
   }

   @Override
   public boolean expectContinue() {
      Header var1 = this.getFirstHeader("Expect");
      return var1 != null && "100-continue".equalsIgnoreCase(var1.getValue());
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      HttpEntityEnclosingRequestBase var1 = (HttpEntityEnclosingRequestBase)super.clone();
      if (this.entity != null) {
         var1.entity = CloneUtils.cloneObject(this.entity);
      }

      return var1;
   }
}
