package org.apache.logging.log4j.core.lookup;

import java.util.Map;
import java.util.Properties;

public final class RuntimeStrSubstitutor extends StrSubstitutor {
   public RuntimeStrSubstitutor() {
   }

   public RuntimeStrSubstitutor(Map<String, String> var1) {
      super(var1);
   }

   public RuntimeStrSubstitutor(Properties var1) {
      super(var1);
   }

   public RuntimeStrSubstitutor(StrLookup var1) {
      super(var1);
   }

   public RuntimeStrSubstitutor(StrSubstitutor var1) {
      super(var1);
   }

   @Override
   boolean isRecursiveEvaluationAllowed() {
      return false;
   }

   @Override
   void setRecursiveEvaluationAllowed(boolean var1) {
      throw new UnsupportedOperationException("recursiveEvaluationAllowed cannot be modified within RuntimeStrSubstitutor");
   }

   @Override
   public String toString() {
      return "RuntimeStrSubstitutor{" + super.toString() + "}";
   }
}
