package com.google.common.eventbus;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

@ElementTypesAreNonnullByDefault
public class EventBus {
   private static final Logger logger = Logger.getLogger(EventBus.class.getName());
   private final String identifier;
   private final Executor executor;
   private final SubscriberExceptionHandler exceptionHandler;
   private final SubscriberRegistry subscribers = new SubscriberRegistry(this);
   private final Dispatcher dispatcher;

   public EventBus() {
      this("default");
   }

   public EventBus(String var1) {
      this(var1, MoreExecutors.directExecutor(), Dispatcher.perThreadDispatchQueue(), EventBus.LoggingHandler.INSTANCE);
   }

   public EventBus(SubscriberExceptionHandler var1) {
      this("default", MoreExecutors.directExecutor(), Dispatcher.perThreadDispatchQueue(), var1);
   }

   EventBus(String var1, Executor var2, Dispatcher var3, SubscriberExceptionHandler var4) {
      this.identifier = Preconditions.checkNotNull(var1);
      this.executor = Preconditions.checkNotNull(var2);
      this.dispatcher = Preconditions.checkNotNull(var3);
      this.exceptionHandler = Preconditions.checkNotNull(var4);
   }

   public final String identifier() {
      return this.identifier;
   }

   final Executor executor() {
      return this.executor;
   }

   void handleSubscriberException(Throwable var1, SubscriberExceptionContext var2) {
      Preconditions.checkNotNull(var1);
      Preconditions.checkNotNull(var2);

      try {
         this.exceptionHandler.handleException(var1, var2);
      } catch (Throwable var4) {
         logger.log(Level.SEVERE, String.format(Locale.ROOT, "Exception %s thrown while handling exception: %s", var4, var1), var4);
      }
   }

   public void register(Object var1) {
      this.subscribers.register(var1);
   }

   public void unregister(Object var1) {
      this.subscribers.unregister(var1);
   }

   public void post(Object var1) {
      Iterator var2 = this.subscribers.getSubscribers(var1);
      if (var2.hasNext()) {
         this.dispatcher.dispatch(var1, var2);
      } else if (!(var1 instanceof DeadEvent)) {
         this.post(new DeadEvent(this, var1));
      }
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).addValue(this.identifier).toString();
   }

   static final class LoggingHandler implements SubscriberExceptionHandler {
      static final EventBus.LoggingHandler INSTANCE = new EventBus.LoggingHandler();

      @Override
      public void handleException(Throwable var1, SubscriberExceptionContext var2) {
         Logger var3 = logger(var2);
         if (var3.isLoggable(Level.SEVERE)) {
            var3.log(Level.SEVERE, message(var2), var1);
         }
      }

      private static Logger logger(SubscriberExceptionContext var0) {
         String var1 = EventBus.class.getName();
         String var2 = var0.getEventBus().identifier();
         return Logger.getLogger(
            new StringBuilder(1 + String.valueOf(var1).length() + String.valueOf(var2).length()).append(var1).append(".").append(var2).toString()
         );
      }

      private static String message(SubscriberExceptionContext var0) {
         Method var1 = var0.getSubscriberMethod();
         String var2 = var1.getName();
         String var3 = var1.getParameterTypes()[0].getName();
         String var4 = String.valueOf(var0.getSubscriber());
         String var5 = String.valueOf(var0.getEvent());
         return new StringBuilder(
               80 + String.valueOf(var2).length() + String.valueOf(var3).length() + String.valueOf(var4).length() + String.valueOf(var5).length()
            )
            .append("Exception thrown by subscriber method ")
            .append(var2)
            .append('(')
            .append(var3)
            .append(')')
            .append(" on subscriber ")
            .append(var4)
            .append(" when dispatching event: ")
            .append(var5)
            .toString();
      }
   }
}
