package org.apache.logging.log4j.status;

import java.io.IOException;
import java.io.PrintStream;
import org.apache.logging.log4j.Level;

public class StatusConsoleListener implements StatusListener {
   private Level level = Level.FATAL;
   private String[] filters;
   private final PrintStream stream;

   public StatusConsoleListener(Level var1) {
      this(var1, System.out);
   }

   public StatusConsoleListener(Level var1, PrintStream var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("You must provide a stream to use for this listener.");
      } else {
         this.level = var1;
         this.stream = var2;
      }
   }

   public void setLevel(Level var1) {
      this.level = var1;
   }

   @Override
   public Level getStatusLevel() {
      return this.level;
   }

   @Override
   public void log(StatusData var1) {
      if (!this.filtered(var1)) {
         this.stream.println(var1.getFormattedStatus());
      }
   }

   public void setFilters(String... var1) {
      this.filters = var1;
   }

   private boolean filtered(StatusData var1) {
      if (this.filters == null) {
         return false;
      } else {
         String var2 = var1.getStackTraceElement().getClassName();

         for (String var6 : this.filters) {
            if (var2.startsWith(var6)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public void close() throws IOException {
      if (this.stream != System.out && this.stream != System.err) {
         this.stream.close();
      }
   }
}
