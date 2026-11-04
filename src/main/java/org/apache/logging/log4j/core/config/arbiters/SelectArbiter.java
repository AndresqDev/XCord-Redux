package org.apache.logging.log4j.core.config.arbiters;

import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(
   name = "Select",
   category = "Core",
   elementType = "Arbiter",
   deferChildren = true,
   printObject = true
)
public class SelectArbiter {
   public Arbiter evaluateConditions(List<Arbiter> var1) {
      Optional var2 = var1.stream().filter(var0 -> var0 instanceof DefaultArbiter).reduce((var0, var1x) -> {
         throw new IllegalStateException("Multiple elements: " + var0 + ", " + var1x);
      });

      for (Arbiter var4 : var1) {
         if (!(var4 instanceof DefaultArbiter) && var4.isCondition()) {
            return var4;
         }
      }

      return (Arbiter)var2.orElse(null);
   }

   @PluginBuilderFactory
   public static SelectArbiter.Builder newBuilder() {
      return new SelectArbiter.Builder();
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<SelectArbiter> {
      public SelectArbiter.Builder asBuilder() {
         return this;
      }

      public SelectArbiter build() {
         return new SelectArbiter();
      }
   }
}
