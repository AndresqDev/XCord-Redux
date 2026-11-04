package org.eclipse.aether.transport.http;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.CredentialsProvider;

final class DemuxCredentialsProvider implements CredentialsProvider {
   private final CredentialsProvider serverCredentialsProvider;
   private final CredentialsProvider proxyCredentialsProvider;
   private final HttpHost proxy;

   DemuxCredentialsProvider(CredentialsProvider var1, CredentialsProvider var2, HttpHost var3) {
      this.serverCredentialsProvider = var1;
      this.proxyCredentialsProvider = var2;
      this.proxy = var3;
   }

   private CredentialsProvider getDelegate(AuthScope var1) {
      return this.proxy.getPort() == var1.getPort() && this.proxy.getHostName().equalsIgnoreCase(var1.getHost())
         ? this.proxyCredentialsProvider
         : this.serverCredentialsProvider;
   }

   @Override
   public Credentials getCredentials(AuthScope var1) {
      return this.getDelegate(var1).getCredentials(var1);
   }

   @Override
   public void setCredentials(AuthScope var1, Credentials var2) {
      this.getDelegate(var1).setCredentials(var1, var2);
   }

   @Override
   public void clear() {
      this.serverCredentialsProvider.clear();
      this.proxyCredentialsProvider.clear();
   }
}
