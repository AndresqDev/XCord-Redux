package org.apache.maven.model.path;

import xcord.Named;
import xcord.Singleton;

@Named
@Singleton
public class DefaultUrlNormalizer implements UrlNormalizer {
   @Override
   public String normalize(String var1) {
      String var2 = var1;
      if (var1 != null) {
         while (true) {
            int var3 = var2.indexOf("/../");
            if (var3 < 0) {
               break;
            }

            if (var3 == 0) {
               var2 = var2.substring(3);
            } else {
               int var4 = var3 - 1;

               while (var4 >= 0 && var2.charAt(var4) == '/') {
                  var4--;
               }

               var4 = var2.lastIndexOf(47, var4);
               if (var4 < 0) {
                  var2 = var2.substring(var3 + 4);
               } else {
                  var2 = var2.substring(0, var4) + var2.substring(var3 + 3);
               }
            }
         }
      }

      return var2;
   }
}
