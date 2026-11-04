package org.codehaus.plexus.util;

import java.io.FilterReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.codehaus.plexus.util.reflection.Reflector;
import org.codehaus.plexus.util.reflection.ReflectorException;

public class LineOrientedInterpolatingReader extends FilterReader {
   public static final String DEFAULT_START_DELIM = "${";
   public static final String DEFAULT_END_DELIM = "}";
   public static final String DEFAULT_ESCAPE_SEQ = "\\";
   private static final char CARRIAGE_RETURN_CHAR = '\r';
   private static final char NEWLINE_CHAR = '\n';
   private final PushbackReader pushbackReader;
   private final Map<String, Object> context;
   private final String startDelim;
   private final String endDelim;
   private final String escapeSeq;
   private final int minExpressionSize;
   private final Reflector reflector;
   private int lineIdx = -1;
   private String line;

   public LineOrientedInterpolatingReader(Reader var1, Map<String, ?> var2, String var3, String var4, String var5) {
      super(var1);
      this.startDelim = var3;
      this.endDelim = var4;
      this.escapeSeq = var5;
      this.minExpressionSize = var3.length() + var4.length() + 1;
      this.context = Collections.unmodifiableMap(var2);
      this.reflector = new Reflector();
      if (var1 instanceof PushbackReader) {
         this.pushbackReader = (PushbackReader)var1;
      } else {
         this.pushbackReader = new PushbackReader(var1, 1);
      }
   }

   public LineOrientedInterpolatingReader(Reader var1, Map<String, ?> var2, String var3, String var4) {
      this(var1, var2, var3, var4, "\\");
   }

   public LineOrientedInterpolatingReader(Reader var1, Map<String, ?> var2) {
      this(var1, var2, "${", "}", "\\");
   }

   @Override
   public int read() throws IOException {
      if (this.line == null || this.lineIdx >= this.line.length()) {
         this.readAndInterpolateLine();
      }

      int var1 = -1;
      if (this.line != null && this.lineIdx < this.line.length()) {
         var1 = this.line.charAt(this.lineIdx++);
      }

      return var1;
   }

   @Override
   public int read(char[] var1, int var2, int var3) throws IOException {
      int var4 = 0;

      for (int var5 = var2; var5 < var2 + var3; var5++) {
         int var6 = this.read();
         if (var6 <= -1) {
            break;
         }

         var1[var5] = (char)var6;
         var4++;
      }

      if (var4 == 0) {
         var4 = -1;
      }

      return var4;
   }

   @Override
   public long skip(long var1) throws IOException {
      long var3 = 0L;

      for (long var5 = 0L; var5 < var1; var5++) {
         int var7 = this.read();
         if (var7 < 0) {
            break;
         }

         var3++;
      }

      return var3;
   }

   private void readAndInterpolateLine() throws IOException {
      String var1 = this.readLine();
      if (var1 != null) {
         Set var2 = this.parseForExpressions(var1);
         Map var3 = this.evaluateExpressions(var2);
         String var4 = this.replaceWithInterpolatedValues(var1, var3);
         if (var4 != null && var4.length() > 0) {
            this.line = var4;
            this.lineIdx = 0;
         }
      } else {
         this.line = null;
         this.lineIdx = -1;
      }
   }

   private String readLine() throws IOException {
      StringBuilder var1 = new StringBuilder(40);
      boolean var3 = false;

      int var2;
      while ((var2 = this.pushbackReader.read()) > -1) {
         char var4 = (char)var2;
         if (var4 == '\r') {
            var3 = true;
            var1.append(var4);
         } else {
            if (var4 == '\n') {
               var1.append(var4);
               break;
            }

            if (var3) {
               this.pushbackReader.unread(var4);
               break;
            }

            var1.append(var4);
         }
      }

      return var1.length() < 1 ? null : var1.toString();
   }

   private String replaceWithInterpolatedValues(String var1, Map<String, Object> var2) {
      String var3 = var1;

      for (Object var5 : var2.entrySet()) {
         Entry var6 = (Entry)var5;
         String var7 = (String)var6.getKey();
         String var8 = String.valueOf(var6.getValue());
         var3 = this.findAndReplaceUnlessEscaped(var3, var7, var8);
      }

      return var3;
   }

   private Map<String, Object> evaluateExpressions(Set<String> var1) {
      TreeMap var2 = new TreeMap();

      for (Object var4 : var1) {
         String var5 = (String)var4;
         String var6 = var5.substring(this.startDelim.length(), var5.length() - this.endDelim.length());
         String[] var7 = var6.split("\\.");
         if (var7.length > 0) {
            Object var8 = this.context.get(var7[0]);
            if (var8 != null) {
               for (int var9 = 1; var9 < var7.length; var9++) {
                  try {
                     var8 = this.reflector.getObjectProperty(var8, var7[var9]);
                     if (var8 == null) {
                        break;
                     }
                  } catch (ReflectorException var11) {
                     var11.printStackTrace();
                     break;
                  }
               }

               var2.put(var5, var8);
            }
         }
      }

      return var2;
   }

   private Set<String> parseForExpressions(String var1) {
      HashSet var2 = new HashSet();
      if (var1 != null) {
         int var3 = -1;

         do {
            int var4 = this.findDelimiter(var1, this.startDelim, var3);
            if (var4 < 0) {
               break;
            }

            int var5 = this.findDelimiter(var1, this.endDelim, var4 + 1);
            if (var5 < 0) {
               break;
            }

            var2.add(var1.substring(var4, var5 + this.endDelim.length()));
            var3 = var5 + 1;
         } while (var3 < var1.length() - this.minExpressionSize);
      }

      return var2;
   }

   private int findDelimiter(String var1, String var2, int var3) {
      int var4 = var3;

      int var5;
      do {
         var5 = var1.indexOf(var2, var4);
         if (var5 < 0) {
            break;
         }

         int var6 = var1.indexOf(this.escapeSeq, var4) + this.escapeSeq.length();
         if (var6 > this.escapeSeq.length() - 1 && var6 == var5) {
            var4 = var5 + 1;
            var5 = -1;
         }
      } while (var5 < 0 && var4 < var1.length() - this.endDelim.length());

      return var5;
   }

   private String findAndReplaceUnlessEscaped(String var1, String var2, String var3) {
      StringBuilder var4 = new StringBuilder((int)((double)var1.length() * 1.5));
      int var5 = -1;

      do {
         int var6 = var1.indexOf(var2, var5 + 1);
         if (var6 <= -1) {
            break;
         }

         if (var5 < 0) {
            var5 = 0;
         }

         var4.append(var1, var5, var6);
         int var7 = var1.indexOf(this.escapeSeq, var5 + 1);
         if (var7 > -1 && var7 + this.escapeSeq.length() == var6) {
            var4.setLength(var4.length() - this.escapeSeq.length());
            var4.append(var2);
         } else {
            var4.append(var3);
         }

         var5 = var6 + var2.length();
      } while (var5 > -1);

      if (var5 < var1.length()) {
         var4.append(var1, var5, var1.length());
      }

      return var4.toString();
   }
}
