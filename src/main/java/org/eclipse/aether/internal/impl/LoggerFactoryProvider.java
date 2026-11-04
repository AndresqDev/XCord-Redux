package org.eclipse.aether.internal.impl;

import org.eclipse.aether.spi.log.LoggerFactory;
import xcord.Inject;
import xcord.Named;
import xcord.Provider;
import xcord.Singleton;

@Named
@Singleton
public class LoggerFactoryProvider implements Provider<LoggerFactory> {
   @Inject
   @Named("slf4j")
   private Provider<LoggerFactory> slf4j;

   public LoggerFactory get() {
      return (LoggerFactory)this.slf4j.get();
   }
}
