package org.apache.logging.log4j.core.lookup;

import java.util.Objects;
import javax.naming.NamingException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.net.JndiManager;
import org.apache.logging.log4j.status.StatusLogger;

@Plugin(
   name = "jndi",
   category = "Lookup"
)
public class JndiLookup extends AbstractLookup {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final Marker LOOKUP = MarkerManager.getMarker("LOOKUP");
   static final String CONTAINER_JNDI_RESOURCE_PATH_PREFIX = "java:comp/env/";

   public JndiLookup() {
      if (!JndiManager.isJndiLookupEnabled()) {
         throw new IllegalStateException("JNDI must be enabled by setting log4j2.enableJndiLookup=true");
      }
   }

   @Override
   public String lookup(LogEvent var1, String var2) {
      if (var2 == null) {
         return null;
      } else {
         String var3 = this.convertJndiName(var2);

         try (JndiManager var4 = JndiManager.getDefaultManager()) {
            return Objects.toString(var4.lookup(var3), null);
         } catch (NamingException var18) {
            LOGGER.warn(LOOKUP, "Error looking up JNDI resource [{}].", var3, var18);
            return null;
         }
      }
   }

   private String convertJndiName(String var1) {
      return !var1.startsWith("java:comp/env/") && var1.indexOf(58) == -1 ? "java:comp/env/" + var1 : var1;
   }
}
