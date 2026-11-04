package org.apache.logging.log4j.core.layout.internal;

import java.util.List;

public class ExcludeChecker implements ListChecker {
   private final List<String> list;

   public ExcludeChecker(List<String> var1) {
      this.list = var1;
   }

   @Override
   public boolean check(String var1) {
      return !this.list.contains(var1);
   }

   @Override
   public String toString() {
      return "ThreadContextExcludes=" + this.list.toString();
   }
}
