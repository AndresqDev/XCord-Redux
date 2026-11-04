package org.apache.logging.log4j.core.lookup;

public abstract class AbstractLookup implements StrLookup {
   @Override
   public String lookup(String var1) {
      return this.lookup(null, var1);
   }
}
