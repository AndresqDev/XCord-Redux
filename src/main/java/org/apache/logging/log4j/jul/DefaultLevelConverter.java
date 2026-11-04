package org.apache.logging.log4j.jul;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

public class DefaultLevelConverter implements LevelConverter {
   private final ConcurrentMap<Level, org.apache.logging.log4j.Level> julToLog4j = new ConcurrentHashMap<>(9);
   private final Map<org.apache.logging.log4j.Level, Level> log4jToJul = new IdentityHashMap<>(10);
   private final List<Level> sortedJulLevels = new ArrayList<>(9);

   public DefaultLevelConverter() {
      this.mapJulToLog4j(Level.ALL, org.apache.logging.log4j.Level.ALL);
      this.mapJulToLog4j(Level.FINEST, LevelTranslator.FINEST);
      this.mapJulToLog4j(Level.FINER, org.apache.logging.log4j.Level.TRACE);
      this.mapJulToLog4j(Level.FINE, org.apache.logging.log4j.Level.DEBUG);
      this.mapJulToLog4j(Level.CONFIG, LevelTranslator.CONFIG);
      this.mapJulToLog4j(Level.INFO, org.apache.logging.log4j.Level.INFO);
      this.mapJulToLog4j(Level.WARNING, org.apache.logging.log4j.Level.WARN);
      this.mapJulToLog4j(Level.SEVERE, org.apache.logging.log4j.Level.ERROR);
      this.mapJulToLog4j(Level.OFF, org.apache.logging.log4j.Level.OFF);
      this.mapLog4jToJul(org.apache.logging.log4j.Level.ALL, Level.ALL);
      this.mapLog4jToJul(LevelTranslator.FINEST, Level.FINEST);
      this.mapLog4jToJul(org.apache.logging.log4j.Level.TRACE, Level.FINER);
      this.mapLog4jToJul(org.apache.logging.log4j.Level.DEBUG, Level.FINE);
      this.mapLog4jToJul(LevelTranslator.CONFIG, Level.CONFIG);
      this.mapLog4jToJul(org.apache.logging.log4j.Level.INFO, Level.INFO);
      this.mapLog4jToJul(org.apache.logging.log4j.Level.WARN, Level.WARNING);
      this.mapLog4jToJul(org.apache.logging.log4j.Level.ERROR, Level.SEVERE);
      this.mapLog4jToJul(org.apache.logging.log4j.Level.FATAL, Level.SEVERE);
      this.mapLog4jToJul(org.apache.logging.log4j.Level.OFF, Level.OFF);
      this.sortedJulLevels.addAll(this.julToLog4j.keySet());
      Collections.sort(this.sortedJulLevels, new DefaultLevelConverter.JulLevelComparator());
   }

   private long distance(Level var1, Level var2) {
      return Math.abs((long)var2.intValue() - (long)var1.intValue());
   }

   private void mapJulToLog4j(Level var1, org.apache.logging.log4j.Level var2) {
      this.julToLog4j.put(var1, var2);
   }

   private void mapLog4jToJul(org.apache.logging.log4j.Level var1, Level var2) {
      this.log4jToJul.put(var1, var2);
   }

   private org.apache.logging.log4j.Level nearestLevel(Level var1) {
      long var2 = Long.MAX_VALUE;
      Level var4 = null;

      for (Level var6 : this.sortedJulLevels) {
         long var7 = this.distance(var1, var6);
         if (var7 > var2) {
            return this.julToLog4j.get(var4);
         }

         var2 = var7;
         var4 = var6;
      }

      return this.julToLog4j.get(var4);
   }

   @Override
   public Level toJavaLevel(org.apache.logging.log4j.Level var1) {
      return this.log4jToJul.get(var1);
   }

   @Override
   public org.apache.logging.log4j.Level toLevel(Level var1) {
      if (var1 == null) {
         return null;
      } else {
         org.apache.logging.log4j.Level var2 = this.julToLog4j.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            org.apache.logging.log4j.Level var3 = this.nearestLevel(var1);
            this.julToLog4j.put(var1, var3);
            return var3;
         }
      }
   }

   static final class JulLevelComparator implements Serializable, Comparator<Level> {
      private static final long serialVersionUID = 1L;

      public int compare(Level var1, Level var2) {
         return Integer.compare(var1.intValue(), var2.intValue());
      }
   }
}
