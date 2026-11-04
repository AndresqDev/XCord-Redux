package org.apache.logging.slf4j;

public class SLF4JLoggingException extends RuntimeException {
   private static final long serialVersionUID = -1618650972455089998L;

   public SLF4JLoggingException(String var1) {
      super(var1);
   }

   public SLF4JLoggingException(String var1, Exception var2) {
      super(var1, var2);
   }

   public SLF4JLoggingException(Exception var1) {
      super(var1);
   }
}
