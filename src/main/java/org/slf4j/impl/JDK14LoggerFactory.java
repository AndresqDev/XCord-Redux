package org.slf4j.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class JDK14LoggerFactory implements ILoggerFactory {
   ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<>();

   public JDK14LoggerFactory() {
      java.util.logging.Logger.getLogger("");
   }

   @Override
   public Logger getLogger(String var1) {
      if (var1.equalsIgnoreCase("ROOT")) {
         var1 = "";
      }

      Logger var2 = this.loggerMap.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         java.util.logging.Logger var3 = java.util.logging.Logger.getLogger(var1);
         JDK14LoggerAdapter var4 = new JDK14LoggerAdapter(var3);
         Logger var5 = this.loggerMap.putIfAbsent(var1, var4);
         return (Logger)(var5 == null ? var4 : var5);
      }
   }
}
