package org.codehaus.plexus.interpolation;

import java.util.List;

public interface Interpolator extends BasicInterpolator {
   void addValueSource(ValueSource var1);

   void removeValuesSource(ValueSource var1);

   void addPostProcessor(InterpolationPostProcessor var1);

   void removePostProcessor(InterpolationPostProcessor var1);

   String interpolate(String var1, String var2) throws InterpolationException;

   String interpolate(String var1, String var2, RecursionInterceptor var3) throws InterpolationException;

   List getFeedback();

   void clearFeedback();

   boolean isCacheAnswers();

   void setCacheAnswers(boolean var1);

   void clearAnswers();
}
