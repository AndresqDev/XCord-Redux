package org.apache.logging.log4j.message;

import java.util.Map;
import org.apache.logging.log4j.util.PerformanceSensitive;

@AsynchronouslyFormattable
@PerformanceSensitive({"allocation"})
public class StringMapMessage extends MapMessage<StringMapMessage, String> {
   private static final long serialVersionUID = 1L;

   public StringMapMessage() {
   }

   public StringMapMessage(int var1) {
      super(var1);
   }

   public StringMapMessage(Map<String, String> var1) {
      super(var1);
   }

   public StringMapMessage newInstance(Map<String, String> var1) {
      return new StringMapMessage(var1);
   }
}
