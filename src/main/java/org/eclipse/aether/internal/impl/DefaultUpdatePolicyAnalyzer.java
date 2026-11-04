package org.eclipse.aether.internal.impl;

import java.util.Calendar;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.UpdatePolicyAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xcord.Named;

@Named
public class DefaultUpdatePolicyAnalyzer implements UpdatePolicyAnalyzer {
   private static final Logger LOGGER = LoggerFactory.getLogger(DefaultUpdatePolicyAnalyzer.class);

   @Override
   public String getEffectiveUpdatePolicy(RepositorySystemSession var1, String var2, String var3) {
      return this.ordinalOfUpdatePolicy(var2) < this.ordinalOfUpdatePolicy(var3) ? var2 : var3;
   }

   private int ordinalOfUpdatePolicy(String var1) {
      if ("daily".equals(var1)) {
         return 1440;
      } else if ("always".equals(var1)) {
         return 0;
      } else {
         return var1 != null && var1.startsWith("interval") ? this.getMinutes(var1) : Integer.MAX_VALUE;
      }
   }

   @Override
   public boolean isUpdatedRequired(RepositorySystemSession var1, long var2, String var4) {
      if (var4 == null) {
         var4 = "";
      }

      boolean var5;
      if ("always".equals(var4)) {
         var5 = true;
      } else if ("daily".equals(var4)) {
         Calendar var6 = Calendar.getInstance();
         var6.set(11, 0);
         var6.set(12, 0);
         var6.set(13, 0);
         var6.set(14, 0);
         var5 = var6.getTimeInMillis() > var2;
      } else if (var4.startsWith("interval")) {
         int var8 = this.getMinutes(var4);
         Calendar var7 = Calendar.getInstance();
         var7.add(12, -var8);
         var5 = var7.getTimeInMillis() > var2;
      } else {
         var5 = false;
         if (!"never".equals(var4)) {
            LOGGER.warn("Unknown repository update policy '{}', assuming '{}'", var4, "never");
         }
      }

      return var5;
   }

   private int getMinutes(String var1) {
      int var2;
      try {
         String var3 = var1.substring("interval".length() + 1);
         var2 = Integer.parseInt(var3);
      } catch (RuntimeException var4) {
         var2 = 1440;
         LOGGER.warn("Non-parseable repository update policy '{}', assuming '{}:1440'", var1, "interval");
      }

      return var2;
   }
}
