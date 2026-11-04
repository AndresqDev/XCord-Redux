package org.codehaus.plexus.interpolation;

import java.util.List;
import org.codehaus.plexus.interpolation.fixed.FixedStringSearchInterpolator;
import org.codehaus.plexus.interpolation.fixed.InterpolationState;

public class FixedInterpolatorValueSource implements ValueSource {
   private final FixedStringSearchInterpolator fixedStringSearchInterpolator;
   private final InterpolationState errorCollector = new InterpolationState();

   public FixedInterpolatorValueSource(FixedStringSearchInterpolator var1) {
      this.fixedStringSearchInterpolator = var1;
   }

   @Override
   public Object getValue(String var1) {
      return this.fixedStringSearchInterpolator.getValue(var1, this.errorCollector);
   }

   @Override
   public List getFeedback() {
      return this.errorCollector.asList();
   }

   @Override
   public void clearFeedback() {
      this.errorCollector.clear();
   }
}
