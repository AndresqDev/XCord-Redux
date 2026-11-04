package org.apache.commons.logging.impl;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class SLF4JLogFactory extends LogFactory {
   ConcurrentMap<String, Log> loggerMap;
   public static final String LOG_PROPERTY = "org.apache.commons.logging.Log";
   protected Hashtable attributes = new Hashtable();

   public SLF4JLogFactory() {
      this.loggerMap = new ConcurrentHashMap<>();
   }

   @Override
   public Object getAttribute(String var1) {
      return this.attributes.get(var1);
   }

   @Override
   public String[] getAttributeNames() {
      ArrayList var1 = new ArrayList();
      Enumeration var2 = this.attributes.keys();

      while (var2.hasMoreElements()) {
         var1.add((String)var2.nextElement());
      }

      String[] var3 = new String[var1.size()];

      for (int var4 = 0; var4 < var3.length; var4++) {
         var3[var4] = (String)var1.get(var4);
      }

      return var3;
   }

   @Override
   public Log getInstance(Class var1) throws LogConfigurationException {
      return this.getInstance(var1.getName());
   }

   @Override
   public Log getInstance(String var1) throws LogConfigurationException {
      Log var2 = this.loggerMap.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         Logger var4 = LoggerFactory.getLogger(var1);
         Object var3;
         if (var4 instanceof LocationAwareLogger) {
            var3 = new SLF4JLocationAwareLog((LocationAwareLogger)var4);
         } else {
            var3 = new SLF4JLog(var4);
         }

         Log var5 = this.loggerMap.putIfAbsent(var1, (Log)var3);
         return (Log)(var5 == null ? var3 : var5);
      }
   }

   @Override
   public void release() {
      System.out.println("WARN: The method " + SLF4JLogFactory.class + "#release() was invoked.");
      System.out.println("WARN: Please see http://www.slf4j.org/codes.html#release for an explanation.");
      System.out.flush();
   }

   @Override
   public void removeAttribute(String var1) {
      this.attributes.remove(var1);
   }

   @Override
   public void setAttribute(String var1, Object var2) {
      if (var2 == null) {
         this.attributes.remove(var1);
      } else {
         this.attributes.put(var1, var2);
      }
   }
}
