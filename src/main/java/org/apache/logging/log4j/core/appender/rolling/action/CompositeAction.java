package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CompositeAction extends AbstractAction {
   private final Action[] actions;
   private final boolean stopOnError;

   public CompositeAction(List<Action> var1, boolean var2) {
      this.actions = new Action[var1.size()];
      var1.toArray(this.actions);
      this.stopOnError = var2;
   }

   @Override
   public void run() {
      try {
         this.execute();
      } catch (IOException var2) {
         LOGGER.warn("Exception during file rollover.", (Throwable)var2);
      }
   }

   @Override
   public boolean execute() throws IOException {
      if (this.stopOnError) {
         for (Action var12 : this.actions) {
            if (!var12.execute()) {
               return false;
            }
         }

         return true;
      } else {
         boolean var1 = true;
         IOException var2 = null;

         for (Action var6 : this.actions) {
            try {
               var1 &= var6.execute();
            } catch (IOException var8) {
               var1 = false;
               if (var2 == null) {
                  var2 = var8;
               }
            }
         }

         if (var2 != null) {
            throw var2;
         } else {
            return var1;
         }
      }
   }

   @Override
   public String toString() {
      return CompositeAction.class.getSimpleName() + Arrays.toString((Object[])this.actions);
   }

   public Action[] getActions() {
      return this.actions;
   }

   public boolean isStopOnError() {
      return this.stopOnError;
   }
}
