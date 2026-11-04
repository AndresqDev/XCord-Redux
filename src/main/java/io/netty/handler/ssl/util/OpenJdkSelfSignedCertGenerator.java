package io.netty.handler.ssl.util;

import io.netty.util.internal.SuppressJava6Requirement;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Date;
import sun.security.x509.AlgorithmId;
import sun.security.x509.CertificateAlgorithmId;
import sun.security.x509.CertificateIssuerName;
import sun.security.x509.CertificateSerialNumber;
import sun.security.x509.CertificateSubjectName;
import sun.security.x509.CertificateValidity;
import sun.security.x509.CertificateVersion;
import sun.security.x509.CertificateX509Key;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertImpl;
import sun.security.x509.X509CertInfo;

final class OpenJdkSelfSignedCertGenerator {
   @SuppressJava6Requirement(
      reason = "Usage guarded by dependency check"
   )
   static String[] generate(String var0, KeyPair var1, SecureRandom var2, Date var3, Date var4, String var5) throws Exception {
      PrivateKey var6 = var1.getPrivate();
      X509CertInfo var7 = new X509CertInfo();
      X500Name var8 = new X500Name("CN=" + var0);
      var7.set("version", new CertificateVersion(2));
      var7.set("serialNumber", new CertificateSerialNumber(new BigInteger(64, var2)));

      try {
         var7.set("subject", new CertificateSubjectName(var8));
      } catch (CertificateException var11) {
         var7.set("subject", var8);
      }

      try {
         var7.set("issuer", new CertificateIssuerName(var8));
      } catch (CertificateException var10) {
         var7.set("issuer", var8);
      }

      var7.set("validity", new CertificateValidity(var3, var4));
      var7.set("key", new CertificateX509Key(var1.getPublic()));
      var7.set("algorithmID", new CertificateAlgorithmId(AlgorithmId.get("1.2.840.113549.1.1.11")));
      X509CertImpl var9 = new X509CertImpl(var7);
      var9.sign(var6, var5.equalsIgnoreCase("EC") ? "SHA256withECDSA" : "SHA256withRSA");
      var7.set("algorithmID.algorithm", var9.get("x509.algorithm"));
      var9 = new X509CertImpl(var7);
      var9.sign(var6, var5.equalsIgnoreCase("EC") ? "SHA256withECDSA" : "SHA256withRSA");
      var9.verify(var1.getPublic());
      return SelfSignedCertificate.newSelfSignedCertificate(var0, var6, var9);
   }

   private OpenJdkSelfSignedCertGenerator() {
   }
}
