package org.slf4j.impl;

import org.apache.logging.slf4j.Log4jMDCAdapter;
import org.slf4j.spi.MDCAdapter;

public final class StaticMDCBinder {
   public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

   private StaticMDCBinder() {
   }

   public static StaticMDCBinder getSingleton() {
      return SINGLETON;
   }

   public MDCAdapter getMDCA() {
      return new Log4jMDCAdapter();
   }

   public String getMDCAdapterClassStr() {
      return Log4jMDCAdapter.class.getName();
   }
}
