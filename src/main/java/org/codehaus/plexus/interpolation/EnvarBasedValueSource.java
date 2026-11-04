package org.codehaus.plexus.interpolation;

import java.io.IOException;
import java.util.Properties;
import org.codehaus.plexus.interpolation.os.OperatingSystemUtils;

public class EnvarBasedValueSource extends AbstractValueSource {
   private static Properties envarsCaseSensitive;
   private static Properties envarsCaseInsensitive;
   private final Properties envars;
   private final boolean caseSensitive;

   public EnvarBasedValueSource() throws IOException {
      this(true);
   }

   public EnvarBasedValueSource(boolean var1) throws IOException {
      super(false);
      this.caseSensitive = var1;
      this.envars = getEnvars(var1);
   }

   private static synchronized Properties getEnvars(boolean var0) throws IOException {
      if (var0) {
         if (envarsCaseSensitive == null) {
            envarsCaseSensitive = OperatingSystemUtils.getSystemEnvVars(var0);
         }

         return envarsCaseSensitive;
      } else {
         if (envarsCaseInsensitive == null) {
            envarsCaseInsensitive = OperatingSystemUtils.getSystemEnvVars(var0);
         }

         return envarsCaseInsensitive;
      }
   }

   @Override
   public Object getValue(String var1) {
      String var2 = var1;
      if (var1.startsWith("env.")) {
         var2 = var1.substring("env.".length());
      }

      if (!this.caseSensitive) {
         var2 = var2.toUpperCase();
      }

      return this.envars.getProperty(var2);
   }

   static void resetStatics() {
      envarsCaseSensitive = null;
      envarsCaseInsensitive = null;
   }
}
