package org.apache.maven.artifact.repository;

public class Authentication {
   private String privateKey;
   private String passphrase;
   private String username;
   private String password;

   public Authentication(String var1, String var2) {
      this.username = var1;
      this.password = var2;
   }

   public String getPassword() {
      return this.password;
   }

   public void setPassword(String var1) {
      this.password = var1;
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String var1) {
      this.username = var1;
   }

   public String getPassphrase() {
      return this.passphrase;
   }

   public void setPassphrase(String var1) {
      this.passphrase = var1;
   }

   public String getPrivateKey() {
      return this.privateKey;
   }

   public void setPrivateKey(String var1) {
      this.privateKey = var1;
   }
}
