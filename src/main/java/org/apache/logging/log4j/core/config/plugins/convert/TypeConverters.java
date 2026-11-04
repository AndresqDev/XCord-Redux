package org.apache.logging.log4j.core.config.plugins.convert;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Provider;
import java.security.Security;
import java.util.UUID;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.rolling.action.Duration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.util.CronExpression;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Constants;
import org.apache.logging.log4j.util.LoaderUtil;

public final class TypeConverters {
   public static final String CATEGORY = "TypeConverter";
   private static final Logger LOGGER = StatusLogger.getLogger();

   public static <T> T convert(String var0, Class<? extends T> var1, Object var2) {
      TypeConverter var3 = TypeConverterRegistry.getInstance().findCompatibleConverter(var1);
      if (var0 == null) {
         return parseDefaultValue(var3, var2);
      } else {
         try {
            return (T)var3.convert(var0);
         } catch (Exception var5) {
            LOGGER.warn("Error while converting string [{}] to type [{}]. Using default value [{}].", var0, var1, var2, var5);
            return parseDefaultValue(var3, var2);
         }
      }
   }

   private static <T> T parseDefaultValue(TypeConverter<T> var0, Object var1) {
      if (var1 == null) {
         return null;
      } else if (!(var1 instanceof String)) {
         return (T)var1;
      } else {
         try {
            return (T)var0.convert((String)var1);
         } catch (Exception var3) {
            LOGGER.debug("Can't parse default value [{}] for type [{}].", var1, var0.getClass(), var3);
            return null;
         }
      }
   }

   @Plugin(
      name = "BigDecimal",
      category = "TypeConverter"
   )
   public static class BigDecimalConverter implements TypeConverter<BigDecimal> {
      public BigDecimal convert(String var1) {
         return new BigDecimal(var1);
      }
   }

   @Plugin(
      name = "BigInteger",
      category = "TypeConverter"
   )
   public static class BigIntegerConverter implements TypeConverter<BigInteger> {
      public BigInteger convert(String var1) {
         return new BigInteger(var1);
      }
   }

   @Plugin(
      name = "Boolean",
      category = "TypeConverter"
   )
   public static class BooleanConverter implements TypeConverter<Boolean> {
      public Boolean convert(String var1) {
         return Boolean.valueOf(var1);
      }
   }

   @Plugin(
      name = "ByteArray",
      category = "TypeConverter"
   )
   public static class ByteArrayConverter implements TypeConverter<byte[]> {
      private static final String PREFIX_0x = "0x";
      private static final String PREFIX_BASE64 = "Base64:";

      public byte[] convert(String var1) {
         byte[] var2;
         if (var1 == null || var1.isEmpty()) {
            var2 = Constants.EMPTY_BYTE_ARRAY;
         } else if (var1.startsWith("Base64:")) {
            String var3 = var1.substring("Base64:".length());
            var2 = Base64Converter.parseBase64Binary(var3);
         } else if (var1.startsWith("0x")) {
            String var4 = var1.substring("0x".length());
            var2 = HexConverter.parseHexBinary(var4);
         } else {
            var2 = var1.getBytes(Charset.defaultCharset());
         }

         return var2;
      }
   }

   @Plugin(
      name = "Byte",
      category = "TypeConverter"
   )
   public static class ByteConverter implements TypeConverter<Byte> {
      public Byte convert(String var1) {
         return Byte.valueOf(var1);
      }
   }

   @Plugin(
      name = "CharacterArray",
      category = "TypeConverter"
   )
   public static class CharArrayConverter implements TypeConverter<char[]> {
      public char[] convert(String var1) {
         return var1.toCharArray();
      }
   }

   @Plugin(
      name = "Character",
      category = "TypeConverter"
   )
   public static class CharacterConverter implements TypeConverter<Character> {
      public Character convert(String var1) {
         if (var1.length() != 1) {
            throw new IllegalArgumentException("Character string must be of length 1: " + var1);
         } else {
            return var1.toCharArray()[0];
         }
      }
   }

   @Plugin(
      name = "Charset",
      category = "TypeConverter"
   )
   public static class CharsetConverter implements TypeConverter<Charset> {
      public Charset convert(String var1) {
         return Charset.forName(var1);
      }
   }

   @Plugin(
      name = "Class",
      category = "TypeConverter"
   )
   public static class ClassConverter implements TypeConverter<Class<?>> {
      public Class<?> convert(String var1) throws ClassNotFoundException {
         String var2 = var1.toLowerCase();
         switch (var2) {
            case "boolean":
               return boolean.class;
            case "byte":
               return byte.class;
            case "char":
               return char.class;
            case "double":
               return double.class;
            case "float":
               return float.class;
            case "int":
               return int.class;
            case "long":
               return long.class;
            case "short":
               return short.class;
            case "void":
               return void.class;
            default:
               return LoaderUtil.loadClass(var1);
         }
      }
   }

   @Plugin(
      name = "CronExpression",
      category = "TypeConverter"
   )
   public static class CronExpressionConverter implements TypeConverter<CronExpression> {
      public CronExpression convert(String var1) throws Exception {
         return new CronExpression(var1);
      }
   }

   @Plugin(
      name = "Double",
      category = "TypeConverter"
   )
   public static class DoubleConverter implements TypeConverter<Double> {
      public Double convert(String var1) {
         return Double.valueOf(var1);
      }
   }

   @Plugin(
      name = "Duration",
      category = "TypeConverter"
   )
   public static class DurationConverter implements TypeConverter<Duration> {
      public Duration convert(String var1) {
         return Duration.parse(var1);
      }
   }

   @Plugin(
      name = "File",
      category = "TypeConverter"
   )
   public static class FileConverter implements TypeConverter<File> {
      public File convert(String var1) {
         return new File(var1);
      }
   }

   @Plugin(
      name = "Float",
      category = "TypeConverter"
   )
   public static class FloatConverter implements TypeConverter<Float> {
      public Float convert(String var1) {
         return Float.valueOf(var1);
      }
   }

   @Plugin(
      name = "InetAddress",
      category = "TypeConverter"
   )
   public static class InetAddressConverter implements TypeConverter<InetAddress> {
      public InetAddress convert(String var1) throws Exception {
         return InetAddress.getByName(var1);
      }
   }

   @Plugin(
      name = "Integer",
      category = "TypeConverter"
   )
   public static class IntegerConverter implements TypeConverter<Integer> {
      public Integer convert(String var1) {
         return Integer.valueOf(var1);
      }
   }

   @Plugin(
      name = "Level",
      category = "TypeConverter"
   )
   public static class LevelConverter implements TypeConverter<Level> {
      public Level convert(String var1) {
         return Level.valueOf(var1);
      }
   }

   @Plugin(
      name = "Long",
      category = "TypeConverter"
   )
   public static class LongConverter implements TypeConverter<Long> {
      public Long convert(String var1) {
         return Long.valueOf(var1);
      }
   }

   @Plugin(
      name = "Path",
      category = "TypeConverter"
   )
   public static class PathConverter implements TypeConverter<Path> {
      public Path convert(String var1) throws Exception {
         return Paths.get(var1);
      }
   }

   @Plugin(
      name = "Pattern",
      category = "TypeConverter"
   )
   public static class PatternConverter implements TypeConverter<Pattern> {
      public Pattern convert(String var1) {
         return Pattern.compile(var1);
      }
   }

   @Plugin(
      name = "SecurityProvider",
      category = "TypeConverter"
   )
   public static class SecurityProviderConverter implements TypeConverter<Provider> {
      public Provider convert(String var1) {
         return Security.getProvider(var1);
      }
   }

   @Plugin(
      name = "Short",
      category = "TypeConverter"
   )
   public static class ShortConverter implements TypeConverter<Short> {
      public Short convert(String var1) {
         return Short.valueOf(var1);
      }
   }

   @Plugin(
      name = "String",
      category = "TypeConverter"
   )
   public static class StringConverter implements TypeConverter<String> {
      public String convert(String var1) {
         return var1;
      }
   }

   @Plugin(
      name = "URI",
      category = "TypeConverter"
   )
   public static class UriConverter implements TypeConverter<URI> {
      public URI convert(String var1) throws URISyntaxException {
         return new URI(var1);
      }
   }

   @Plugin(
      name = "URL",
      category = "TypeConverter"
   )
   public static class UrlConverter implements TypeConverter<URL> {
      public URL convert(String var1) throws MalformedURLException {
         return new URL(var1);
      }
   }

   @Plugin(
      name = "UUID",
      category = "TypeConverter"
   )
   public static class UuidConverter implements TypeConverter<UUID> {
      public UUID convert(String var1) throws Exception {
         return UUID.fromString(var1);
      }
   }
}
