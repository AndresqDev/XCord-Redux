package io.netty.handler.ssl;

import java.security.cert.Certificate;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

interface OpenSslSession extends SSLSession {
   OpenSslSessionId sessionId();

   void setLocalCertificate(Certificate[] var1);

   void setSessionId(OpenSslSessionId var1);

   OpenSslSessionContext getSessionContext();

   void tryExpandApplicationBufferSize(int var1);

   void handshakeFinished(byte[] var1, String var2, String var3, byte[] var4, byte[][] var5, long var6, long var8) throws SSLException;
}
