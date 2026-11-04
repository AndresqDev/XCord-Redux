package org.apache.logging.log4j.core.layout.internal;

public interface ListChecker {
   ListChecker.NoopChecker NOOP_CHECKER = new ListChecker.NoopChecker();

   boolean check(String var1);

   public static class NoopChecker implements ListChecker {
      @Override
      public boolean check(String var1) {
         return true;
      }

      @Override
      public String toString() {
         return "";
      }
   }
}
