package org.apache.logging.log4j.core.parser;

public class ParseException extends Exception {
   private static final long serialVersionUID = -2739649998196663857L;

   public ParseException(String var1) {
      super(var1);
   }

   public ParseException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public ParseException(Throwable var1) {
      super(var1);
   }
}
