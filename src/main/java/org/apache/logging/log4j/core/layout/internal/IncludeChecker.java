package org.apache.logging.log4j.core.layout.internal;

import java.util.List;

public class IncludeChecker implements ListChecker {
   private final List<String> list;

   public IncludeChecker(List<String> var1) {
      this.list = var1;
   }

   @Override
   public boolean check(String var1) {
      return this.list.contains(var1);
   }

   @Override
   public String toString() {
      return "ThreadContextIncludes=" + this.list.toString();
   }
}
