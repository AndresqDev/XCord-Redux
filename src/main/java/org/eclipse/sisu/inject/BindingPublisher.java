package org.eclipse.sisu.inject;

public interface BindingPublisher {
   <T> void subscribe(BindingSubscriber<T> var1);

   <T> void unsubscribe(BindingSubscriber<T> var1);

   int maxBindingRank();
}
