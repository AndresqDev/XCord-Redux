package org.eclipse.aether.transport.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.auth.KerberosSchemeFactory;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.auth.SPNegoSchemeFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.transport.AbstractTransporter;
import org.eclipse.aether.spi.connector.transport.GetTask;
import org.eclipse.aether.spi.connector.transport.PeekTask;
import org.eclipse.aether.spi.connector.transport.PutTask;
import org.eclipse.aether.spi.connector.transport.TransportTask;
import org.eclipse.aether.transfer.NoTransporterException;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.util.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class HttpTransporter extends AbstractTransporter {
   private static final Pattern CONTENT_RANGE_PATTERN = Pattern.compile("\\s*bytes\\s+([0-9]+)\\s*-\\s*([0-9]+)\\s*/.*");
   private static final Logger LOGGER = LoggerFactory.getLogger(HttpTransporter.class);
   private final AuthenticationContext repoAuthContext;
   private final AuthenticationContext proxyAuthContext;
   private final URI baseUri;
   private final HttpHost server;
   private final HttpHost proxy;
   private final CloseableHttpClient client;
   private final Map<?, ?> headers;
   private final LocalState state;

   HttpTransporter(RemoteRepository var1, RepositorySystemSession var2) throws NoTransporterException {
      if (!"http".equalsIgnoreCase(var1.getProtocol()) && !"https".equalsIgnoreCase(var1.getProtocol())) {
         throw new NoTransporterException(var1);
      } else {
         try {
            this.baseUri = new URI(var1.getUrl()).parseServerAuthority();
            if (this.baseUri.isOpaque()) {
               throw new URISyntaxException(var1.getUrl(), "URL must not be opaque");
            }

            this.server = URIUtils.extractHost(this.baseUri);
            if (this.server == null) {
               throw new URISyntaxException(var1.getUrl(), "URL lacks host name");
            }
         } catch (URISyntaxException var11) {
            throw new NoTransporterException(var1, var11.getMessage(), var11);
         }

         this.proxy = toHost(var1.getProxy());
         this.repoAuthContext = AuthenticationContext.forRepository(var2, var1);
         this.proxyAuthContext = AuthenticationContext.forProxy(var2, var1);
         this.state = new LocalState(var2, var1, new SslConfig(var2, this.repoAuthContext));
         this.headers = ConfigUtils.getMap(var2, Collections.emptyMap(), "aether.connector.http.headers." + var1.getId(), "aether.connector.http.headers");
         String var3 = ConfigUtils.getString(
            var2, "ISO-8859-1", "aether.connector.http.credentialEncoding." + var1.getId(), "aether.connector.http.credentialEncoding"
         );
         int var4 = ConfigUtils.getInteger(var2, 10000, "aether.connector.connectTimeout." + var1.getId(), "aether.connector.connectTimeout");
         int var5 = ConfigUtils.getInteger(var2, 1800000, "aether.connector.requestTimeout." + var1.getId(), "aether.connector.requestTimeout");
         String var6 = ConfigUtils.getString(var2, "Aether", "aether.connector.userAgent");
         Charset var7 = Charset.forName(var3);
         Registry var8 = RegistryBuilder.<BasicSchemeFactory>create()
            .register("Basic", new BasicSchemeFactory(var7))
            .register("Digest", new DigestSchemeFactory(var7))
            .register("NTLM", new NTLMSchemeFactory())
            .register("Negotiate", new SPNegoSchemeFactory())
            .register("Kerberos", new KerberosSchemeFactory())
            .build();
         SocketConfig var9 = SocketConfig.custom().setSoTimeout(var5).build();
         RequestConfig var10 = RequestConfig.custom().setConnectTimeout(var4).setConnectionRequestTimeout(var4).setSocketTimeout(var5).build();
         this.client = HttpClientBuilder.create()
            .setUserAgent(var6)
            .setDefaultSocketConfig(var9)
            .setDefaultRequestConfig(var10)
            .setDefaultAuthSchemeRegistry(var8)
            .setConnectionManager(this.state.getConnectionManager())
            .setConnectionManagerShared(true)
            .setDefaultCredentialsProvider(toCredentialsProvider(this.server, this.repoAuthContext, this.proxy, this.proxyAuthContext))
            .setProxy(this.proxy)
            .build();
      }
   }

   private static HttpHost toHost(Proxy var0) {
      HttpHost var1 = null;
      if (var0 != null) {
         var1 = new HttpHost(var0.getHost(), var0.getPort());
      }

      return var1;
   }

   private static CredentialsProvider toCredentialsProvider(HttpHost var0, AuthenticationContext var1, HttpHost var2, AuthenticationContext var3) {
      Object var4 = toCredentialsProvider(var0.getHostName(), -1, var1);
      if (var2 != null) {
         CredentialsProvider var5 = toCredentialsProvider(var2.getHostName(), var2.getPort(), var3);
         var4 = new DemuxCredentialsProvider((CredentialsProvider)var4, var5, var2);
      }

      return (CredentialsProvider)var4;
   }

   private static CredentialsProvider toCredentialsProvider(String var0, int var1, AuthenticationContext var2) {
      DeferredCredentialsProvider var3 = new DeferredCredentialsProvider();
      if (var2 != null) {
         AuthScope var4 = new AuthScope(var0, var1);
         var3.setCredentials(var4, new DeferredCredentialsProvider.BasicFactory(var2));
         AuthScope var5 = new AuthScope(var0, var1, AuthScope.ANY_REALM, "ntlm");
         var3.setCredentials(var5, new DeferredCredentialsProvider.NtlmFactory(var2));
      }

      return var3;
   }

   LocalState getState() {
      return this.state;
   }

   private URI resolve(TransportTask var1) {
      return UriUtils.resolve(this.baseUri, var1.getLocation());
   }

   @Override
   public int classify(Throwable var1) {
      return var1 instanceof HttpResponseException && ((HttpResponseException)var1).getStatusCode() == 404 ? 1 : 0;
   }

   @Override
   protected void implPeek(PeekTask var1) throws Exception {
      HttpHead var2 = this.commonHeaders(new HttpHead(this.resolve(var1)));
      this.execute(var2, null);
   }

   @Override
   protected void implGet(GetTask var1) throws Exception {
      HttpTransporter.EntityGetter var2 = new HttpTransporter.EntityGetter(var1);
      HttpGet var3 = this.commonHeaders(new HttpGet(this.resolve(var1)));
      this.resume(var3, var1);

      try {
         this.execute(var3, var2);
      } catch (HttpResponseException var5) {
         if (var5.getStatusCode() == 412 && var3.containsHeader("Range")) {
            var3 = this.commonHeaders(new HttpGet(var3.getURI()));
            this.execute(var3, var2);
         } else {
            throw var5;
         }
      }
   }

   @Override
   protected void implPut(PutTask var1) throws Exception {
      HttpTransporter.PutTaskEntity var2 = new HttpTransporter.PutTaskEntity(var1);
      HttpPut var3 = this.commonHeaders(this.entity(new HttpPut(this.resolve(var1)), var2));

      try {
         this.execute(var3, null);
      } catch (HttpResponseException var5) {
         if (var5.getStatusCode() == 417 && var3.containsHeader("Expect")) {
            this.state.setExpectContinue(false);
            var3 = this.commonHeaders(this.entity(new HttpPut(var3.getURI()), var2));
            this.execute(var3, null);
         } else {
            throw var5;
         }
      }
   }

   private void execute(HttpUriRequest var1, HttpTransporter.EntityGetter var2) throws Exception {
      try {
         SharingHttpContext var3 = new SharingHttpContext(this.state);
         this.prepare(var1, var3);
         CloseableHttpResponse var4 = this.client.execute(this.server, var1, var3);

         try {
            var3.close();
            this.handleStatus(var4);
            if (var2 != null) {
               var2.handle(var4);
            }
         } finally {
            EntityUtils.consumeQuietly(var4.getEntity());
         }
      } catch (IOException var9) {
         if (var9.getCause() instanceof TransferCancelledException) {
            throw (Exception)var9.getCause();
         } else {
            throw var9;
         }
      }
   }

   private void prepare(HttpUriRequest var1, SharingHttpContext var2) {
      boolean var3 = "PUT".equalsIgnoreCase(var1.getMethod());
      if (this.state.getWebDav() == null && (var3 || this.isPayloadPresent(var1))) {
         try {
            HttpOptions var4 = this.commonHeaders(new HttpOptions(var1.getURI()));
            CloseableHttpResponse var5 = this.client.execute(this.server, var4, var2);
            this.state.setWebDav(this.isWebDav(var5));
            EntityUtils.consumeQuietly(var5.getEntity());
         } catch (IOException var6) {
            LOGGER.debug("Failed to prepare HTTP context", (Throwable)var6);
         }
      }

      if (var3 && Boolean.TRUE.equals(this.state.getWebDav())) {
         this.mkdirs(var1.getURI(), var2);
      }
   }

   private boolean isWebDav(HttpResponse var1) {
      return var1.containsHeader("Dav");
   }

   private void mkdirs(URI var1, SharingHttpContext var2) {
      List var3 = UriUtils.getDirectories(this.baseUri, var1);

      int var4;
      for (var4 = 0; var4 < var3.size(); var4++) {
         try {
            CloseableHttpResponse var5 = this.client.execute(this.server, this.commonHeaders(new HttpMkCol((URI)var3.get(var4))), var2);

            try {
               int var6 = var5.getStatusLine().getStatusCode();
               if (var6 < 300 || var6 == 405) {
                  break;
               }

               if (var6 != 409) {
                  this.handleStatus(var5);
               }
            } finally {
               EntityUtils.consumeQuietly(var5.getEntity());
            }
         } catch (IOException var20) {
            LOGGER.debug("Failed to create parent directory {}", var3.get(var4), var20);
            return;
         }
      }

      var4--;

      for (; var4 >= 0; var4--) {
         try {
            CloseableHttpResponse var22 = this.client.execute(this.server, this.commonHeaders(new HttpMkCol((URI)var3.get(var4))), var2);

            try {
               this.handleStatus(var22);
            } finally {
               EntityUtils.consumeQuietly(var22.getEntity());
            }
         } catch (IOException var18) {
            LOGGER.debug("Failed to create parent directory {}", var3.get(var4), var18);
            return;
         }
      }
   }

   private <T extends HttpEntityEnclosingRequest> T entity(T var1, HttpEntity var2) {
      var1.setEntity(var2);
      return (T)var1;
   }

   private boolean isPayloadPresent(HttpUriRequest var1) {
      if (!(var1 instanceof HttpEntityEnclosingRequest)) {
         return false;
      } else {
         HttpEntity var2 = ((HttpEntityEnclosingRequest)var1).getEntity();
         return var2 != null && var2.getContentLength() != 0L;
      }
   }

   private <T extends HttpUriRequest> T commonHeaders(T var1) {
      var1.setHeader("Cache-Control", "no-cache, no-store");
      var1.setHeader("Pragma", "no-cache");
      if (this.state.isExpectContinue() && this.isPayloadPresent(var1)) {
         var1.setHeader("Expect", "100-continue");
      }

      for (Entry var3 : this.headers.entrySet()) {
         if (var3.getKey() instanceof String) {
            if (var3.getValue() instanceof String) {
               var1.setHeader(var3.getKey().toString(), var3.getValue().toString());
            } else {
               var1.removeHeaders(var3.getKey().toString());
            }
         }
      }

      if (!this.state.isExpectContinue()) {
         var1.removeHeaders("Expect");
      }

      return (T)var1;
   }

   private <T extends HttpUriRequest> T resume(T var1, GetTask var2) {
      long var3 = var2.getResumeOffset();
      if (var3 > 0L && var2.getDataFile() != null) {
         var1.setHeader("Range", "bytes=" + var3 + '-');
         var1.setHeader("If-Unmodified-Since", DateUtils.formatDate(new Date(var2.getDataFile().lastModified() - 60000L)));
         var1.setHeader("Accept-Encoding", "identity");
      }

      return (T)var1;
   }

   private void handleStatus(HttpResponse var1) throws HttpResponseException {
      int var2 = var1.getStatusLine().getStatusCode();
      if (var2 >= 300) {
         throw new HttpResponseException(var2, var1.getStatusLine().getReasonPhrase() + " (" + var2 + ")");
      }
   }

   @Override
   protected void implClose() {
      try {
         this.client.close();
      } catch (IOException var2) {
         throw new UncheckedIOException(var2);
      }

      AuthenticationContext.close(this.repoAuthContext);
      AuthenticationContext.close(this.proxyAuthContext);
      this.state.close();
   }

   private class EntityGetter {
      private final GetTask task;

      EntityGetter(GetTask var2) {
         this.task = var2;
      }

      public void handle(HttpResponse var1) throws IOException, TransferCancelledException {
         Object var2 = var1.getEntity();
         if (var2 == null) {
            var2 = new ByteArrayEntity(new byte[0]);
         }

         long var3 = 0L;
         long var5 = ((HttpEntity)var2).getContentLength();
         String var7 = this.getHeader(var1, "Content-Range");
         if (var7 != null) {
            Matcher var8 = HttpTransporter.CONTENT_RANGE_PATTERN.matcher(var7);
            if (!var8.matches()) {
               throw new IOException("Invalid Content-Range header for partial download: " + var7);
            }

            var3 = Long.parseLong(var8.group(1));
            var5 = Long.parseLong(var8.group(2)) + 1L;
            if (var3 < 0L || var3 >= var5 || var3 > 0L && var3 != this.task.getResumeOffset()) {
               throw new IOException("Invalid Content-Range header for partial download from offset " + this.task.getResumeOffset() + ": " + var7);
            }
         }

         InputStream var9 = ((HttpEntity)var2).getContent();
         HttpTransporter.this.utilGet(this.task, var9, true, var5, var3 > 0L);
         this.extractChecksums(var1);
      }

      private void extractChecksums(HttpResponse var1) {
         String var2 = this.getHeader(var1, "ETag");
         if (var2 != null) {
            int var3 = var2.indexOf("SHA1{");
            int var4 = var2.indexOf("}", var3 + 5);
            if (var3 >= 0 && var4 > var3) {
               this.task.setChecksum("SHA-1", var2.substring(var3 + 5, var4));
            }
         }
      }

      private String getHeader(HttpResponse var1, String var2) {
         Header var3 = var1.getFirstHeader(var2);
         return var3 != null ? var3.getValue() : null;
      }
   }

   private class PutTaskEntity extends AbstractHttpEntity {
      private final PutTask task;

      PutTaskEntity(PutTask var2) {
         this.task = var2;
      }

      @Override
      public boolean isRepeatable() {
         return true;
      }

      @Override
      public boolean isStreaming() {
         return false;
      }

      @Override
      public long getContentLength() {
         return this.task.getDataLength();
      }

      @Override
      public InputStream getContent() throws IOException {
         return this.task.newInputStream();
      }

      @Override
      public void writeTo(OutputStream var1) throws IOException {
         try {
            HttpTransporter.this.utilPut(this.task, var1, false);
         } catch (TransferCancelledException var3) {
            throw (IOException)new InterruptedIOException().initCause(var3);
         }
      }
   }
}
