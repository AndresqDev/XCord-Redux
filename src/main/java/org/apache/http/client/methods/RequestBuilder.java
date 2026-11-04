package org.apache.http.client.methods;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.HeaderGroup;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;

public class RequestBuilder {
   private String method;
   private Charset charset;
   private ProtocolVersion version;
   private URI uri;
   private HeaderGroup headerGroup;
   private HttpEntity entity;
   private List<NameValuePair> parameters;
   private RequestConfig config;

   RequestBuilder(String var1) {
      this.charset = Consts.UTF_8;
      this.method = var1;
   }

   RequestBuilder(String var1, URI var2) {
      this.method = var1;
      this.uri = var2;
   }

   RequestBuilder(String var1, String var2) {
      this.method = var1;
      this.uri = var2 != null ? URI.create(var2) : null;
   }

   RequestBuilder() {
      this(null);
   }

   public static RequestBuilder create(String var0) {
      Args.notBlank(var0, "HTTP method");
      return new RequestBuilder(var0);
   }

   public static RequestBuilder get() {
      return new RequestBuilder("GET");
   }

   public static RequestBuilder get(URI var0) {
      return new RequestBuilder("GET", var0);
   }

   public static RequestBuilder get(String var0) {
      return new RequestBuilder("GET", var0);
   }

   public static RequestBuilder head() {
      return new RequestBuilder("HEAD");
   }

   public static RequestBuilder head(URI var0) {
      return new RequestBuilder("HEAD", var0);
   }

   public static RequestBuilder head(String var0) {
      return new RequestBuilder("HEAD", var0);
   }

   public static RequestBuilder patch() {
      return new RequestBuilder("PATCH");
   }

   public static RequestBuilder patch(URI var0) {
      return new RequestBuilder("PATCH", var0);
   }

   public static RequestBuilder patch(String var0) {
      return new RequestBuilder("PATCH", var0);
   }

   public static RequestBuilder post() {
      return new RequestBuilder("POST");
   }

   public static RequestBuilder post(URI var0) {
      return new RequestBuilder("POST", var0);
   }

   public static RequestBuilder post(String var0) {
      return new RequestBuilder("POST", var0);
   }

   public static RequestBuilder put() {
      return new RequestBuilder("PUT");
   }

   public static RequestBuilder put(URI var0) {
      return new RequestBuilder("PUT", var0);
   }

   public static RequestBuilder put(String var0) {
      return new RequestBuilder("PUT", var0);
   }

   public static RequestBuilder delete() {
      return new RequestBuilder("DELETE");
   }

   public static RequestBuilder delete(URI var0) {
      return new RequestBuilder("DELETE", var0);
   }

   public static RequestBuilder delete(String var0) {
      return new RequestBuilder("DELETE", var0);
   }

   public static RequestBuilder trace() {
      return new RequestBuilder("TRACE");
   }

   public static RequestBuilder trace(URI var0) {
      return new RequestBuilder("TRACE", var0);
   }

   public static RequestBuilder trace(String var0) {
      return new RequestBuilder("TRACE", var0);
   }

   public static RequestBuilder options() {
      return new RequestBuilder("OPTIONS");
   }

   public static RequestBuilder options(URI var0) {
      return new RequestBuilder("OPTIONS", var0);
   }

   public static RequestBuilder options(String var0) {
      return new RequestBuilder("OPTIONS", var0);
   }

   public static RequestBuilder copy(HttpRequest var0) {
      Args.notNull(var0, "HTTP request");
      return new RequestBuilder().doCopy(var0);
   }

   private RequestBuilder doCopy(HttpRequest var1) {
      if (var1 == null) {
         return this;
      } else {
         this.method = var1.getRequestLine().getMethod();
         this.version = var1.getRequestLine().getProtocolVersion();
         if (this.headerGroup == null) {
            this.headerGroup = new HeaderGroup();
         }

         this.headerGroup.clear();
         this.headerGroup.setHeaders(var1.getAllHeaders());
         this.parameters = null;
         this.entity = null;
         if (var1 instanceof HttpEntityEnclosingRequest) {
            HttpEntity var2 = ((HttpEntityEnclosingRequest)var1).getEntity();
            ContentType var3 = ContentType.get(var2);
            if (var3 != null && var3.getMimeType().equals(ContentType.APPLICATION_FORM_URLENCODED.getMimeType())) {
               try {
                  List var4 = URLEncodedUtils.parse(var2);
                  if (!var4.isEmpty()) {
                     this.parameters = var4;
                  }
               } catch (IOException var5) {
               }
            } else {
               this.entity = var2;
            }
         }

         if (var1 instanceof HttpUriRequest) {
            this.uri = ((HttpUriRequest)var1).getURI();
         } else {
            this.uri = URI.create(var1.getRequestLine().getUri());
         }

         if (var1 instanceof Configurable) {
            this.config = ((Configurable)var1).getConfig();
         } else {
            this.config = null;
         }

         return this;
      }
   }

   public RequestBuilder setCharset(Charset var1) {
      this.charset = var1;
      return this;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public String getMethod() {
      return this.method;
   }

   public ProtocolVersion getVersion() {
      return this.version;
   }

   public RequestBuilder setVersion(ProtocolVersion var1) {
      this.version = var1;
      return this;
   }

   public URI getUri() {
      return this.uri;
   }

   public RequestBuilder setUri(URI var1) {
      this.uri = var1;
      return this;
   }

   public RequestBuilder setUri(String var1) {
      this.uri = var1 != null ? URI.create(var1) : null;
      return this;
   }

   public Header getFirstHeader(String var1) {
      return this.headerGroup != null ? this.headerGroup.getFirstHeader(var1) : null;
   }

   public Header getLastHeader(String var1) {
      return this.headerGroup != null ? this.headerGroup.getLastHeader(var1) : null;
   }

   public Header[] getHeaders(String var1) {
      return this.headerGroup != null ? this.headerGroup.getHeaders(var1) : null;
   }

   public RequestBuilder addHeader(Header var1) {
      if (this.headerGroup == null) {
         this.headerGroup = new HeaderGroup();
      }

      this.headerGroup.addHeader(var1);
      return this;
   }

   public RequestBuilder addHeader(String var1, String var2) {
      if (this.headerGroup == null) {
         this.headerGroup = new HeaderGroup();
      }

      this.headerGroup.addHeader(new BasicHeader(var1, var2));
      return this;
   }

   public RequestBuilder removeHeader(Header var1) {
      if (this.headerGroup == null) {
         this.headerGroup = new HeaderGroup();
      }

      this.headerGroup.removeHeader(var1);
      return this;
   }

   public RequestBuilder removeHeaders(String var1) {
      if (var1 != null && this.headerGroup != null) {
         HeaderIterator var2 = this.headerGroup.iterator();

         while (var2.hasNext()) {
            Header var3 = var2.nextHeader();
            if (var1.equalsIgnoreCase(var3.getName())) {
               var2.remove();
            }
         }

         return this;
      } else {
         return this;
      }
   }

   public RequestBuilder setHeader(Header var1) {
      if (this.headerGroup == null) {
         this.headerGroup = new HeaderGroup();
      }

      this.headerGroup.updateHeader(var1);
      return this;
   }

   public RequestBuilder setHeader(String var1, String var2) {
      if (this.headerGroup == null) {
         this.headerGroup = new HeaderGroup();
      }

      this.headerGroup.updateHeader(new BasicHeader(var1, var2));
      return this;
   }

   public HttpEntity getEntity() {
      return this.entity;
   }

   public RequestBuilder setEntity(HttpEntity var1) {
      this.entity = var1;
      return this;
   }

   public List<NameValuePair> getParameters() {
      return this.parameters != null ? new ArrayList<>(this.parameters) : new ArrayList<>();
   }

   public RequestBuilder addParameter(NameValuePair var1) {
      Args.notNull(var1, "Name value pair");
      if (this.parameters == null) {
         this.parameters = new LinkedList<>();
      }

      this.parameters.add(var1);
      return this;
   }

   public RequestBuilder addParameter(String var1, String var2) {
      return this.addParameter(new BasicNameValuePair(var1, var2));
   }

   public RequestBuilder addParameters(NameValuePair... var1) {
      for (NameValuePair var5 : var1) {
         this.addParameter(var5);
      }

      return this;
   }

   public RequestConfig getConfig() {
      return this.config;
   }

   public RequestBuilder setConfig(RequestConfig var1) {
      this.config = var1;
      return this;
   }

   public HttpUriRequest build() {
      URI var2 = this.uri != null ? this.uri : URI.create("/");
      Object var3 = this.entity;
      if (this.parameters != null && !this.parameters.isEmpty()) {
         if (var3 == null && ("POST".equalsIgnoreCase(this.method) || "PUT".equalsIgnoreCase(this.method))) {
            var3 = new UrlEncodedFormEntity(this.parameters, this.charset != null ? this.charset : HTTP.DEF_CONTENT_CHARSET);
         } else {
            try {
               var2 = new URIBuilder(var2).setCharset(this.charset).addParameters(this.parameters).build();
            } catch (URISyntaxException var5) {
            }
         }
      }

      Object var1;
      if (var3 == null) {
         var1 = new RequestBuilder.InternalRequest(this.method);
      } else {
         RequestBuilder.InternalEntityEclosingRequest var4 = new RequestBuilder.InternalEntityEclosingRequest(this.method);
         var4.setEntity((HttpEntity)var3);
         var1 = var4;
      }

      ((HttpRequestBase)var1).setProtocolVersion(this.version);
      ((HttpRequestBase)var1).setURI(var2);
      if (this.headerGroup != null) {
         ((HttpRequestBase)var1).setHeaders(this.headerGroup.getAllHeaders());
      }

      ((HttpRequestBase)var1).setConfig(this.config);
      return (HttpUriRequest)var1;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("RequestBuilder [method=");
      var1.append(this.method);
      var1.append(", charset=");
      var1.append(this.charset);
      var1.append(", version=");
      var1.append(this.version);
      var1.append(", uri=");
      var1.append(this.uri);
      var1.append(", headerGroup=");
      var1.append(this.headerGroup);
      var1.append(", entity=");
      var1.append(this.entity);
      var1.append(", parameters=");
      var1.append(this.parameters);
      var1.append(", config=");
      var1.append(this.config);
      var1.append("]");
      return var1.toString();
   }

   static class InternalEntityEclosingRequest extends HttpEntityEnclosingRequestBase {
      private final String method;

      InternalEntityEclosingRequest(String var1) {
         this.method = var1;
      }

      @Override
      public String getMethod() {
         return this.method;
      }
   }

   static class InternalRequest extends HttpRequestBase {
      private final String method;

      InternalRequest(String var1) {
         this.method = var1;
      }

      @Override
      public String getMethod() {
         return this.method;
      }
   }
}
