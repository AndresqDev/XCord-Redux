package org.codehaus.plexus.interpolation;

import java.util.List;

public interface ValueSource {
   Object getValue(String var1);

   List getFeedback();

   void clearFeedback();
}
