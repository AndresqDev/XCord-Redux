package io.netty.handler.ssl;

import io.netty.util.internal.SuppressJava6Requirement;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;

class JdkSslEngine extends SSLEngine implements ApplicationProtocolAccessor {
   private final SSLEngine engine;
   private volatile String applicationProtocol;

   JdkSslEngine(SSLEngine var1) {
      this.engine = var1;
   }

   @Override
   public String getNegotiatedApplicationProtocol() {
      return this.applicationProtocol;
   }

   void setNegotiatedApplicationProtocol(String var1) {
      this.applicationProtocol = var1;
   }

   @Override
   public SSLSession getSession() {
      return this.engine.getSession();
   }

   public SSLEngine getWrappedEngine() {
      return this.engine;
   }

   @Override
   public void closeInbound() throws SSLException {
      this.engine.closeInbound();
   }

   @Override
   public void closeOutbound() {
      this.engine.closeOutbound();
   }

   @Override
   public String getPeerHost() {
      return this.engine.getPeerHost();
   }

   @Override
   public int getPeerPort() {
      return this.engine.getPeerPort();
   }

   @Override
   public SSLEngineResult wrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      return this.engine.wrap(var1, var2);
   }

   @Override
   public SSLEngineResult wrap(ByteBuffer[] var1, ByteBuffer var2) throws SSLException {
      return this.engine.wrap(var1, var2);
   }

   @Override
   public SSLEngineResult wrap(ByteBuffer[] var1, int var2, int var3, ByteBuffer var4) throws SSLException {
      return this.engine.wrap(var1, var2, var3, var4);
   }

   @Override
   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer var2) throws SSLException {
      return this.engine.unwrap(var1, var2);
   }

   @Override
   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2) throws SSLException {
      return this.engine.unwrap(var1, var2);
   }

   @Override
   public SSLEngineResult unwrap(ByteBuffer var1, ByteBuffer[] var2, int var3, int var4) throws SSLException {
      return this.engine.unwrap(var1, var2, var3, var4);
   }

   @Override
   public Runnable getDelegatedTask() {
      return this.engine.getDelegatedTask();
   }

   @Override
   public boolean isInboundDone() {
      return this.engine.isInboundDone();
   }

   @Override
   public boolean isOutboundDone() {
      return this.engine.isOutboundDone();
   }

   @Override
   public String[] getSupportedCipherSuites() {
      return this.engine.getSupportedCipherSuites();
   }

   @Override
   public String[] getEnabledCipherSuites() {
      return this.engine.getEnabledCipherSuites();
   }

   @Override
   public void setEnabledCipherSuites(String[] var1) {
      this.engine.setEnabledCipherSuites(var1);
   }

   @Override
   public String[] getSupportedProtocols() {
      return this.engine.getSupportedProtocols();
   }

   @Override
   public String[] getEnabledProtocols() {
      return this.engine.getEnabledProtocols();
   }

   @Override
   public void setEnabledProtocols(String[] var1) {
      this.engine.setEnabledProtocols(var1);
   }

   @SuppressJava6Requirement(
      reason = "Can only be called when running on JDK7+"
   )
   @Override
   public SSLSession getHandshakeSession() {
      return this.engine.getHandshakeSession();
   }

   @Override
   public void beginHandshake() throws SSLException {
      this.engine.beginHandshake();
   }

   @Override
   public HandshakeStatus getHandshakeStatus() {
      return this.engine.getHandshakeStatus();
   }

   @Override
   public void setUseClientMode(boolean var1) {
      this.engine.setUseClientMode(var1);
   }

   @Override
   public boolean getUseClientMode() {
      return this.engine.getUseClientMode();
   }

   @Override
   public void setNeedClientAuth(boolean var1) {
      this.engine.setNeedClientAuth(var1);
   }

   @Override
   public boolean getNeedClientAuth() {
      return this.engine.getNeedClientAuth();
   }

   @Override
   public void setWantClientAuth(boolean var1) {
      this.engine.setWantClientAuth(var1);
   }

   @Override
   public boolean getWantClientAuth() {
      return this.engine.getWantClientAuth();
   }

   @Override
   public void setEnableSessionCreation(boolean var1) {
      this.engine.setEnableSessionCreation(var1);
   }

   @Override
   public boolean getEnableSessionCreation() {
      return this.engine.getEnableSessionCreation();
   }

   @Override
   public SSLParameters getSSLParameters() {
      return this.engine.getSSLParameters();
   }

   @Override
   public void setSSLParameters(SSLParameters var1) {
      this.engine.setSSLParameters(var1);
   }
}
