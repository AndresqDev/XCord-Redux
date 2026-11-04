package io.netty.handler.ssl.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SuppressJava6Requirement;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;

@SuppressJava6Requirement(
   reason = "Usage guarded by java version check"
)
final class X509TrustManagerWrapper extends X509ExtendedTrustManager {
   private final X509TrustManager delegate;

   X509TrustManagerWrapper(X509TrustManager var1) {
      this.delegate = ObjectUtil.checkNotNull(var1, "delegate");
   }

   @Override
   public void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException {
      this.delegate.checkClientTrusted(var1, var2);
   }

   @Override
   public void checkClientTrusted(X509Certificate[] var1, String var2, Socket var3) throws CertificateException {
      this.delegate.checkClientTrusted(var1, var2);
   }

   @Override
   public void checkClientTrusted(X509Certificate[] var1, String var2, SSLEngine var3) throws CertificateException {
      this.delegate.checkClientTrusted(var1, var2);
   }

   @Override
   public void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException {
      this.delegate.checkServerTrusted(var1, var2);
   }

   @Override
   public void checkServerTrusted(X509Certificate[] var1, String var2, Socket var3) throws CertificateException {
      this.delegate.checkServerTrusted(var1, var2);
   }

   @Override
   public void checkServerTrusted(X509Certificate[] var1, String var2, SSLEngine var3) throws CertificateException {
      this.delegate.checkServerTrusted(var1, var2);
   }

   @Override
   public X509Certificate[] getAcceptedIssuers() {
      return this.delegate.getAcceptedIssuers();
   }
}
