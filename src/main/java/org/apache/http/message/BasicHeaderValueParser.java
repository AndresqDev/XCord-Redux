package org.apache.http.message;

import java.util.ArrayList;
import java.util.BitSet;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class BasicHeaderValueParser implements HeaderValueParser {
   @Deprecated
   public static final BasicHeaderValueParser DEFAULT = new BasicHeaderValueParser();
   public static final BasicHeaderValueParser INSTANCE = new BasicHeaderValueParser();
   private static final char PARAM_DELIMITER = ';';
   private static final char ELEM_DELIMITER = ',';
   private static final BitSet TOKEN_DELIMS = TokenParser.INIT_BITSET(61, 59, 44);
   private static final BitSet VALUE_DELIMS = TokenParser.INIT_BITSET(59, 44);
   private final TokenParser tokenParser = TokenParser.INSTANCE;

   public static HeaderElement[] parseElements(String var0, HeaderValueParser var1) throws ParseException {
      Args.notNull(var0, "Value");
      CharArrayBuffer var2 = new CharArrayBuffer(var0.length());
      var2.append(var0);
      ParserCursor var3 = new ParserCursor(0, var0.length());
      return ((HeaderValueParser)(var1 != null ? var1 : INSTANCE)).parseElements(var2, var3);
   }

   @Override
   public HeaderElement[] parseElements(CharArrayBuffer var1, ParserCursor var2) {
      Args.notNull(var1, "Char array buffer");
      Args.notNull(var2, "Parser cursor");
      ArrayList var3 = new ArrayList();

      while (!var2.atEnd()) {
         HeaderElement var4 = this.parseHeaderElement(var1, var2);
         if (!var4.getName().isEmpty() || var4.getValue() != null) {
            var3.add(var4);
         }
      }

      return var3.toArray(new HeaderElement[var3.size()]);
   }

   public static HeaderElement parseHeaderElement(String var0, HeaderValueParser var1) throws ParseException {
      Args.notNull(var0, "Value");
      CharArrayBuffer var2 = new CharArrayBuffer(var0.length());
      var2.append(var0);
      ParserCursor var3 = new ParserCursor(0, var0.length());
      return ((HeaderValueParser)(var1 != null ? var1 : INSTANCE)).parseHeaderElement(var2, var3);
   }

   @Override
   public HeaderElement parseHeaderElement(CharArrayBuffer var1, ParserCursor var2) {
      Args.notNull(var1, "Char array buffer");
      Args.notNull(var2, "Parser cursor");
      NameValuePair var3 = this.parseNameValuePair(var1, var2);
      NameValuePair[] var4 = null;
      if (!var2.atEnd()) {
         char var5 = var1.charAt(var2.getPos() - 1);
         if (var5 != ',') {
            var4 = this.parseParameters(var1, var2);
         }
      }

      return this.createHeaderElement(var3.getName(), var3.getValue(), var4);
   }

   protected HeaderElement createHeaderElement(String var1, String var2, NameValuePair[] var3) {
      return new BasicHeaderElement(var1, var2, var3);
   }

   public static NameValuePair[] parseParameters(String var0, HeaderValueParser var1) throws ParseException {
      Args.notNull(var0, "Value");
      CharArrayBuffer var2 = new CharArrayBuffer(var0.length());
      var2.append(var0);
      ParserCursor var3 = new ParserCursor(0, var0.length());
      return ((HeaderValueParser)(var1 != null ? var1 : INSTANCE)).parseParameters(var2, var3);
   }

   @Override
   public NameValuePair[] parseParameters(CharArrayBuffer var1, ParserCursor var2) {
      Args.notNull(var1, "Char array buffer");
      Args.notNull(var2, "Parser cursor");
      this.tokenParser.skipWhiteSpace(var1, var2);
      ArrayList var3 = new ArrayList();

      while (!var2.atEnd()) {
         NameValuePair var4 = this.parseNameValuePair(var1, var2);
         var3.add(var4);
         char var5 = var1.charAt(var2.getPos() - 1);
         if (var5 == ',') {
            break;
         }
      }

      return var3.toArray(new NameValuePair[var3.size()]);
   }

   public static NameValuePair parseNameValuePair(String var0, HeaderValueParser var1) throws ParseException {
      Args.notNull(var0, "Value");
      CharArrayBuffer var2 = new CharArrayBuffer(var0.length());
      var2.append(var0);
      ParserCursor var3 = new ParserCursor(0, var0.length());
      return ((HeaderValueParser)(var1 != null ? var1 : INSTANCE)).parseNameValuePair(var2, var3);
   }

   @Override
   public NameValuePair parseNameValuePair(CharArrayBuffer var1, ParserCursor var2) {
      Args.notNull(var1, "Char array buffer");
      Args.notNull(var2, "Parser cursor");
      String var3 = this.tokenParser.parseToken(var1, var2, TOKEN_DELIMS);
      if (var2.atEnd()) {
         return new BasicNameValuePair(var3, null);
      } else {
         char var4 = var1.charAt(var2.getPos());
         var2.updatePos(var2.getPos() + 1);
         if (var4 != '=') {
            return this.createNameValuePair(var3, null);
         } else {
            String var5 = this.tokenParser.parseValue(var1, var2, VALUE_DELIMS);
            if (!var2.atEnd()) {
               var2.updatePos(var2.getPos() + 1);
            }

            return this.createNameValuePair(var3, var5);
         }
      }
   }

   @Deprecated
   public NameValuePair parseNameValuePair(CharArrayBuffer var1, ParserCursor var2, char[] var3) {
      Args.notNull(var1, "Char array buffer");
      Args.notNull(var2, "Parser cursor");
      BitSet var4 = new BitSet();
      if (var3 != null) {
         for (char var8 : var3) {
            var4.set(var8);
         }
      }

      var4.set(61);
      String var9 = this.tokenParser.parseToken(var1, var2, var4);
      if (var2.atEnd()) {
         return new BasicNameValuePair(var9, null);
      } else {
         char var10 = var1.charAt(var2.getPos());
         var2.updatePos(var2.getPos() + 1);
         if (var10 != '=') {
            return this.createNameValuePair(var9, null);
         } else {
            var4.clear(61);
            String var11 = this.tokenParser.parseValue(var1, var2, var4);
            if (!var2.atEnd()) {
               var2.updatePos(var2.getPos() + 1);
            }

            return this.createNameValuePair(var9, var11);
         }
      }
   }

   protected NameValuePair createNameValuePair(String var1, String var2) {
      return new BasicNameValuePair(var1, var2);
   }
}
