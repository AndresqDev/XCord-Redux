package net.md_5.bungee.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Configuration {
   private static final char SEPARATOR = '.';
   final Map<String, Object> self = new LinkedHashMap<>();
   private final Configuration defaults;

   public Configuration() {
      this(null);
   }

   public Configuration(Configuration var1) {
      this(new LinkedHashMap(), var1);
   }

   Configuration(Map<?, ?> var1, Configuration var2) {
      this.defaults = var2;

      for (Entry var4 : var1.entrySet()) {
         String var5 = var4.getKey() == null ? "null" : var4.getKey().toString();
         if (var4.getValue() instanceof Map) {
            this.self.put(var5, new Configuration((Map<?, ?>)var4.getValue(), var2 == null ? null : var2.getSection(var5)));
         } else {
            this.self.put(var5, var4.getValue());
         }
      }
   }

   public Configuration(Configuration var1, Configuration var2) {
      this(var1.self, var2);
   }

   private Configuration getSectionFor(String var1) {
      int var2 = var1.indexOf(46);
      if (var2 == -1) {
         return this;
      } else {
         String var3 = var1.substring(0, var2);
         Object var4 = this.self.get(var3);
         if (var4 == null) {
            var4 = new Configuration(this.defaults == null ? null : this.defaults.getSection(var3));
            this.self.put(var3, var4);
         }

         return (Configuration)var4;
      }
   }

   private String getChild(String var1) {
      int var2 = var1.indexOf(46);
      return var2 == -1 ? var1 : var1.substring(var2 + 1);
   }

   public <T> T get(String var1, T var2) {
      Configuration var3 = this.getSectionFor(var1);
      Object var4;
      if (var3 == this) {
         var4 = this.self.get(var1);
      } else {
         var4 = var3.get(this.getChild(var1), var2);
      }

      if (var4 == null && var2 instanceof Configuration) {
         this.self.put(var1, var2);
      }

      return (T)(var4 != null ? var4 : var2);
   }

   public boolean contains(String var1) {
      return this.get(var1, null) != null;
   }

   public Object get(String var1) {
      return this.get(var1, this.getDefault(var1));
   }

   public Object getDefault(String var1) {
      return this.defaults == null ? null : this.defaults.get(var1);
   }

   public void set(String var1, Object var2) {
      if (var2 instanceof Map) {
         var2 = new Configuration((Map<?, ?>)var2, this.defaults == null ? null : this.defaults.getSection(var1));
      }

      Configuration var3 = this.getSectionFor(var1);
      if (var3 == this) {
         if (var2 == null) {
            this.self.remove(var1);
         } else {
            this.self.put(var1, var2);
         }
      } else {
         var3.set(this.getChild(var1), var2);
      }
   }

   public Configuration getSection(String var1) {
      Object var2 = this.getDefault(var1);
      return this.get(
         var1, (Configuration)(var2 instanceof Configuration ? var2 : new Configuration(this.defaults == null ? null : this.defaults.getSection(var1)))
      );
   }

   public Collection<String> getKeys() {
      return new LinkedHashSet<>(this.self.keySet());
   }

   public byte getByte(String var1) {
      Object var2 = this.getDefault(var1);
      return this.getByte(var1, var2 instanceof Number ? ((Number)var2).byteValue() : 0);
   }

   public byte getByte(String var1, byte var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Number ? ((Number)var3).byteValue() : var2;
   }

   public List<Byte> getByteList(String var1) {
      List var2 = this.getList(var1);
      ArrayList var3 = new ArrayList();

      for (Object var5 : var2) {
         if (var5 instanceof Number) {
            var3.add(((Number)var5).byteValue());
         }
      }

      return var3;
   }

   public short getShort(String var1) {
      Object var2 = this.getDefault(var1);
      return this.getShort(var1, var2 instanceof Number ? ((Number)var2).shortValue() : 0);
   }

   public short getShort(String var1, short var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Number ? ((Number)var3).shortValue() : var2;
   }

   public List<Short> getShortList(String var1) {
      List var2 = this.getList(var1);
      ArrayList var3 = new ArrayList();

      for (Object var5 : var2) {
         if (var5 instanceof Number) {
            var3.add(((Number)var5).shortValue());
         }
      }

      return var3;
   }

   public int getInt(String var1) {
      Object var2 = this.getDefault(var1);
      return this.getInt(var1, var2 instanceof Number ? ((Number)var2).intValue() : 0);
   }

   public int getInt(String var1, int var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Number ? ((Number)var3).intValue() : var2;
   }

   public List<Integer> getIntList(String var1) {
      List var2 = this.getList(var1);
      ArrayList var3 = new ArrayList();

      for (Object var5 : var2) {
         if (var5 instanceof Number) {
            var3.add(((Number)var5).intValue());
         }
      }

      return var3;
   }

   public long getLong(String var1) {
      Object var2 = this.getDefault(var1);
      return this.getLong(var1, var2 instanceof Number ? ((Number)var2).longValue() : 0L);
   }

   public long getLong(String var1, long var2) {
      Object var4 = this.get(var1, var2);
      return var4 instanceof Number ? ((Number)var4).longValue() : var2;
   }

   public List<Long> getLongList(String var1) {
      List var2 = this.getList(var1);
      ArrayList var3 = new ArrayList();

      for (Object var5 : var2) {
         if (var5 instanceof Number) {
            var3.add(((Number)var5).longValue());
         }
      }

      return var3;
   }

   public float getFloat(String var1) {
      Object var2 = this.getDefault(var1);
      return this.getFloat(var1, var2 instanceof Number ? ((Number)var2).floatValue() : 0.0F);
   }

   public float getFloat(String var1, float var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Number ? ((Number)var3).floatValue() : var2;
   }

   public List<Float> getFloatList(String var1) {
      List var2 = this.getList(var1);
      ArrayList var3 = new ArrayList();

      for (Object var5 : var2) {
         if (var5 instanceof Number) {
            var3.add(((Number)var5).floatValue());
         }
      }

      return var3;
   }

   public double getDouble(String var1) {
      Object var2 = this.getDefault(var1);
      return this.getDouble(var1, var2 instanceof Number ? ((Number)var2).doubleValue() : 0.0);
   }

   public double getDouble(String var1, double var2) {
      Object var4 = this.get(var1, var2);
      return var4 instanceof Number ? ((Number)var4).doubleValue() : var2;
   }

   public List<Double> getDoubleList(String var1) {
      List var2 = this.getList(var1);
      ArrayList var3 = new ArrayList();

      for (Object var5 : var2) {
         if (var5 instanceof Number) {
            var3.add(((Number)var5).doubleValue());
         }
      }

      return var3;
   }

   public boolean getBoolean(String var1) {
      Object var2 = this.getDefault(var1);
      return this.getBoolean(var1, var2 instanceof Boolean ? (Boolean)var2 : false);
   }

   public boolean getBoolean(String var1, boolean var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Boolean ? (Boolean)var3 : var2;
   }

   public List<Boolean> getBooleanList(String var1) {
      List var2 = this.getList(var1);
      ArrayList var3 = new ArrayList();

      for (Object var5 : var2) {
         if (var5 instanceof Boolean) {
            var3.add((Boolean)var5);
         }
      }

      return var3;
   }

   public char getChar(String var1) {
      Object var2 = this.getDefault(var1);
      return this.getChar(var1, var2 instanceof Character ? (Character)var2 : '\u0000');
   }

   public char getChar(String var1, char var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof Character ? (Character)var3 : var2;
   }

   public List<Character> getCharList(String var1) {
      List var2 = this.getList(var1);
      ArrayList var3 = new ArrayList();

      for (Object var5 : var2) {
         if (var5 instanceof Character) {
            var3.add((Character)var5);
         }
      }

      return var3;
   }

   public String getString(String var1) {
      Object var2 = this.getDefault(var1);
      return this.getString(var1, var2 instanceof String ? (String)var2 : "");
   }

   public String getString(String var1, String var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof String ? (String)var3 : var2;
   }

   public List<String> getStringList(String var1) {
      List var2 = this.getList(var1);
      ArrayList var3 = new ArrayList();

      for (Object var5 : var2) {
         if (var5 instanceof String) {
            var3.add((String)var5);
         }
      }

      return var3;
   }

   public List<?> getList(String var1) {
      Object var2 = this.getDefault(var1);
      return this.getList(var1, var2 instanceof List ? (List)var2 : Collections.EMPTY_LIST);
   }

   public List<?> getList(String var1, List<?> var2) {
      Object var3 = this.get(var1, var2);
      return var3 instanceof List ? (List)var3 : var2;
   }
}
