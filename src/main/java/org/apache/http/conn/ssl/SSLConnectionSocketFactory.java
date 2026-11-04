package org.apache.http.conn.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import javax.net.SocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.security.auth.x500.X500Principal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.util.PublicSuffixMatcherLoader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.TextUtils;

@Contract(
   threading = ThreadingBehavior.SAFE
)
public class SSLConnectionSocketFactory implements LayeredConnectionSocketFactory {
   public static final String TLS = "TLS";
   public static final String SSL = "SSL";
   public static final String SSLV2 = "SSLv2";
   @Deprecated
   public static final X509HostnameVerifier ALLOW_ALL_HOSTNAME_VERIFIER = AllowAllHostnameVerifier.INSTANCE;
   @Deprecated
   public static final X509HostnameVerifier BROWSER_COMPATIBLE_HOSTNAME_VERIFIER = BrowserCompatHostnameVerifier.INSTANCE;
   @Deprecated
   public static final X509HostnameVerifier STRICT_HOSTNAME_VERIFIER = StrictHostnameVerifier.INSTANCE;
   private static final String WEAK_KEY_EXCHANGES = "^(TLS|SSL)_(NULL|ECDH_anon|DH_anon|DH_anon_EXPORT|DHE_RSA_EXPORT|DHE_DSS_EXPORT|DSS_EXPORT|DH_DSS_EXPORT|DH_RSA_EXPORT|RSA_EXPORT|KRB5_EXPORT)_(.*)";
   private static final String WEAK_CIPHERS = "^(TLS|SSL)_(.*)_WITH_(NULL|DES_CBC|DES40_CBC|DES_CBC_40|3DES_EDE_CBC|RC4_128|RC4_40|RC2_CBC_40)_(.*)";
   private static final List<Pattern> WEAK_CIPHER_SUITE_PATTERNS = Collections.unmodifiableList(
      Arrays.asList(
         Pattern.compile(
            "^(TLS|SSL)_(NULL|ECDH_anon|DH_anon|DH_anon_EXPORT|DHE_RSA_EXPORT|DHE_DSS_EXPORT|DSS_EXPORT|DH_DSS_EXPORT|DH_RSA_EXPORT|RSA_EXPORT|KRB5_EXPORT)_(.*)",
            2
         ),
         Pattern.compile("^(TLS|SSL)_(.*)_WITH_(NULL|DES_CBC|DES40_CBC|DES_CBC_40|3DES_EDE_CBC|RC4_128|RC4_40|RC2_CBC_40)_(.*)", 2)
      )
   );
   private final Log log = LogFactory.getLog(this.getClass());
   private final javax.net.ssl.SSLSocketFactory socketfactory;
   private final HostnameVerifier hostnameVerifier;
   private final String[] supportedProtocols;
   private final String[] supportedCipherSuites;

   public static HostnameVerifier getDefaultHostnameVerifier() {
      return new DefaultHostnameVerifier(PublicSuffixMatcherLoader.getDefault());
   }

   public static SSLConnectionSocketFactory getSocketFactory() throws SSLInitializationException {
      return new SSLConnectionSocketFactory(org.apache.http.ssl.SSLContexts.createDefault(), getDefaultHostnameVerifier());
   }

   static boolean isWeakCipherSuite(String var0) {
      for (Pattern var2 : WEAK_CIPHER_SUITE_PATTERNS) {
         if (var2.matcher(var0).matches()) {
            return true;
         }
      }

      return false;
   }

   private static String[] split(String var0) {
      return TextUtils.isBlank(var0) ? null : var0.split(" *, *");
   }

   public static SSLConnectionSocketFactory getSystemSocketFactory() throws SSLInitializationException {
      return new SSLConnectionSocketFactory(
         (javax.net.ssl.SSLSocketFactory)javax.net.ssl.SSLSocketFactory.getDefault(),
         split(System.getProperty("https.protocols")),
         split(System.getProperty("https.cipherSuites")),
         getDefaultHostnameVerifier()
      );
   }

   public SSLConnectionSocketFactory(SSLContext var1) {
      this(var1, getDefaultHostnameVerifier());
   }

   @Deprecated
   public SSLConnectionSocketFactory(SSLContext var1, X509HostnameVerifier var2) {
      this(Args.notNull(var1, "SSL context").getSocketFactory(), null, null, var2);
   }

   @Deprecated
   public SSLConnectionSocketFactory(SSLContext var1, String[] var2, String[] var3, X509HostnameVerifier var4) {
      this(Args.notNull(var1, "SSL context").getSocketFactory(), var2, var3, var4);
   }

   @Deprecated
   public SSLConnectionSocketFactory(javax.net.ssl.SSLSocketFactory var1, X509HostnameVerifier var2) {
      this(var1, null, null, var2);
   }

   @Deprecated
   public SSLConnectionSocketFactory(javax.net.ssl.SSLSocketFactory var1, String[] var2, String[] var3, X509HostnameVerifier var4) {
      this(var1, var2, var3, (HostnameVerifier)var4);
   }

   public SSLConnectionSocketFactory(SSLContext var1, HostnameVerifier var2) {
      this(Args.notNull(var1, "SSL context").getSocketFactory(), null, null, var2);
   }

   public SSLConnectionSocketFactory(SSLContext var1, String[] var2, String[] var3, HostnameVerifier var4) {
      this(Args.notNull(var1, "SSL context").getSocketFactory(), var2, var3, var4);
   }

   public SSLConnectionSocketFactory(javax.net.ssl.SSLSocketFactory var1, HostnameVerifier var2) {
      this(var1, null, null, var2);
   }

   public SSLConnectionSocketFactory(javax.net.ssl.SSLSocketFactory var1, String[] var2, String[] var3, HostnameVerifier var4) {
      this.socketfactory = Args.notNull(var1, "SSL socket factory");
      this.supportedProtocols = var2;
      this.supportedCipherSuites = var3;
      this.hostnameVerifier = var4 != null ? var4 : getDefaultHostnameVerifier();
   }

   protected void prepareSocket(SSLSocket var1) throws IOException {
   }

   @Override
   public Socket createSocket(HttpContext var1) throws IOException {
      return SocketFactory.getDefault().createSocket();
   }

   @Override
   public Socket connectSocket(int var1, Socket var2, HttpHost var3, InetSocketAddress var4, InetSocketAddress var5, HttpContext var6) throws IOException {
      Args.notNull(var3, "HTTP host");
      Args.notNull(var4, "Remote address");
      Socket var7 = var2 != null ? var2 : this.createSocket(var6);
      if (var5 != null) {
         var7.bind(var5);
      }

      try {
         if (var1 > 0 && var7.getSoTimeout() == 0) {
            var7.setSoTimeout(var1);
         }

         if (this.log.isDebugEnabled()) {
            this.log.debug("Connecting socket to " + var4 + " with timeout " + var1);
         }

         var7.connect(var4, var1);
      } catch (IOException var11) {
         try {
            var7.close();
         } catch (IOException var10) {
         }

         throw var11;
      }

      if (var7 instanceof SSLSocket) {
         SSLSocket var8 = (SSLSocket)var7;
         this.log.debug("Starting handshake");
         var8.startHandshake();
         this.verifyHostname(var8, var3.getHostName());
         return var7;
      } else {
         return this.createLayeredSocket(var7, var3.getHostName(), var4.getPort(), var6);
      }
   }

   @Override
   public Socket createLayeredSocket(Socket var1, String var2, int var3, HttpContext var4) throws IOException {
      SSLSocket var5 = (SSLSocket)this.socketfactory.createSocket(var1, var2, var3, true);
      if (this.supportedProtocols != null) {
         var5.setEnabledProtocols(this.supportedProtocols);
      } else {
         String[] var6 = var5.getEnabledProtocols();
         ArrayList var7 = new ArrayList(var6.length);

         for (String var11 : var6) {
            if (!var11.startsWith("SSL")) {
               var7.add(var11);
            }
         }

         if (!var7.isEmpty()) {
            var5.setEnabledProtocols(var7.toArray(new String[var7.size()]));
         }
      }

      if (this.supportedCipherSuites != null) {
         var5.setEnabledCipherSuites(this.supportedCipherSuites);
      } else {
         String[] var12 = var5.getEnabledCipherSuites();
         ArrayList var13 = new ArrayList(var12.length);

         for (String var17 : var12) {
            if (!isWeakCipherSuite(var17)) {
               var13.add(var17);
            }
         }

         if (!var13.isEmpty()) {
            var5.setEnabledCipherSuites(var13.toArray(new String[var13.size()]));
         }
      }

      if (this.log.isDebugEnabled()) {
         this.log.debug("Enabled protocols: " + Arrays.asList(var5.getEnabledProtocols()));
         this.log.debug("Enabled cipher suites:" + Arrays.asList(var5.getEnabledCipherSuites()));
      }

      this.prepareSocket(var5);
      this.log.debug("Starting handshake");
      var5.startHandshake();
      this.verifyHostname(var5, var2);
      return var5;
   }

   private void verifyHostname(SSLSocket var1, String var2) throws IOException {
      try {
         SSLSession var3 = var1.getSession();
         if (var3 == null) {
            InputStream var4 = var1.getInputStream();
            var4.available();
            var3 = var1.getSession();
            if (var3 == null) {
               var1.startHandshake();
               var3 = var1.getSession();
            }
         }

         if (var3 == null) {
            throw new SSLHandshakeException("SSL session not available");
         } else {
            if (this.log.isDebugEnabled()) {
               this.log.debug("Secure session established");
               this.log.debug(" negotiated protocol: " + var3.getProtocol());
               this.log.debug(" negotiated cipher suite: " + var3.getCipherSuite());

               try {
                  Certificate[] var16 = var3.getPeerCertificates();
                  X509Certificate var5 = (X509Certificate)var16[0];
                  X500Principal var6 = var5.getSubjectX500Principal();
                  this.log.debug(" peer principal: " + var6.toString());
                  Collection var7 = var5.getSubjectAlternativeNames();
                  if (var7 != null) {
                     ArrayList var8 = new ArrayList();

                     for (List var10 : var7) {
                        if (!var10.isEmpty()) {
                           var8.add((String)var10.get(1));
                        }
                     }

                     this.log.debug(" peer alternative names: " + var8);
                  }

                  X500Principal var20 = var5.getIssuerX500Principal();
                  this.log.debug(" issuer principal: " + var20.toString());
                  Collection var21 = var5.getIssuerAlternativeNames();
                  if (var21 != null) {
                     ArrayList var22 = new ArrayList();

                     for (List var12 : var21) {
                        if (!var12.isEmpty()) {
                           var22.add((String)var12.get(1));
                        }
                     }

                     this.log.debug(" issuer alternative names: " + var22);
                  }
               } catch (Exception var14) {
               }
            }

            if (!this.hostnameVerifier.verify(var2, var3)) {
               Certificate[] var17 = var3.getPeerCertificates();
               X509Certificate var18 = (X509Certificate)var17[0];
               List var19 = DefaultHostnameVerifier.getSubjectAltNames(var18);
               throw new SSLPeerUnverifiedException("Certificate for <" + var2 + "> doesn't match any " + "of the subject alternative names: " + var19);
            }
         }
      } catch (IOException var15) {
         try {
            var1.close();
         } catch (Exception var13) {
         }

         throw var15;
      }
   }
}
