package org.apache.maven.repository;

public class Proxy {
   public static final String PROXY_SOCKS5 = "SOCKS_5";
   public static final String PROXY_SOCKS4 = "SOCKS4";
   public static final String PROXY_HTTP = "HTTP";
   private String host;
   private String userName;
   private String password;
   private int port;
   private String protocol;
   private String nonProxyHosts;
   private String ntlmHost;
   private String ntlmDomain;

   public String getHost() {
      return this.host;
   }

   public void setHost(String var1) {
      this.host = var1;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String var1) {
      this.password = var1;
   }

   public int getPort() {
      return this.port;
   }

   public void setPort(int var1) {
      this.port = var1;
   }

   public String getUserName() {
      return this.userName;
   }

   public void setUserName(String var1) {
      this.userName = var1;
   }

   public String getProtocol() {
      return this.protocol;
   }

   public void setProtocol(String var1) {
      this.protocol = var1;
   }

   public String getNonProxyHosts() {
      return this.nonProxyHosts;
   }

   public void setNonProxyHosts(String var1) {
      this.nonProxyHosts = var1;
   }

   public String getNtlmHost() {
      return this.ntlmHost;
   }

   public void setNtlmHost(String var1) {
      this.ntlmHost = var1;
   }

   public void setNtlmDomain(String var1) {
      this.ntlmDomain = var1;
   }

   public String getNtlmDomain() {
      return this.ntlmDomain;
   }
}
