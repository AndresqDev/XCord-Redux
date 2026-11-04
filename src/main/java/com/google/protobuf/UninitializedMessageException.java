package com.google.protobuf;

import java.util.Collections;
import java.util.List;

public class UninitializedMessageException extends RuntimeException {
   private static final long serialVersionUID = -7466929953374883507L;
   private final List<String> missingFields;

   public UninitializedMessageException(MessageLite var1) {
      super("Message was missing required fields.  (Lite runtime could not determine which fields were missing).");
      this.missingFields = null;
   }

   public UninitializedMessageException(List<String> var1) {
      super(buildDescription(var1));
      this.missingFields = var1;
   }

   public List<String> getMissingFields() {
      return Collections.unmodifiableList(this.missingFields);
   }

   public InvalidProtocolBufferException asInvalidProtocolBufferException() {
      return new InvalidProtocolBufferException(this.getMessage());
   }

   private static String buildDescription(List<String> var0) {
      StringBuilder var1 = new StringBuilder("Message missing required fields: ");
      boolean var2 = true;

      for (String var4 : var0) {
         if (var2) {
            var2 = false;
         } else {
            var1.append(", ");
         }

         var1.append(var4);
      }

      return var1.toString();
   }
}
