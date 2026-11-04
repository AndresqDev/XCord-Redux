package org.apache.http.message;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpMessage;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

public abstract class AbstractHttpMessage implements HttpMessage {
   protected HeaderGroup headergroup = new HeaderGroup();
   @Deprecated
   protected HttpParams params;

   @Deprecated
   protected AbstractHttpMessage(HttpParams var1) {
      this.params = var1;
   }

   protected AbstractHttpMessage() {
      this(null);
   }

   @Override
   public boolean containsHeader(String var1) {
      return this.headergroup.containsHeader(var1);
   }

   @Override
   public Header[] getHeaders(String var1) {
      return this.headergroup.getHeaders(var1);
   }

   @Override
   public Header getFirstHeader(String var1) {
      return this.headergroup.getFirstHeader(var1);
   }

   @Override
   public Header getLastHeader(String var1) {
      return this.headergroup.getLastHeader(var1);
   }

   @Override
   public Header[] getAllHeaders() {
      return this.headergroup.getAllHeaders();
   }

   @Override
   public void addHeader(Header var1) {
      this.headergroup.addHeader(var1);
   }

   @Override
   public void addHeader(String var1, String var2) {
      Args.notNull(var1, "Header name");
      this.headergroup.addHeader(new BasicHeader(var1, var2));
   }

   @Override
   public void setHeader(Header var1) {
      this.headergroup.updateHeader(var1);
   }

   @Override
   public void setHeader(String var1, String var2) {
      Args.notNull(var1, "Header name");
      this.headergroup.updateHeader(new BasicHeader(var1, var2));
   }

   @Override
   public void setHeaders(Header[] var1) {
      this.headergroup.setHeaders(var1);
   }

   @Override
   public void removeHeader(Header var1) {
      this.headergroup.removeHeader(var1);
   }

   @Override
   public void removeHeaders(String var1) {
      if (var1 != null) {
         HeaderIterator var2 = this.headergroup.iterator();

         while (var2.hasNext()) {
            Header var3 = var2.nextHeader();
            if (var1.equalsIgnoreCase(var3.getName())) {
               var2.remove();
            }
         }
      }
   }

   @Override
   public HeaderIterator headerIterator() {
      return this.headergroup.iterator();
   }

   @Override
   public HeaderIterator headerIterator(String var1) {
      return this.headergroup.iterator(var1);
   }

   @Deprecated
   @Override
   public HttpParams getParams() {
      if (this.params == null) {
         this.params = new BasicHttpParams();
      }

      return this.params;
   }

   @Deprecated
   @Override
   public void setParams(HttpParams var1) {
      this.params = Args.notNull(var1, "HTTP parameters");
   }
}
