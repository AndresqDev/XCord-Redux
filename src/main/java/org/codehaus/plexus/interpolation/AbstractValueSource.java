package org.codehaus.plexus.interpolation;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractValueSource implements ValueSource {
   private final List feedback;

   protected AbstractValueSource(boolean var1) {
      if (var1) {
         this.feedback = new ArrayList();
      } else {
         this.feedback = null;
      }
   }

   @Override
   public void clearFeedback() {
      if (this.feedback != null) {
         this.feedback.clear();
      }
   }

   @Override
   public List getFeedback() {
      return this.feedback;
   }

   protected void addFeedback(String var1) {
      this.feedback.add(var1);
   }

   protected void addFeedback(String var1, Throwable var2) {
      this.feedback.add(var1);
      this.feedback.add(var2);
   }
}
