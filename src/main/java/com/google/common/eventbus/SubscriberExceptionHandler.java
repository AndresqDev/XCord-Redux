package com.google.common.eventbus;

@ElementTypesAreNonnullByDefault
public interface SubscriberExceptionHandler {
   void handleException(Throwable var1, SubscriberExceptionContext var2);
}
