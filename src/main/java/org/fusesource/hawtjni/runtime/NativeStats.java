package org.fusesource.hawtjni.runtime;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

public class NativeStats {
   private final HashMap<NativeStats.StatsInterface, ArrayList<NativeStats.NativeFunction>> snapshot;

   public NativeStats(NativeStats.StatsInterface... var1) {
      this(Arrays.asList(var1));
   }

   public NativeStats(Collection<NativeStats.StatsInterface> var1) {
      this(snapshot(var1));
   }

   private NativeStats(HashMap<NativeStats.StatsInterface, ArrayList<NativeStats.NativeFunction>> var1) {
      this.snapshot = var1;
   }

   public void reset() {
      for (ArrayList var2 : this.snapshot.values()) {
         for (NativeStats.NativeFunction var4 : var2) {
            var4.reset();
         }
      }
   }

   public void update() {
      for (Entry var2 : this.snapshot.entrySet()) {
         NativeStats.StatsInterface var3 = (NativeStats.StatsInterface)var2.getKey();

         for (NativeStats.NativeFunction var5 : (ArrayList)var2.getValue()) {
            var5.setCounter(var3.functionCounter(var5.getOrdinal()));
         }
      }
   }

   public NativeStats snapshot() {
      NativeStats var1 = this.copy();
      var1.update();
      return var1;
   }

   public NativeStats copy() {
      HashMap var1 = new HashMap(this.snapshot.size() * 2);

      for (Entry var3 : this.snapshot.entrySet()) {
         ArrayList var4 = new ArrayList(((ArrayList)var3.getValue()).size());

         for (NativeStats.NativeFunction var6 : (ArrayList)var3.getValue()) {
            var4.add(var6.copy());
         }

         var1.put(var3.getKey(), var4);
      }

      return new NativeStats(var1);
   }

   public NativeStats diff() {
      HashMap var1 = new HashMap(this.snapshot.size() * 2);

      for (Entry var3 : this.snapshot.entrySet()) {
         NativeStats.StatsInterface var4 = (NativeStats.StatsInterface)var3.getKey();
         ArrayList var5 = new ArrayList(((ArrayList)var3.getValue()).size());

         for (NativeStats.NativeFunction var7 : (ArrayList)var3.getValue()) {
            NativeStats.NativeFunction var8 = var7.copy();
            var8.setCounter(var4.functionCounter(var8.getOrdinal()));
            var8.subtract(var7);
            var5.add(var8);
         }

         var1.put(var4, var5);
      }

      return new NativeStats(var1);
   }

   public void dump(PrintStream var1) {
      boolean var2 = true;

      for (Entry var4 : this.snapshot.entrySet()) {
         NativeStats.StatsInterface var5 = (NativeStats.StatsInterface)var4.getKey();
         ArrayList var6 = (ArrayList)var4.getValue();
         int var7 = 0;

         for (NativeStats.NativeFunction var9 : var6) {
            var7 += var9.getCounter();
         }

         if (!var2) {
            var1.print(", ");
         }

         var2 = false;
         var1.print("[");
         if (var7 > 0) {
            var1.println("{ ");
            var1.println("  \"class\": \"" + var5.getNativeClass() + "\",");
            var1.println("  \"total\": " + var7 + ", ");
            var1.print("  \"functions\": {");
            boolean var11 = true;

            for (NativeStats.NativeFunction var10 : var6) {
               if (var10.getCounter() > 0) {
                  if (!var11) {
                     var1.print(",");
                  }

                  var11 = false;
                  var1.println();
                  var1.print("    \"" + var10.getName() + "\": " + var10.getCounter());
               }
            }

            var1.println();
            var1.println("  }");
            var1.print("}");
         }

         var1.print("]");
      }
   }

   private static HashMap<NativeStats.StatsInterface, ArrayList<NativeStats.NativeFunction>> snapshot(Collection<NativeStats.StatsInterface> var0) {
      HashMap var1 = new HashMap();

      for (NativeStats.StatsInterface var3 : var0) {
         int var4 = var3.functionCount();
         ArrayList var5 = new ArrayList(var4);

         for (int var6 = 0; var6 < var4; var6++) {
            String var7 = var3.functionName(var6);
            var5.add(new NativeStats.NativeFunction(var6, var7, 0));
         }

         Collections.sort(var5);
         var1.put(var3, var5);
      }

      return var1;
   }

   public static class NativeFunction implements Comparable<NativeStats.NativeFunction> {
      private final int ordinal;
      private final String name;
      private int counter;

      public NativeFunction(int var1, String var2, int var3) {
         this.ordinal = var1;
         this.name = var2;
         this.counter = var3;
      }

      void subtract(NativeStats.NativeFunction var1) {
         this.counter = this.counter - var1.counter;
      }

      public int getCounter() {
         return this.counter;
      }

      public void setCounter(int var1) {
         this.counter = var1;
      }

      public String getName() {
         return this.name;
      }

      public int getOrdinal() {
         return this.ordinal;
      }

      public int compareTo(NativeStats.NativeFunction var1) {
         return var1.counter - this.counter;
      }

      public void reset() {
         this.counter = 0;
      }

      public NativeStats.NativeFunction copy() {
         return new NativeStats.NativeFunction(this.ordinal, this.name, this.counter);
      }
   }

   public interface StatsInterface {
      String getNativeClass();

      int functionCount();

      String functionName(int var1);

      int functionCounter(int var1);
   }
}
