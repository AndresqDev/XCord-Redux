package joptsimple.internal;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

public class AbbreviationMap<V> implements OptionNameMap<V> {
   private final Map<Character, AbbreviationMap<V>> children = new TreeMap<>();
   private String key;
   private V value;
   private int keysBeyond;

   @Override
   public boolean contains(String var1) {
      return this.get(var1) != null;
   }

   @Override
   public V get(String var1) {
      char[] var2 = charsOf(var1);
      AbbreviationMap var3 = this;

      for (char var7 : var2) {
         var3 = var3.children.get(var7);
         if (var3 == null) {
            return null;
         }
      }

      return var3.value;
   }

   @Override
   public void put(String var1, V var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else if (var1.length() == 0) {
         throw new IllegalArgumentException();
      } else {
         char[] var3 = charsOf(var1);
         this.add(var3, (V)var2, 0, var3.length);
      }
   }

   @Override
   public void putAll(Iterable<String> var1, V var2) {
      for (String var4 : var1) {
         this.put(var4, (V)var2);
      }
   }

   private boolean add(char[] var1, V var2, int var3, int var4) {
      if (var3 == var4) {
         this.value = (V)var2;
         boolean var8 = this.key != null;
         this.key = new String(var1);
         return !var8;
      } else {
         char var5 = var1[var3];
         AbbreviationMap var6 = this.children.get(var5);
         if (var6 == null) {
            var6 = new AbbreviationMap();
            this.children.put(var5, var6);
         }

         boolean var7 = var6.add(var1, var2, var3 + 1, var4);
         if (var7) {
            this.keysBeyond++;
         }

         if (this.key == null) {
            this.value = (V)(this.keysBeyond > 1 ? null : var2);
         }

         return var7;
      }
   }

   @Override
   public void remove(String var1) {
      if (var1.length() == 0) {
         throw new IllegalArgumentException();
      } else {
         char[] var2 = charsOf(var1);
         this.remove(var2, 0, var2.length);
      }
   }

   private boolean remove(char[] var1, int var2, int var3) {
      if (var2 == var3) {
         return this.removeAtEndOfKey();
      } else {
         char var4 = var1[var2];
         AbbreviationMap var5 = this.children.get(var4);
         if (var5 != null && var5.remove(var1, var2 + 1, var3)) {
            this.keysBeyond--;
            if (var5.keysBeyond == 0) {
               this.children.remove(var4);
            }

            if (this.keysBeyond == 1 && this.key == null) {
               this.setValueToThatOfOnlyChild();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private void setValueToThatOfOnlyChild() {
      Entry var1 = this.children.entrySet().iterator().next();
      AbbreviationMap var2 = (AbbreviationMap)var1.getValue();
      this.value = var2.value;
   }

   private boolean removeAtEndOfKey() {
      if (this.key == null) {
         return false;
      } else {
         this.key = null;
         if (this.keysBeyond == 1) {
            this.setValueToThatOfOnlyChild();
         } else {
            this.value = null;
         }

         return true;
      }
   }

   @Override
   public Map<String, V> toJavaUtilMap() {
      TreeMap var1 = new TreeMap();
      this.addToMappings(var1);
      return var1;
   }

   private void addToMappings(Map<String, V> var1) {
      if (this.key != null) {
         var1.put(this.key, this.value);
      }

      for (AbbreviationMap var3 : this.children.values()) {
         var3.addToMappings(var1);
      }
   }

   private static char[] charsOf(String var0) {
      char[] var1 = new char[var0.length()];
      var0.getChars(0, var0.length(), var1, 0);
      return var1;
   }
}
