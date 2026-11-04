package org.apache.http.impl.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.ProtocolException;
import org.apache.http.entity.HttpEntityWrapper;

@Deprecated
public class EntityEnclosingRequestWrapper extends RequestWrapper implements HttpEntityEnclosingRequest {
   private HttpEntity entity;
   private boolean consumed;

   public EntityEnclosingRequestWrapper(HttpEntityEnclosingRequest var1) throws ProtocolException {
      super(var1);
      this.setEntity(var1.getEntity());
   }

   @Override
   public HttpEntity getEntity() {
      return this.entity;
   }

   @Override
   public void setEntity(HttpEntity var1) {
      this.entity = var1 != null ? new EntityEnclosingRequestWrapper.EntityWrapper(var1) : null;
      this.consumed = false;
   }

   @Override
   public boolean expectContinue() {
      Header var1 = this.getFirstHeader("Expect");
      return var1 != null && "100-continue".equalsIgnoreCase(var1.getValue());
   }

   @Override
   public boolean isRepeatable() {
      return this.entity == null || this.entity.isRepeatable() || !this.consumed;
   }

   class EntityWrapper extends HttpEntityWrapper {
      EntityWrapper(HttpEntity var2) {
         super(var2);
      }

      @Override
      public void consumeContent() throws IOException {
         EntityEnclosingRequestWrapper.this.consumed = true;
         super.consumeContent();
      }

      @Override
      public InputStream getContent() throws IOException {
         EntityEnclosingRequestWrapper.this.consumed = true;
         return super.getContent();
      }

      @Override
      public void writeTo(OutputStream var1) throws IOException {
         EntityEnclosingRequestWrapper.this.consumed = true;
         super.writeTo(var1);
      }
   }
}
