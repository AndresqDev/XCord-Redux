package org.apache.logging.slf4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import org.apache.logging.log4j.MarkerManager;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;
import org.slf4j.impl.StaticMarkerBinder;

public class Log4jMarker implements Marker {
   public static final long serialVersionUID = 1590472L;
   private final IMarkerFactory factory = StaticMarkerBinder.SINGLETON.getMarkerFactory();
   private final org.apache.logging.log4j.Marker marker;

   public Log4jMarker(org.apache.logging.log4j.Marker var1) {
      this.marker = var1;
   }

   @Override
   public void add(Marker var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         Marker var2 = this.factory.getMarker(var1.getName());
         this.marker.addParents(((Log4jMarker)var2).getLog4jMarker());
      }
   }

   @Override
   public boolean contains(Marker var1) {
      if (var1 == null) {
         throw new IllegalArgumentException();
      } else {
         return this.marker.isInstanceOf(var1.getName());
      }
   }

   @Override
   public boolean contains(String var1) {
      return var1 != null ? this.marker.isInstanceOf(var1) : false;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 == null) {
         return false;
      } else if (!(var1 instanceof Log4jMarker)) {
         return false;
      } else {
         Log4jMarker var2 = (Log4jMarker)var1;
         return Objects.equals(this.marker, var2.marker);
      }
   }

   public org.apache.logging.log4j.Marker getLog4jMarker() {
      return this.marker;
   }

   @Override
   public String getName() {
      return this.marker.getName();
   }

   @Override
   public boolean hasChildren() {
      return this.marker.hasParents();
   }

   @Override
   public int hashCode() {
      return 31 + Objects.hashCode(this.marker);
   }

   @Override
   public boolean hasReferences() {
      return this.marker.hasParents();
   }

   @Override
   public Iterator<Marker> iterator() {
      org.apache.logging.log4j.Marker[] var1 = this.marker.getParents();
      ArrayList var2 = new ArrayList(var1.length);

      for (org.apache.logging.log4j.Marker var6 : var1) {
         var2.add(this.factory.getMarker(var6.getName()));
      }

      return var2.iterator();
   }

   @Override
   public boolean remove(Marker var1) {
      return var1 != null ? this.marker.remove(MarkerManager.getMarker(var1.getName())) : false;
   }
}
