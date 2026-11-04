package io.netty.handler.ssl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import javax.security.auth.x500.X500Principal;

final class OpenSslKeyMaterialManager {
   static final String KEY_TYPE_RSA = "RSA";
   static final String KEY_TYPE_DH_RSA = "DH_RSA";
   static final String KEY_TYPE_EC = "EC";
   static final String KEY_TYPE_EC_EC = "EC_EC";
   static final String KEY_TYPE_EC_RSA = "EC_RSA";
   private static final Map<String, String> KEY_TYPES = new HashMap<>();
   private final OpenSslKeyMaterialProvider provider;

   OpenSslKeyMaterialManager(OpenSslKeyMaterialProvider var1) {
      this.provider = var1;
   }

   void setKeyMaterialServerSide(ReferenceCountedOpenSslEngine var1) throws SSLException {
      String[] var2 = var1.authMethods();
      if (var2.length == 0) {
         throw new SSLHandshakeException("Unable to find key material");
      } else {
         HashSet var3 = new HashSet(KEY_TYPES.size());

         for (String var7 : var2) {
            String var8 = KEY_TYPES.get(var7);
            if (var8 != null && var3.add(var8)) {
               String var9 = this.chooseServerAlias(var1, var8);
               if (var9 != null) {
                  this.setKeyMaterial(var1, var9);
                  return;
               }
            }
         }

         throw new SSLHandshakeException("Unable to find key material for auth method(s): " + Arrays.toString((Object[])var2));
      }
   }

   void setKeyMaterialClientSide(ReferenceCountedOpenSslEngine var1, String[] var2, X500Principal[] var3) throws SSLException {
      String var4 = this.chooseClientAlias(var1, var2, var3);
      if (var4 != null) {
         this.setKeyMaterial(var1, var4);
      }
   }

   private void setKeyMaterial(ReferenceCountedOpenSslEngine var1, String var2) throws SSLException {
      OpenSslKeyMaterial var3 = null;

      try {
         var3 = this.provider.chooseKeyMaterial(var1.alloc, var2);
         if (var3 != null) {
            var1.setKeyMaterial(var3);
            return;
         }
      } catch (SSLException var9) {
         throw var9;
      } catch (Exception var10) {
         throw new SSLException(var10);
      } finally {
         if (var3 != null) {
            var3.release();
         }
      }
   }

   private String chooseClientAlias(ReferenceCountedOpenSslEngine var1, String[] var2, X500Principal[] var3) {
      X509KeyManager var4 = this.provider.keyManager();
      return var4 instanceof X509ExtendedKeyManager
         ? ((X509ExtendedKeyManager)var4).chooseEngineClientAlias(var2, var3, var1)
         : var4.chooseClientAlias(var2, var3, null);
   }

   private String chooseServerAlias(ReferenceCountedOpenSslEngine var1, String var2) {
      X509KeyManager var3 = this.provider.keyManager();
      return var3 instanceof X509ExtendedKeyManager
         ? ((X509ExtendedKeyManager)var3).chooseEngineServerAlias(var2, null, var1)
         : var3.chooseServerAlias(var2, null, null);
   }

   static {
      KEY_TYPES.put("RSA", "RSA");
      KEY_TYPES.put("DHE_RSA", "RSA");
      KEY_TYPES.put("ECDHE_RSA", "RSA");
      KEY_TYPES.put("ECDHE_ECDSA", "EC");
      KEY_TYPES.put("ECDH_RSA", "EC_RSA");
      KEY_TYPES.put("ECDH_ECDSA", "EC_EC");
      KEY_TYPES.put("DH_RSA", "DH_RSA");
   }
}
