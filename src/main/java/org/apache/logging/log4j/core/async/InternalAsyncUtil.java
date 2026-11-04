package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.apache.logging.log4j.message.Message;

public class InternalAsyncUtil {
   public static Message makeMessageImmutable(Message var0) {
      if (var0 != null && !canFormatMessageInBackground(var0)) {
         var0.getFormattedMessage();
      }

      return var0;
   }

   private static boolean canFormatMessageInBackground(Message var0) {
      return Constants.FORMAT_MESSAGES_IN_BACKGROUND || var0.getClass().isAnnotationPresent(AsynchronouslyFormattable.class);
   }
}
