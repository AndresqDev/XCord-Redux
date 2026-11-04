package org.yaml.snakeyaml.representer;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.reader.StreamReader;

class SafeRepresenter extends BaseRepresenter {
   protected Map<Class<? extends Object>, Tag> classTags;
   protected TimeZone timeZone = null;
   protected DumperOptions.NonPrintableStyle nonPrintableStyle;
   private static final Pattern MULTILINE_PATTERN = Pattern.compile("\n|\u0085|\u2028|\u2029");

   public SafeRepresenter() {
      this(new DumperOptions());
   }

   public SafeRepresenter(DumperOptions var1) {
      this.nullRepresenter = new SafeRepresenter.RepresentNull();
      this.representers.put(String.class, new SafeRepresenter.RepresentString());
      this.representers.put(Boolean.class, new SafeRepresenter.RepresentBoolean());
      this.representers.put(Character.class, new SafeRepresenter.RepresentString());
      this.representers.put(UUID.class, new SafeRepresenter.RepresentUuid());
      this.representers.put(byte[].class, new SafeRepresenter.RepresentByteArray());
      SafeRepresenter.RepresentPrimitiveArray var2 = new SafeRepresenter.RepresentPrimitiveArray();
      this.representers.put(short[].class, var2);
      this.representers.put(int[].class, var2);
      this.representers.put(long[].class, var2);
      this.representers.put(float[].class, var2);
      this.representers.put(double[].class, var2);
      this.representers.put(char[].class, var2);
      this.representers.put(boolean[].class, var2);
      this.multiRepresenters.put(Number.class, new SafeRepresenter.RepresentNumber());
      this.multiRepresenters.put(List.class, new SafeRepresenter.RepresentList());
      this.multiRepresenters.put(Map.class, new SafeRepresenter.RepresentMap());
      this.multiRepresenters.put(Set.class, new SafeRepresenter.RepresentSet());
      this.multiRepresenters.put(Iterator.class, new SafeRepresenter.RepresentIterator());
      this.multiRepresenters.put(new Object[0].getClass(), new SafeRepresenter.RepresentArray());
      this.multiRepresenters.put(Date.class, new SafeRepresenter.RepresentDate());
      this.multiRepresenters.put(Enum.class, new SafeRepresenter.RepresentEnum());
      this.multiRepresenters.put(Calendar.class, new SafeRepresenter.RepresentDate());
      this.classTags = new HashMap<>();
      this.nonPrintableStyle = var1.getNonPrintableStyle();
   }

   protected Tag getTag(Class<?> var1, Tag var2) {
      return this.classTags.containsKey(var1) ? this.classTags.get(var1) : var2;
   }

   public Tag addClassTag(Class<? extends Object> var1, Tag var2) {
      if (var2 == null) {
         throw new NullPointerException("Tag must be provided.");
      } else {
         return this.classTags.put(var1, var2);
      }
   }

   public TimeZone getTimeZone() {
      return this.timeZone;
   }

   public void setTimeZone(TimeZone var1) {
      this.timeZone = var1;
   }

   private static class IteratorWrapper implements Iterable<Object> {
      private Iterator<Object> iter;

      public IteratorWrapper(Iterator<Object> var1) {
         this.iter = var1;
      }

      @Override
      public Iterator<Object> iterator() {
         return this.iter;
      }
   }

   protected class RepresentArray implements Represent {
      @Override
      public Node representData(Object var1) {
         Object[] var2 = (Object[])var1;
         List var3 = Arrays.asList(var2);
         return SafeRepresenter.this.representSequence(Tag.SEQ, var3, DumperOptions.FlowStyle.AUTO);
      }
   }

   protected class RepresentBoolean implements Represent {
      @Override
      public Node representData(Object var1) {
         String var2;
         if (Boolean.TRUE.equals(var1)) {
            var2 = "true";
         } else {
            var2 = "false";
         }

         return SafeRepresenter.this.representScalar(Tag.BOOL, var2);
      }
   }

   protected class RepresentByteArray implements Represent {
      @Override
      public Node representData(Object var1) {
         char[] var2 = Base64Coder.encode((byte[])var1);
         return SafeRepresenter.this.representScalar(Tag.BINARY, String.valueOf(var2), DumperOptions.ScalarStyle.LITERAL);
      }
   }

   protected class RepresentDate implements Represent {
      @Override
      public Node representData(Object var1) {
         Calendar var2;
         if (var1 instanceof Calendar) {
            var2 = (Calendar)var1;
         } else {
            var2 = Calendar.getInstance(SafeRepresenter.this.getTimeZone() == null ? TimeZone.getTimeZone("UTC") : SafeRepresenter.this.timeZone);
            var2.setTime((Date)var1);
         }

         int var3 = var2.get(1);
         int var4 = var2.get(2) + 1;
         int var5 = var2.get(5);
         int var6 = var2.get(11);
         int var7 = var2.get(12);
         int var8 = var2.get(13);
         int var9 = var2.get(14);
         StringBuilder var10 = new StringBuilder(String.valueOf(var3));

         while (var10.length() < 4) {
            var10.insert(0, "0");
         }

         var10.append("-");
         if (var4 < 10) {
            var10.append("0");
         }

         var10.append(String.valueOf(var4));
         var10.append("-");
         if (var5 < 10) {
            var10.append("0");
         }

         var10.append(String.valueOf(var5));
         var10.append("T");
         if (var6 < 10) {
            var10.append("0");
         }

         var10.append(String.valueOf(var6));
         var10.append(":");
         if (var7 < 10) {
            var10.append("0");
         }

         var10.append(String.valueOf(var7));
         var10.append(":");
         if (var8 < 10) {
            var10.append("0");
         }

         var10.append(String.valueOf(var8));
         if (var9 > 0) {
            if (var9 < 10) {
               var10.append(".00");
            } else if (var9 < 100) {
               var10.append(".0");
            } else {
               var10.append(".");
            }

            var10.append(String.valueOf(var9));
         }

         int var11 = var2.getTimeZone().getOffset(var2.getTime().getTime());
         if (var11 == 0) {
            var10.append('Z');
         } else {
            if (var11 < 0) {
               var10.append('-');
               var11 *= -1;
            } else {
               var10.append('+');
            }

            int var12 = var11 / 60000;
            int var13 = var12 / 60;
            int var14 = var12 % 60;
            if (var13 < 10) {
               var10.append('0');
            }

            var10.append(var13);
            var10.append(':');
            if (var14 < 10) {
               var10.append('0');
            }

            var10.append(var14);
         }

         return SafeRepresenter.this.representScalar(
            SafeRepresenter.this.getTag(var1.getClass(), Tag.TIMESTAMP), var10.toString(), DumperOptions.ScalarStyle.PLAIN
         );
      }
   }

   protected class RepresentEnum implements Represent {
      @Override
      public Node representData(Object var1) {
         Tag var2 = new Tag((Class<? extends Object>)var1.getClass());
         return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(var1.getClass(), var2), ((Enum)var1).name());
      }
   }

   protected class RepresentIterator implements Represent {
      @Override
      public Node representData(Object var1) {
         Iterator var2 = (Iterator)var1;
         return SafeRepresenter.this.representSequence(
            SafeRepresenter.this.getTag(var1.getClass(), Tag.SEQ), new SafeRepresenter.IteratorWrapper(var2), DumperOptions.FlowStyle.AUTO
         );
      }
   }

   protected class RepresentList implements Represent {
      @Override
      public Node representData(Object var1) {
         return SafeRepresenter.this.representSequence(SafeRepresenter.this.getTag(var1.getClass(), Tag.SEQ), (List)var1, DumperOptions.FlowStyle.AUTO);
      }
   }

   protected class RepresentMap implements Represent {
      @Override
      public Node representData(Object var1) {
         return SafeRepresenter.this.representMapping(SafeRepresenter.this.getTag(var1.getClass(), Tag.MAP), (Map<?, ?>)var1, DumperOptions.FlowStyle.AUTO);
      }
   }

   protected class RepresentNull implements Represent {
      @Override
      public Node representData(Object var1) {
         return SafeRepresenter.this.representScalar(Tag.NULL, "null");
      }
   }

   protected class RepresentNumber implements Represent {
      @Override
      public Node representData(Object var1) {
         Tag var2;
         String var3;
         if (!(var1 instanceof Byte) && !(var1 instanceof Short) && !(var1 instanceof Integer) && !(var1 instanceof Long) && !(var1 instanceof BigInteger)) {
            Number var4 = (Number)var1;
            var2 = Tag.FLOAT;
            if (var4.equals(Double.NaN)) {
               var3 = ".NaN";
            } else if (var4.equals(Double.POSITIVE_INFINITY)) {
               var3 = ".inf";
            } else if (var4.equals(Double.NEGATIVE_INFINITY)) {
               var3 = "-.inf";
            } else {
               var3 = var4.toString();
            }
         } else {
            var2 = Tag.INT;
            var3 = var1.toString();
         }

         return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(var1.getClass(), var2), var3);
      }
   }

   protected class RepresentPrimitiveArray implements Represent {
      @Override
      public Node representData(Object var1) {
         Class var2 = var1.getClass().getComponentType();
         if (byte.class == var2) {
            return SafeRepresenter.this.representSequence(Tag.SEQ, this.asByteList(var1), DumperOptions.FlowStyle.AUTO);
         } else if (short.class == var2) {
            return SafeRepresenter.this.representSequence(Tag.SEQ, this.asShortList(var1), DumperOptions.FlowStyle.AUTO);
         } else if (int.class == var2) {
            return SafeRepresenter.this.representSequence(Tag.SEQ, this.asIntList(var1), DumperOptions.FlowStyle.AUTO);
         } else if (long.class == var2) {
            return SafeRepresenter.this.representSequence(Tag.SEQ, this.asLongList(var1), DumperOptions.FlowStyle.AUTO);
         } else if (float.class == var2) {
            return SafeRepresenter.this.representSequence(Tag.SEQ, this.asFloatList(var1), DumperOptions.FlowStyle.AUTO);
         } else if (double.class == var2) {
            return SafeRepresenter.this.representSequence(Tag.SEQ, this.asDoubleList(var1), DumperOptions.FlowStyle.AUTO);
         } else if (char.class == var2) {
            return SafeRepresenter.this.representSequence(Tag.SEQ, this.asCharList(var1), DumperOptions.FlowStyle.AUTO);
         } else if (boolean.class == var2) {
            return SafeRepresenter.this.representSequence(Tag.SEQ, this.asBooleanList(var1), DumperOptions.FlowStyle.AUTO);
         } else {
            throw new YAMLException("Unexpected primitive '" + var2.getCanonicalName() + "'");
         }
      }

      private List<Byte> asByteList(Object var1) {
         byte[] var2 = (byte[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for (int var4 = 0; var4 < var2.length; var4++) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Short> asShortList(Object var1) {
         short[] var2 = (short[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for (int var4 = 0; var4 < var2.length; var4++) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Integer> asIntList(Object var1) {
         int[] var2 = (int[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for (int var4 = 0; var4 < var2.length; var4++) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Long> asLongList(Object var1) {
         long[] var2 = (long[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for (int var4 = 0; var4 < var2.length; var4++) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Float> asFloatList(Object var1) {
         float[] var2 = (float[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for (int var4 = 0; var4 < var2.length; var4++) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Double> asDoubleList(Object var1) {
         double[] var2 = (double[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for (int var4 = 0; var4 < var2.length; var4++) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Character> asCharList(Object var1) {
         char[] var2 = (char[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for (int var4 = 0; var4 < var2.length; var4++) {
            var3.add(var2[var4]);
         }

         return var3;
      }

      private List<Boolean> asBooleanList(Object var1) {
         boolean[] var2 = (boolean[])var1;
         ArrayList var3 = new ArrayList(var2.length);

         for (int var4 = 0; var4 < var2.length; var4++) {
            var3.add(var2[var4]);
         }

         return var3;
      }
   }

   protected class RepresentSet implements Represent {
      @Override
      public Node representData(Object var1) {
         LinkedHashMap var2 = new LinkedHashMap();

         for (Object var5 : (Set)var1) {
            var2.put(var5, null);
         }

         return SafeRepresenter.this.representMapping(SafeRepresenter.this.getTag(var1.getClass(), Tag.SET), var2, DumperOptions.FlowStyle.AUTO);
      }
   }

   protected class RepresentString implements Represent {
      @Override
      public Node representData(Object var1) {
         Tag var2 = Tag.STR;
         DumperOptions.ScalarStyle var3 = null;
         String var4 = var1.toString();
         if (SafeRepresenter.this.nonPrintableStyle == DumperOptions.NonPrintableStyle.BINARY && !StreamReader.isPrintable(var4)) {
            var2 = Tag.BINARY;

            char[] var5;
            try {
               byte[] var6 = var4.getBytes("UTF-8");
               String var7 = new String(var6, "UTF-8");
               if (!var7.equals(var4)) {
                  throw new YAMLException("invalid string value has occurred");
               }

               var5 = Base64Coder.encode(var6);
            } catch (UnsupportedEncodingException var8) {
               throw new YAMLException(var8);
            }

            var4 = String.valueOf(var5);
            var3 = DumperOptions.ScalarStyle.LITERAL;
         }

         if (SafeRepresenter.this.defaultScalarStyle == DumperOptions.ScalarStyle.PLAIN && SafeRepresenter.MULTILINE_PATTERN.matcher(var4).find()) {
            var3 = DumperOptions.ScalarStyle.LITERAL;
         }

         return SafeRepresenter.this.representScalar(var2, var4, var3);
      }
   }

   protected class RepresentUuid implements Represent {
      @Override
      public Node representData(Object var1) {
         return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(var1.getClass(), new Tag(UUID.class)), var1.toString());
      }
   }
}
