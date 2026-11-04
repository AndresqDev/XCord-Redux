package org.apache.maven.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class InputLocation implements Serializable, Cloneable, InputLocationTracker {
   private int lineNumber = -1;
   private int columnNumber = -1;
   private InputSource source;
   private Map<Object, InputLocation> locations;
   private InputLocation location;

   public InputLocation(int var1, int var2) {
      this.lineNumber = var1;
      this.columnNumber = var2;
   }

   public InputLocation(int var1, int var2, InputSource var3) {
      this.lineNumber = var1;
      this.columnNumber = var2;
      this.source = var3;
   }

   public InputLocation clone() {
      try {
         InputLocation var1 = (InputLocation)super.clone();
         if (var1.locations != null) {
            var1.locations = new LinkedHashMap<>(var1.locations);
         }

         return var1;
      } catch (Exception var2) {
         throw (RuntimeException)new UnsupportedOperationException(this.getClass().getName() + " does not support clone()").initCause(var2);
      }
   }

   public int getColumnNumber() {
      return this.columnNumber;
   }

   public int getLineNumber() {
      return this.lineNumber;
   }

   @Override
   public InputLocation getLocation(Object var1) {
      if (var1 instanceof String) {
         String var2 = (String)var1;
         byte var3 = -1;
         switch (var2.hashCode()) {
            case 0:
               if (var2.equals("")) {
                  var3 = 0;
               }
            default:
               switch (var3) {
                  case 0:
                     return this.location;
                  default:
                     return this.getOtherLocation(var1);
               }
         }
      } else {
         return this.getOtherLocation(var1);
      }
   }

   public Map<Object, InputLocation> getLocations() {
      return this.locations;
   }

   @Override
   public void setLocation(Object var1, InputLocation var2) {
      if (var1 instanceof String) {
         String var3 = (String)var1;
         byte var4 = -1;
         switch (var3.hashCode()) {
            case 0:
               if (var3.equals("")) {
                  var4 = 0;
               }
            default:
               switch (var4) {
                  case 0:
                     this.location = var2;
                     return;
                  default:
                     this.setOtherLocation(var1, var2);
               }
         }
      } else {
         this.setOtherLocation(var1, var2);
      }
   }

   public void setOtherLocation(Object var1, InputLocation var2) {
      if (var2 != null) {
         if (this.locations == null) {
            this.locations = new LinkedHashMap<>();
         }

         this.locations.put(var1, var2);
      }
   }

   private InputLocation getOtherLocation(Object var1) {
      return this.locations != null ? this.locations.get(var1) : null;
   }

   public InputSource getSource() {
      return this.source;
   }

   public static InputLocation merge(InputLocation var0, InputLocation var1, boolean var2) {
      if (var1 == null) {
         return var0;
      } else if (var0 == null) {
         return var1;
      } else {
         InputLocation var3 = new InputLocation(var0.getLineNumber(), var0.getColumnNumber(), var0.getSource());
         Map var5 = var1.getLocations();
         Map var6 = var0.getLocations();
         Object var4;
         if (var5 == null) {
            var4 = var6;
         } else if (var6 == null) {
            var4 = var5;
         } else {
            var4 = new LinkedHashMap();
            var4.putAll(var2 ? var6 : var5);
            var4.putAll(var2 ? var5 : var6);
         }

         var3.setLocations((Map<Object, InputLocation>)var4);
         return var3;
      }
   }

   public static InputLocation merge(InputLocation var0, InputLocation var1, Collection<Integer> var2) {
      if (var1 == null) {
         return var0;
      } else if (var0 == null) {
         return var1;
      } else {
         InputLocation var3 = new InputLocation(var0.getLineNumber(), var0.getColumnNumber(), var0.getSource());
         Map var5 = var1.getLocations();
         Map var6 = var0.getLocations();
         Object var4;
         if (var5 == null) {
            var4 = var6;
         } else if (var6 == null) {
            var4 = var5;
         } else {
            var4 = new LinkedHashMap();

            for (Integer var9 : var2) {
               InputLocation var8;
               if (var9 < 0) {
                  var8 = (InputLocation)var5.get(~var9);
               } else {
                  var8 = (InputLocation)var6.get(var9);
               }

               var4.put(var4.size(), var8);
            }
         }

         var3.setLocations((Map<Object, InputLocation>)var4);
         return var3;
      }
   }

   public void setLocations(Map<Object, InputLocation> var1) {
      this.locations = var1;
   }

   @Override
   public String toString() {
      return this.getLineNumber() + " : " + this.getColumnNumber() + ", " + this.getSource();
   }

   public abstract static class StringFormatter {
      public abstract String toString(InputLocation var1);
   }
}
