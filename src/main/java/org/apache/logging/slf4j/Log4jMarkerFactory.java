package org.apache.logging.slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.status.StatusLogger;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;

public class Log4jMarkerFactory implements IMarkerFactory {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final ConcurrentMap<String, Marker> markerMap = new ConcurrentHashMap<>();

   @Override
   public Marker getMarker(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Marker name must not be null");
      } else {
         Marker var2 = this.markerMap.get(var1);
         if (var2 != null) {
            return var2;
         } else {
            org.apache.logging.log4j.Marker var3 = MarkerManager.getMarker(var1);
            return this.addMarkerIfAbsent(var1, var3);
         }
      }
   }

   private Marker addMarkerIfAbsent(String var1, org.apache.logging.log4j.Marker var2) {
      Log4jMarker var3 = new Log4jMarker(var2);
      Marker var4 = this.markerMap.putIfAbsent(var1, var3);
      return (Marker)(var4 == null ? var3 : var4);
   }

   public Marker getMarker(Marker var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Marker must not be null");
      } else {
         Marker var2 = this.markerMap.get(var1.getName());
         return var2 != null ? var2 : this.addMarkerIfAbsent(var1.getName(), convertMarker(var1));
      }
   }

   private static org.apache.logging.log4j.Marker convertMarker(Marker var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("Marker must not be null");
      } else {
         return convertMarker(var0, new ArrayList<>());
      }
   }

   private static org.apache.logging.log4j.Marker convertMarker(Marker var0, Collection<Marker> var1) {
      org.apache.logging.log4j.Marker var2 = MarkerManager.getMarker(var0.getName());
      if (var0.hasReferences()) {
         for (Marker var4 : var0) {
            if (var1.contains(var4)) {
               LOGGER.warn("Found a cycle in Marker [{}]. Cycle will be broken.", var4.getName());
            } else {
               var1.add(var4);
               var2.addParents(convertMarker(var4, var1));
            }
         }
      }

      return var2;
   }

   @Override
   public boolean exists(String var1) {
      return this.markerMap.containsKey(var1);
   }

   @Override
   public boolean detachMarker(String var1) {
      return false;
   }

   @Override
   public Marker getDetachedMarker(String var1) {
      LOGGER.warn("Log4j does not support detached Markers. Returned Marker [{}] will be unchanged.", var1);
      return this.getMarker(var1);
   }
}
