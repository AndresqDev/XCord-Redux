package org.apache.http.impl.execchain;

import java.io.IOException;
import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.params.HttpParams;

class HttpResponseProxy implements CloseableHttpResponse {
   private final HttpResponse original;
   private final ConnectionHolder connHolder;

   public HttpResponseProxy(HttpResponse var1, ConnectionHolder var2) {
      this.original = var1;
      this.connHolder = var2;
      ResponseEntityProxy.enchance(var1, var2);
   }

   @Override
   public void close() throws IOException {
      if (this.connHolder != null) {
         this.connHolder.close();
      }
   }

   @Override
   public StatusLine getStatusLine() {
      return this.original.getStatusLine();
   }

   @Override
   public void setStatusLine(StatusLine var1) {
      this.original.setStatusLine(var1);
   }

   @Override
   public void setStatusLine(ProtocolVersion var1, int var2) {
      this.original.setStatusLine(var1, var2);
   }

   @Override
   public void setStatusLine(ProtocolVersion var1, int var2, String var3) {
      this.original.setStatusLine(var1, var2, var3);
   }

   @Override
   public void setStatusCode(int var1) throws IllegalStateException {
      this.original.setStatusCode(var1);
   }

   @Override
   public void setReasonPhrase(String var1) throws IllegalStateException {
      this.original.setReasonPhrase(var1);
   }

   @Override
   public HttpEntity getEntity() {
      return this.original.getEntity();
   }

   @Override
   public void setEntity(HttpEntity var1) {
      this.original.setEntity(var1);
   }

   @Override
   public Locale getLocale() {
      return this.original.getLocale();
   }

   @Override
   public void setLocale(Locale var1) {
      this.original.setLocale(var1);
   }

   @Override
   public ProtocolVersion getProtocolVersion() {
      return this.original.getProtocolVersion();
   }

   @Override
   public boolean containsHeader(String var1) {
      return this.original.containsHeader(var1);
   }

   @Override
   public Header[] getHeaders(String var1) {
      return this.original.getHeaders(var1);
   }

   @Override
   public Header getFirstHeader(String var1) {
      return this.original.getFirstHeader(var1);
   }

   @Override
   public Header getLastHeader(String var1) {
      return this.original.getLastHeader(var1);
   }

   @Override
   public Header[] getAllHeaders() {
      return this.original.getAllHeaders();
   }

   @Override
   public void addHeader(Header var1) {
      this.original.addHeader(var1);
   }

   @Override
   public void addHeader(String var1, String var2) {
      this.original.addHeader(var1, var2);
   }

   @Override
   public void setHeader(Header var1) {
      this.original.setHeader(var1);
   }

   @Override
   public void setHeader(String var1, String var2) {
      this.original.setHeader(var1, var2);
   }

   @Override
   public void setHeaders(Header[] var1) {
      this.original.setHeaders(var1);
   }

   @Override
   public void removeHeader(Header var1) {
      this.original.removeHeader(var1);
   }

   @Override
   public void removeHeaders(String var1) {
      this.original.removeHeaders(var1);
   }

   @Override
   public HeaderIterator headerIterator() {
      return this.original.headerIterator();
   }

   @Override
   public HeaderIterator headerIterator(String var1) {
      return this.original.headerIterator(var1);
   }

   @Override
   public HttpParams getParams() {
      return this.original.getParams();
   }

   @Override
   public void setParams(HttpParams var1) {
      this.original.setParams(var1);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("HttpResponseProxy{");
      var1.append(this.original);
      var1.append('}');
      return var1.toString();
   }
}
