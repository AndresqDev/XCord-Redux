package io.netty.handler.ssl.util;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

final class BouncyCastleSelfSignedCertGenerator {
   private static final Provider PROVIDER = new BouncyCastleProvider();

   static String[] generate(String var0, KeyPair var1, SecureRandom var2, Date var3, Date var4, String var5) throws Exception {
      PrivateKey var6 = var1.getPrivate();
      X500Name var7 = new X500Name("CN=" + var0);
      JcaX509v3CertificateBuilder var8 = new JcaX509v3CertificateBuilder(var7, new BigInteger(64, var2), var3, var4, var7, var1.getPublic());
      ContentSigner var9 = new JcaContentSignerBuilder(var5.equalsIgnoreCase("EC") ? "SHA256withECDSA" : "SHA256WithRSAEncryption").build(var6);
      X509CertificateHolder var10 = var8.build(var9);
      X509Certificate var11 = new JcaX509CertificateConverter().setProvider(PROVIDER).getCertificate(var10);
      var11.verify(var1.getPublic());
      return SelfSignedCertificate.newSelfSignedCertificate(var0, var6, var11);
   }

   private BouncyCastleSelfSignedCertGenerator() {
   }
}
