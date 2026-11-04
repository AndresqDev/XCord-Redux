package org.apache.logging.log4j.core.util;

import java.net.URLConnection;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Base64Util;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public class BasicAuthorizationProvider implements AuthorizationProvider {
   private static final String[] PREFIXES = new String[]{"log4j2.config.", "logging.auth."};
   private static final String AUTH_USER_NAME = "username";
   private static final String AUTH_PASSWORD = "password";
   private static final String AUTH_PASSWORD_DECRYPTOR = "passwordDecryptor";
   public static final String CONFIG_USER_NAME = "log4j2.configurationUserName";
   public static final String CONFIG_PASSWORD = "log4j2.configurationPassword";
   public static final String PASSWORD_DECRYPTOR = "log4j2.passwordDecryptor";
   private static Logger LOGGER = StatusLogger.getLogger();
   private String authString = null;

   public BasicAuthorizationProvider(PropertiesUtil var1) {
      String var2 = var1.getStringProperty(PREFIXES, "username", () -> var1.getStringProperty("log4j2.configurationUserName"));
      String var3 = var1.getStringProperty(PREFIXES, "password", () -> var1.getStringProperty("log4j2.configurationPassword"));
      String var4 = var1.getStringProperty(PREFIXES, "passwordDecryptor", () -> var1.getStringProperty("log4j2.passwordDecryptor"));
      if (var4 != null) {
         try {
            Object var5 = LoaderUtil.newInstanceOf(var4);
            if (var5 instanceof PasswordDecryptor) {
               var3 = ((PasswordDecryptor)var5).decryptPassword(var3);
            }
         } catch (Exception var6) {
            LOGGER.warn("Unable to decrypt password.", (Throwable)var6);
         }
      }

      if (var2 != null && var3 != null) {
         this.authString = "Basic " + Base64Util.encode(var2 + ":" + var3);
      }
   }

   @Override
   public void addAuthorization(URLConnection var1) {
      if (this.authString != null) {
         var1.setRequestProperty("Authorization", this.authString);
      }
   }
}
