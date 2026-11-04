package com.lmax.disruptor.dsl;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.ExceptionHandler;

public class ExceptionHandlerSetting<T> {
   private final EventHandler<T> eventHandler;
   private final ConsumerRepository<T> consumerRepository;

   ExceptionHandlerSetting(EventHandler<T> var1, ConsumerRepository<T> var2) {
      this.eventHandler = var1;
      this.consumerRepository = var2;
   }

   public void with(ExceptionHandler<? super T> var1) {
      EventProcessor var2 = this.consumerRepository.getEventProcessorFor(this.eventHandler);
      if (var2 instanceof BatchEventProcessor) {
         ((BatchEventProcessor)var2).setExceptionHandler(var1);
         this.consumerRepository.getBarrierFor(this.eventHandler).alert();
      } else {
         throw new RuntimeException("EventProcessor: " + var2 + " is not a BatchEventProcessor " + "and does not support exception handlers");
      }
   }
}
