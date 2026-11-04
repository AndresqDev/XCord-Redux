package org.apache.commons.lang3;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;
import org.apache.commons.lang3.text.translate.OctalUnescaper;
import org.apache.commons.lang3.text.translate.UnicodeUnescaper;
import org.apache.commons.lang3.text.translate.UnicodeUnpairedSurrogateRemover;

@Deprecated
public class StringEscapeUtils {
   public static final CharSequenceTranslator ESCAPE_JAVA = new LookupTranslator(new String[]{"\"", "\\\""}, new String[]{"\\", "\\\\"})
      .with(new CharSequenceTranslator[]{new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE())})
      .with(JavaUnicodeEscaper.outsideOf(32, 127));
   public static final CharSequenceTranslator ESCAPE_ECMASCRIPT = new AggregateTranslator(
      new LookupTranslator(new String[]{"'", "\\'"}, new String[]{"\"", "\\\""}, new String[]{"\\", "\\\\"}, new String[]{"/", "\\/"}),
      new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE()),
      JavaUnicodeEscaper.outsideOf(32, 127)
   );
   public static final CharSequenceTranslator ESCAPE_JSON = new AggregateTranslator(
      new LookupTranslator(new String[]{"\"", "\\\""}, new String[]{"\\", "\\\\"}, new String[]{"/", "\\/"}),
      new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE()),
      JavaUnicodeEscaper.outsideOf(32, 127)
   );
   @Deprecated
   public static final CharSequenceTranslator ESCAPE_XML = new AggregateTranslator(
      new LookupTranslator(EntityArrays.BASIC_ESCAPE()), new LookupTranslator(EntityArrays.APOS_ESCAPE())
   );
   public static final CharSequenceTranslator ESCAPE_XML10 = new AggregateTranslator(
      new LookupTranslator(EntityArrays.BASIC_ESCAPE()),
      new LookupTranslator(EntityArrays.APOS_ESCAPE()),
      new LookupTranslator(
         new String[]{"\u0000", ""},
         new String[]{"\u0001", ""},
         new String[]{"\u0002", ""},
         new String[]{"\u0003", ""},
         new String[]{"\u0004", ""},
         new String[]{"\u0005", ""},
         new String[]{"\u0006", ""},
         new String[]{"\u0007", ""},
         new String[]{"\b", ""},
         new String[]{"\u000b", ""},
         new String[]{"\f", ""},
         new String[]{"\u000e", ""},
         new String[]{"\u000f", ""},
         new String[]{"\u0010", ""},
         new String[]{"\u0011", ""},
         new String[]{"\u0012", ""},
         new String[]{"\u0013", ""},
         new String[]{"\u0014", ""},
         new String[]{"\u0015", ""},
         new String[]{"\u0016", ""},
         new String[]{"\u0017", ""},
         new String[]{"\u0018", ""},
         new String[]{"\u0019", ""},
         new String[]{"\u001a", ""},
         new String[]{"\u001b", ""},
         new String[]{"\u001c", ""},
         new String[]{"\u001d", ""},
         new String[]{"\u001e", ""},
         new String[]{"\u001f", ""},
         new String[]{"\ufffe", ""},
         new String[]{"\uffff", ""}
      ),
      NumericEntityEscaper.between(127, 132),
      NumericEntityEscaper.between(134, 159),
      new UnicodeUnpairedSurrogateRemover()
   );
   public static final CharSequenceTranslator ESCAPE_XML11 = new AggregateTranslator(
      new LookupTranslator(EntityArrays.BASIC_ESCAPE()),
      new LookupTranslator(EntityArrays.APOS_ESCAPE()),
      new LookupTranslator(
         new String[]{"\u0000", ""}, new String[]{"\u000b", "&#11;"}, new String[]{"\f", "&#12;"}, new String[]{"\ufffe", ""}, new String[]{"\uffff", ""}
      ),
      NumericEntityEscaper.between(1, 8),
      NumericEntityEscaper.between(14, 31),
      NumericEntityEscaper.between(127, 132),
      NumericEntityEscaper.between(134, 159),
      new UnicodeUnpairedSurrogateRemover()
   );
   public static final CharSequenceTranslator ESCAPE_HTML3 = new AggregateTranslator(
      new LookupTranslator(EntityArrays.BASIC_ESCAPE()), new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE())
   );
   public static final CharSequenceTranslator ESCAPE_HTML4 = new AggregateTranslator(
      new LookupTranslator(EntityArrays.BASIC_ESCAPE()),
      new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE()),
      new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE())
   );
   public static final CharSequenceTranslator ESCAPE_CSV = new StringEscapeUtils.CsvEscaper();
   public static final CharSequenceTranslator UNESCAPE_JAVA = new AggregateTranslator(
      new OctalUnescaper(),
      new UnicodeUnescaper(),
      new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_UNESCAPE()),
      new LookupTranslator(new String[]{"\\\\", "\\"}, new String[]{"\\\"", "\""}, new String[]{"\\'", "'"}, new String[]{"\\", ""})
   );
   public static final CharSequenceTranslator UNESCAPE_ECMASCRIPT = UNESCAPE_JAVA;
   public static final CharSequenceTranslator UNESCAPE_JSON = UNESCAPE_JAVA;
   public static final CharSequenceTranslator UNESCAPE_HTML3 = new AggregateTranslator(
      new LookupTranslator(EntityArrays.BASIC_UNESCAPE()), new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE()), new NumericEntityUnescaper()
   );
   public static final CharSequenceTranslator UNESCAPE_HTML4 = new AggregateTranslator(
      new LookupTranslator(EntityArrays.BASIC_UNESCAPE()),
      new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE()),
      new LookupTranslator(EntityArrays.HTML40_EXTENDED_UNESCAPE()),
      new NumericEntityUnescaper()
   );
   public static final CharSequenceTranslator UNESCAPE_XML = new AggregateTranslator(
      new LookupTranslator(EntityArrays.BASIC_UNESCAPE()), new LookupTranslator(EntityArrays.APOS_UNESCAPE()), new NumericEntityUnescaper()
   );
   public static final CharSequenceTranslator UNESCAPE_CSV = new StringEscapeUtils.CsvUnescaper();

   public static final String escapeJava(String var0) {
      return ESCAPE_JAVA.translate(var0);
   }

   public static final String escapeEcmaScript(String var0) {
      return ESCAPE_ECMASCRIPT.translate(var0);
   }

   public static final String escapeJson(String var0) {
      return ESCAPE_JSON.translate(var0);
   }

   public static final String unescapeJava(String var0) {
      return UNESCAPE_JAVA.translate(var0);
   }

   public static final String unescapeEcmaScript(String var0) {
      return UNESCAPE_ECMASCRIPT.translate(var0);
   }

   public static final String unescapeJson(String var0) {
      return UNESCAPE_JSON.translate(var0);
   }

   public static final String escapeHtml4(String var0) {
      return ESCAPE_HTML4.translate(var0);
   }

   public static final String escapeHtml3(String var0) {
      return ESCAPE_HTML3.translate(var0);
   }

   public static final String unescapeHtml4(String var0) {
      return UNESCAPE_HTML4.translate(var0);
   }

   public static final String unescapeHtml3(String var0) {
      return UNESCAPE_HTML3.translate(var0);
   }

   @Deprecated
   public static final String escapeXml(String var0) {
      return ESCAPE_XML.translate(var0);
   }

   public static String escapeXml10(String var0) {
      return ESCAPE_XML10.translate(var0);
   }

   public static String escapeXml11(String var0) {
      return ESCAPE_XML11.translate(var0);
   }

   public static final String unescapeXml(String var0) {
      return UNESCAPE_XML.translate(var0);
   }

   public static final String escapeCsv(String var0) {
      return ESCAPE_CSV.translate(var0);
   }

   public static final String unescapeCsv(String var0) {
      return UNESCAPE_CSV.translate(var0);
   }

   static class CsvEscaper extends CharSequenceTranslator {
      private static final char CSV_DELIMITER = ',';
      private static final char CSV_QUOTE = '"';
      private static final String CSV_QUOTE_STR = String.valueOf('"');
      private static final char[] CSV_SEARCH_CHARS = new char[]{',', '"', '\r', '\n'};

      @Override
      public int translate(CharSequence var1, int var2, Writer var3) throws IOException {
         if (var2 != 0) {
            throw new IllegalStateException("CsvEscaper should never reach the [1] index");
         } else {
            if (StringUtils.containsNone(var1.toString(), CSV_SEARCH_CHARS)) {
               var3.write(var1.toString());
            } else {
               var3.write(34);
               var3.write(StringUtils.replace(var1.toString(), CSV_QUOTE_STR, CSV_QUOTE_STR + CSV_QUOTE_STR));
               var3.write(34);
            }

            return Character.codePointCount(var1, 0, var1.length());
         }
      }
   }

   static class CsvUnescaper extends CharSequenceTranslator {
      private static final char CSV_DELIMITER = ',';
      private static final char CSV_QUOTE = '"';
      private static final String CSV_QUOTE_STR = String.valueOf('"');
      private static final char[] CSV_SEARCH_CHARS = new char[]{',', '"', '\r', '\n'};

      @Override
      public int translate(CharSequence var1, int var2, Writer var3) throws IOException {
         if (var2 != 0) {
            throw new IllegalStateException("CsvUnescaper should never reach the [1] index");
         } else if (var1.charAt(0) == '"' && var1.charAt(var1.length() - 1) == '"') {
            String var4 = var1.subSequence(1, var1.length() - 1).toString();
            if (StringUtils.containsAny(var4, CSV_SEARCH_CHARS)) {
               var3.write(StringUtils.replace(var4, CSV_QUOTE_STR + CSV_QUOTE_STR, CSV_QUOTE_STR));
            } else {
               var3.write(var1.toString());
            }

            return Character.codePointCount(var1, 0, var1.length());
         } else {
            var3.write(var1.toString());
            return Character.codePointCount(var1, 0, var1.length());
         }
      }
   }
}
