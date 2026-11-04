package org.apache.logging.log4j.core.lookup;

import java.util.Map;
import java.util.Properties;

public final class ConfigurationStrSubstitutor extends StrSubstitutor {
   public ConfigurationStrSubstitutor() {
   }

   public ConfigurationStrSubstitutor(Map<String, String> var1) {
      super(var1);
   }

   public ConfigurationStrSubstitutor(Properties var1) {
      super(var1);
   }

   public ConfigurationStrSubstitutor(StrLookup var1) {
      super(var1);
   }

   public ConfigurationStrSubstitutor(StrSubstitutor var1) {
      super(var1);
   }

   @Override
   boolean isRecursiveEvaluationAllowed() {
      return true;
   }

   @Override
   void setRecursiveEvaluationAllowed(boolean var1) {
      throw new UnsupportedOperationException("recursiveEvaluationAllowed cannot be modified within ConfigurationStrSubstitutor");
   }

   @Override
   public String toString() {
      return "ConfigurationStrSubstitutor{" + super.toString() + "}";
   }
}
