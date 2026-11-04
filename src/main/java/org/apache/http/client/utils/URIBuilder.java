package org.apache.http.client.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.TextUtils;

public class URIBuilder {
   private String scheme;
   private String encodedSchemeSpecificPart;
   private String encodedAuthority;
   private String userInfo;
   private String encodedUserInfo;
   private String host;
   private int port;
   private String encodedPath;
   private List<String> pathSegments;
   private String encodedQuery;
   private List<NameValuePair> queryParams;
   private String query;
   private Charset charset;
   private String fragment;
   private String encodedFragment;

   public URIBuilder() {
      this.port = -1;
   }

   public URIBuilder(String var1) throws URISyntaxException {
      this(new URI(var1), null);
   }

   public URIBuilder(URI var1) {
      this(var1, null);
   }

   public URIBuilder(String var1, Charset var2) throws URISyntaxException {
      this(new URI(var1), var2);
   }

   public URIBuilder(URI var1, Charset var2) {
      this.setCharset(var2);
      this.digestURI(var1);
   }

   public URIBuilder setCharset(Charset var1) {
      this.charset = var1;
      return this;
   }

   public Charset getCharset() {
      return this.charset;
   }

   private List<NameValuePair> parseQuery(String var1, Charset var2) {
      return var1 != null && !var1.isEmpty() ? URLEncodedUtils.parse(var1, var2) : null;
   }

   private List<String> parsePath(String var1, Charset var2) {
      return var1 != null && !var1.isEmpty() ? URLEncodedUtils.parsePathSegments(var1, var2) : null;
   }

   public URI build() throws URISyntaxException {
      return new URI(this.buildString());
   }

   private String buildString() {
      StringBuilder var1 = new StringBuilder();
      if (this.scheme != null) {
         var1.append(this.scheme).append(':');
      }

      if (this.encodedSchemeSpecificPart != null) {
         var1.append(this.encodedSchemeSpecificPart);
      } else {
         if (this.encodedAuthority != null) {
            var1.append("//").append(this.encodedAuthority);
         } else if (this.host != null) {
            var1.append("//");
            if (this.encodedUserInfo != null) {
               var1.append(this.encodedUserInfo).append("@");
            } else if (this.userInfo != null) {
               var1.append(this.encodeUserInfo(this.userInfo)).append("@");
            }

            if (InetAddressUtils.isIPv6Address(this.host)) {
               var1.append("[").append(this.host).append("]");
            } else {
               var1.append(this.host);
            }

            if (this.port >= 0) {
               var1.append(":").append(this.port);
            }
         }

         if (this.encodedPath != null) {
            var1.append(normalizePath(this.encodedPath, var1.length() == 0));
         } else if (this.pathSegments != null) {
            var1.append(this.encodePath(this.pathSegments));
         }

         if (this.encodedQuery != null) {
            var1.append("?").append(this.encodedQuery);
         } else if (this.queryParams != null && !this.queryParams.isEmpty()) {
            var1.append("?").append(this.encodeUrlForm(this.queryParams));
         } else if (this.query != null) {
            var1.append("?").append(this.encodeUric(this.query));
         }
      }

      if (this.encodedFragment != null) {
         var1.append("#").append(this.encodedFragment);
      } else if (this.fragment != null) {
         var1.append("#").append(this.encodeUric(this.fragment));
      }

      return var1.toString();
   }

   private static String normalizePath(String var0, boolean var1) {
      String var2 = var0;
      if (TextUtils.isBlank(var0)) {
         return "";
      } else {
         if (!var1 && !var0.startsWith("/")) {
            var2 = "/" + var0;
         }

         return var2;
      }
   }

   private void digestURI(URI var1) {
      this.scheme = var1.getScheme();
      this.encodedSchemeSpecificPart = var1.getRawSchemeSpecificPart();
      this.encodedAuthority = var1.getRawAuthority();
      this.host = var1.getHost();
      this.port = var1.getPort();
      this.encodedUserInfo = var1.getRawUserInfo();
      this.userInfo = var1.getUserInfo();
      this.encodedPath = var1.getRawPath();
      this.pathSegments = this.parsePath(var1.getRawPath(), this.charset != null ? this.charset : Consts.UTF_8);
      this.encodedQuery = var1.getRawQuery();
      this.queryParams = this.parseQuery(var1.getRawQuery(), this.charset != null ? this.charset : Consts.UTF_8);
      this.encodedFragment = var1.getRawFragment();
      this.fragment = var1.getFragment();
   }

   private String encodeUserInfo(String var1) {
      return URLEncodedUtils.encUserInfo(var1, this.charset != null ? this.charset : Consts.UTF_8);
   }

   private String encodePath(List<String> var1) {
      return URLEncodedUtils.formatSegments(var1, this.charset != null ? this.charset : Consts.UTF_8);
   }

   private String encodeUrlForm(List<NameValuePair> var1) {
      return URLEncodedUtils.format(var1, this.charset != null ? this.charset : Consts.UTF_8);
   }

   private String encodeUric(String var1) {
      return URLEncodedUtils.encUric(var1, this.charset != null ? this.charset : Consts.UTF_8);
   }

   public URIBuilder setScheme(String var1) {
      this.scheme = var1;
      return this;
   }

   public URIBuilder setUserInfo(String var1) {
      this.userInfo = var1;
      this.encodedSchemeSpecificPart = null;
      this.encodedAuthority = null;
      this.encodedUserInfo = null;
      return this;
   }

   public URIBuilder setUserInfo(String var1, String var2) {
      return this.setUserInfo(var1 + ':' + var2);
   }

   public URIBuilder setHost(String var1) {
      this.host = var1;
      this.encodedSchemeSpecificPart = null;
      this.encodedAuthority = null;
      return this;
   }

   public URIBuilder setPort(int var1) {
      this.port = var1 < 0 ? -1 : var1;
      this.encodedSchemeSpecificPart = null;
      this.encodedAuthority = null;
      return this;
   }

   public URIBuilder setPath(String var1) {
      return this.setPathSegments(var1 != null ? URLEncodedUtils.splitPathSegments(var1) : null);
   }

   public URIBuilder setPathSegments(String... var1) {
      this.pathSegments = var1.length > 0 ? Arrays.asList(var1) : null;
      this.encodedSchemeSpecificPart = null;
      this.encodedPath = null;
      return this;
   }

   public URIBuilder setPathSegments(List<String> var1) {
      this.pathSegments = var1 != null && var1.size() > 0 ? new ArrayList<>(var1) : null;
      this.encodedSchemeSpecificPart = null;
      this.encodedPath = null;
      return this;
   }

   public URIBuilder removeQuery() {
      this.queryParams = null;
      this.query = null;
      this.encodedQuery = null;
      this.encodedSchemeSpecificPart = null;
      return this;
   }

   @Deprecated
   public URIBuilder setQuery(String var1) {
      this.queryParams = this.parseQuery(var1, this.charset != null ? this.charset : Consts.UTF_8);
      this.query = null;
      this.encodedQuery = null;
      this.encodedSchemeSpecificPart = null;
      return this;
   }

   public URIBuilder setParameters(List<NameValuePair> var1) {
      if (this.queryParams == null) {
         this.queryParams = new ArrayList<>();
      } else {
         this.queryParams.clear();
      }

      this.queryParams.addAll(var1);
      this.encodedQuery = null;
      this.encodedSchemeSpecificPart = null;
      this.query = null;
      return this;
   }

   public URIBuilder addParameters(List<NameValuePair> var1) {
      if (this.queryParams == null) {
         this.queryParams = new ArrayList<>();
      }

      this.queryParams.addAll(var1);
      this.encodedQuery = null;
      this.encodedSchemeSpecificPart = null;
      this.query = null;
      return this;
   }

   public URIBuilder setParameters(NameValuePair... var1) {
      if (this.queryParams == null) {
         this.queryParams = new ArrayList<>();
      } else {
         this.queryParams.clear();
      }

      for (NameValuePair var5 : var1) {
         this.queryParams.add(var5);
      }

      this.encodedQuery = null;
      this.encodedSchemeSpecificPart = null;
      this.query = null;
      return this;
   }

   public URIBuilder addParameter(String var1, String var2) {
      if (this.queryParams == null) {
         this.queryParams = new ArrayList<>();
      }

      this.queryParams.add(new BasicNameValuePair(var1, var2));
      this.encodedQuery = null;
      this.encodedSchemeSpecificPart = null;
      this.query = null;
      return this;
   }

   public URIBuilder setParameter(String var1, String var2) {
      if (this.queryParams == null) {
         this.queryParams = new ArrayList<>();
      }

      if (!this.queryParams.isEmpty()) {
         Iterator var3 = this.queryParams.iterator();

         while (var3.hasNext()) {
            NameValuePair var4 = (NameValuePair)var3.next();
            if (var4.getName().equals(var1)) {
               var3.remove();
            }
         }
      }

      this.queryParams.add(new BasicNameValuePair(var1, var2));
      this.encodedQuery = null;
      this.encodedSchemeSpecificPart = null;
      this.query = null;
      return this;
   }

   public URIBuilder clearParameters() {
      this.queryParams = null;
      this.encodedQuery = null;
      this.encodedSchemeSpecificPart = null;
      return this;
   }

   public URIBuilder setCustomQuery(String var1) {
      this.query = var1;
      this.encodedQuery = null;
      this.encodedSchemeSpecificPart = null;
      this.queryParams = null;
      return this;
   }

   public URIBuilder setFragment(String var1) {
      this.fragment = var1;
      this.encodedFragment = null;
      return this;
   }

   public boolean isAbsolute() {
      return this.scheme != null;
   }

   public boolean isOpaque() {
      return this.pathSegments == null && this.encodedPath == null;
   }

   public String getScheme() {
      return this.scheme;
   }

   public String getUserInfo() {
      return this.userInfo;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public boolean isPathEmpty() {
      return (this.pathSegments == null || this.pathSegments.isEmpty()) && (this.encodedPath == null || this.encodedPath.isEmpty());
   }

   public List<String> getPathSegments() {
      return (List<String>)(this.pathSegments != null ? new ArrayList<>(this.pathSegments) : Collections.emptyList());
   }

   public String getPath() {
      if (this.pathSegments == null) {
         return null;
      } else {
         StringBuilder var1 = new StringBuilder();

         for (String var3 : this.pathSegments) {
            var1.append('/').append(var3);
         }

         return var1.toString();
      }
   }

   public boolean isQueryEmpty() {
      return (this.queryParams == null || this.queryParams.isEmpty()) && this.encodedQuery == null;
   }

   public List<NameValuePair> getQueryParams() {
      return (List<NameValuePair>)(this.queryParams != null ? new ArrayList<>(this.queryParams) : Collections.emptyList());
   }

   public String getFragment() {
      return this.fragment;
   }

   @Override
   public String toString() {
      return this.buildString();
   }
}
