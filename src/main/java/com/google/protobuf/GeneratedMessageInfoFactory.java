package com.google.protobuf;

class GeneratedMessageInfoFactory implements MessageInfoFactory {
   private static final GeneratedMessageInfoFactory instance = new GeneratedMessageInfoFactory();

   private GeneratedMessageInfoFactory() {
   }

   public static GeneratedMessageInfoFactory getInstance() {
      return instance;
   }

   @Override
   public boolean isSupported(Class<?> var1) {
      return GeneratedMessageLite.class.isAssignableFrom(var1);
   }

   @Override
   public MessageInfo messageInfoFor(Class<?> var1) {
      if (!GeneratedMessageLite.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException("Unsupported message type: " + var1.getName());
      } else {
         try {
            return (MessageInfo)GeneratedMessageLite.getDefaultInstance(var1.asSubclass(GeneratedMessageLite.class)).buildMessageInfo();
         } catch (Exception var3) {
            throw new RuntimeException("Unable to get message info for " + var1.getName(), var3);
         }
      }
   }
}
