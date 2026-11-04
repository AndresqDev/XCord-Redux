package org.apache.logging.log4j.message;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.EnglishEnums;
import org.apache.logging.log4j.util.IndexedReadOnlyStringMap;
import org.apache.logging.log4j.util.IndexedStringMap;
import org.apache.logging.log4j.util.MultiFormatStringBuilderFormattable;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringBuilders;
import org.apache.logging.log4j.util.TriConsumer;

@AsynchronouslyFormattable
@PerformanceSensitive({"allocation"})
public class MapMessage<M extends MapMessage<M, V>, V> implements MultiFormatStringBuilderFormattable {
   private static final long serialVersionUID = -5031471831131487120L;
   private final IndexedStringMap data;

   public MapMessage() {
      this.data = new SortedArrayStringMap();
   }

   public MapMessage(int var1) {
      this.data = new SortedArrayStringMap(var1);
   }

   public MapMessage(Map<String, V> var1) {
      this.data = new SortedArrayStringMap(var1);
   }

   @Override
   public String[] getFormats() {
      return MapMessage.MapFormat.names();
   }

   @Override
   public Object[] getParameters() {
      Object[] var1 = new Object[this.data.size()];

      for (int var2 = 0; var2 < this.data.size(); var2++) {
         var1[var2] = this.data.getValueAt(var2);
      }

      return var1;
   }

   @Override
   public String getFormat() {
      return "";
   }

   public Map<String, V> getData() {
      TreeMap var1 = new TreeMap();

      for (int var2 = 0; var2 < this.data.size(); var2++) {
         var1.put(this.data.getKeyAt(var2), this.data.getValueAt(var2));
      }

      return Collections.unmodifiableMap(var1);
   }

   public IndexedReadOnlyStringMap getIndexedReadOnlyStringMap() {
      return this.data;
   }

   public void clear() {
      this.data.clear();
   }

   public boolean containsKey(String var1) {
      return this.data.containsKey(var1);
   }

   public void put(String var1, String var2) {
      if (var2 == null) {
         throw new IllegalArgumentException("No value provided for key " + var1);
      } else {
         String var3 = this.toKey(var1);
         this.validate(var3, var2);
         this.data.putValue(var3, var2);
      }
   }

   public void putAll(Map<String, String> var1) {
      for (Entry var3 : var1.entrySet()) {
         this.data.putValue((String)var3.getKey(), var3.getValue());
      }
   }

   public String get(String var1) {
      Object var2 = this.data.getValue(var1);
      return ParameterFormatter.deepToString(var2);
   }

   public String remove(String var1) {
      String var2 = this.get(var1);
      this.data.remove(var1);
      return var2;
   }

   public String asString() {
      return this.format((MapMessage.MapFormat)null, new StringBuilder()).toString();
   }

   public String asString(String var1) {
      try {
         return this.format(EnglishEnums.valueOf(MapMessage.MapFormat.class, var1), new StringBuilder()).toString();
      } catch (IllegalArgumentException var3) {
         return this.asString();
      }
   }

   public <CV> void forEach(BiConsumer<String, ? super CV> var1) {
      this.data.forEach(var1);
   }

   public <CV, S> void forEach(TriConsumer<String, ? super CV, S> var1, S var2) {
      this.data.forEach(var1, var2);
   }

   private StringBuilder format(MapMessage.MapFormat var1, StringBuilder var2) {
      if (var1 == null) {
         this.appendMap(var2);
      } else {
         switch (var1) {
            case XML:
               this.asXml(var2);
               break;
            case JSON:
               this.asJson(var2);
               break;
            case JAVA:
               this.asJava(var2);
               break;
            case JAVA_UNQUOTED:
               this.asJavaUnquoted(var2);
               break;
            default:
               this.appendMap(var2);
         }
      }

      return var2;
   }

   public void asXml(StringBuilder var1) {
      var1.append("<Map>\n");

      for (int var2 = 0; var2 < this.data.size(); var2++) {
         var1.append("  <Entry key=\"").append(this.data.getKeyAt(var2)).append("\">");
         int var3 = var1.length();
         ParameterFormatter.recursiveDeepToString(this.data.getValueAt(var2), var1);
         StringBuilders.escapeXml(var1, var3);
         var1.append("</Entry>\n");
      }

      var1.append("</Map>");
   }

   @Override
   public String getFormattedMessage() {
      return this.asString();
   }

   @Override
   public String getFormattedMessage(String[] var1) {
      return this.format(this.getFormat(var1), new StringBuilder()).toString();
   }

   private MapMessage.MapFormat getFormat(String[] var1) {
      if (var1 != null && var1.length != 0) {
         for (int var2 = 0; var2 < var1.length; var2++) {
            MapMessage.MapFormat var3 = MapMessage.MapFormat.lookupIgnoreCase(var1[var2]);
            if (var3 != null) {
               return var3;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   protected void appendMap(StringBuilder var1) {
      for (int var2 = 0; var2 < this.data.size(); var2++) {
         if (var2 > 0) {
            var1.append(' ');
         }

         var1.append(this.data.getKeyAt(var2)).append('=').append('"');
         ParameterFormatter.recursiveDeepToString(this.data.getValueAt(var2), var1);
         var1.append('"');
      }
   }

   protected void asJson(StringBuilder var1) {
      MapMessageJsonFormatter.format(var1, this.data);
   }

   protected void asJavaUnquoted(StringBuilder var1) {
      this.asJava(var1, false);
   }

   protected void asJava(StringBuilder var1) {
      this.asJava(var1, true);
   }

   private void asJava(StringBuilder var1, boolean var2) {
      var1.append('{');

      for (int var3 = 0; var3 < this.data.size(); var3++) {
         if (var3 > 0) {
            var1.append(", ");
         }

         var1.append(this.data.getKeyAt(var3)).append('=');
         if (var2) {
            var1.append('"');
         }

         ParameterFormatter.recursiveDeepToString(this.data.getValueAt(var3), var1);
         if (var2) {
            var1.append('"');
         }
      }

      var1.append('}');
   }

   public M newInstance(Map<String, V> var1) {
      return (M)(new MapMessage(var1));
   }

   @Override
   public String toString() {
      return this.asString();
   }

   @Override
   public void formatTo(StringBuilder var1) {
      this.format((MapMessage.MapFormat)null, var1);
   }

   @Override
   public void formatTo(String[] var1, StringBuilder var2) {
      this.format(this.getFormat(var1), var2);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         MapMessage var2 = (MapMessage)var1;
         return this.data.equals(var2.data);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.data.hashCode();
   }

   @Override
   public Throwable getThrowable() {
      return null;
   }

   protected void validate(String var1, boolean var2) {
   }

   protected void validate(String var1, byte var2) {
   }

   protected void validate(String var1, char var2) {
   }

   protected void validate(String var1, double var2) {
   }

   protected void validate(String var1, float var2) {
   }

   protected void validate(String var1, int var2) {
   }

   protected void validate(String var1, long var2) {
   }

   protected void validate(String var1, Object var2) {
   }

   protected void validate(String var1, short var2) {
   }

   protected void validate(String var1, String var2) {
   }

   protected String toKey(String var1) {
      return var1;
   }

   public M with(String var1, boolean var2) {
      String var3 = this.toKey(var1);
      this.validate(var3, var2);
      this.data.putValue(var3, Boolean.valueOf(var2));
      return (M)this;
   }

   public M with(String var1, byte var2) {
      String var3 = this.toKey(var1);
      this.validate(var3, var2);
      this.data.putValue(var3, Byte.valueOf(var2));
      return (M)this;
   }

   public M with(String var1, char var2) {
      String var3 = this.toKey(var1);
      this.validate(var3, var2);
      this.data.putValue(var3, Character.valueOf(var2));
      return (M)this;
   }

   public M with(String var1, double var2) {
      String var4 = this.toKey(var1);
      this.validate(var4, var2);
      this.data.putValue(var4, Double.valueOf(var2));
      return (M)this;
   }

   public M with(String var1, float var2) {
      String var3 = this.toKey(var1);
      this.validate(var3, var2);
      this.data.putValue(var3, Float.valueOf(var2));
      return (M)this;
   }

   public M with(String var1, int var2) {
      String var3 = this.toKey(var1);
      this.validate(var3, var2);
      this.data.putValue(var3, Integer.valueOf(var2));
      return (M)this;
   }

   public M with(String var1, long var2) {
      String var4 = this.toKey(var1);
      this.validate(var4, var2);
      this.data.putValue(var4, Long.valueOf(var2));
      return (M)this;
   }

   public M with(String var1, Object var2) {
      String var3 = this.toKey(var1);
      this.validate(var3, var2);
      this.data.putValue(var3, var2);
      return (M)this;
   }

   public M with(String var1, short var2) {
      String var3 = this.toKey(var1);
      this.validate(var3, var2);
      this.data.putValue(var3, Short.valueOf(var2));
      return (M)this;
   }

   public M with(String var1, String var2) {
      String var3 = this.toKey(var1);
      this.put(var3, var2);
      return (M)this;
   }

   public static enum MapFormat {
      XML,
      JSON,
      JAVA,
      JAVA_UNQUOTED;

      public static MapMessage.MapFormat lookupIgnoreCase(String var0) {
         return XML.name().equalsIgnoreCase(var0)
            ? XML
            : (
               JSON.name().equalsIgnoreCase(var0)
                  ? JSON
                  : (JAVA.name().equalsIgnoreCase(var0) ? JAVA : (JAVA_UNQUOTED.name().equalsIgnoreCase(var0) ? JAVA_UNQUOTED : null))
            );
      }

      public static String[] names() {
         return new String[]{XML.name(), JSON.name(), JAVA.name(), JAVA_UNQUOTED.name()};
      }
   }
}
