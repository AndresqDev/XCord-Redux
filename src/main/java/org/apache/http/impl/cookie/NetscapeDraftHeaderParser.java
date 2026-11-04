package org.apache.http.impl.cookie;

import java.util.ArrayList;
import java.util.BitSet;
import org.apache.http.HeaderElement;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.message.BasicHeaderElement;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.ParserCursor;
import org.apache.http.message.TokenParser;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public class NetscapeDraftHeaderParser {
   public static final NetscapeDraftHeaderParser DEFAULT = new NetscapeDraftHeaderParser();
   private static final char PARAM_DELIMITER = ';';
   private static final BitSet TOKEN_DELIMS = TokenParser.INIT_BITSET(61, 59);
   private static final BitSet VALUE_DELIMS = TokenParser.INIT_BITSET(59);
   private final TokenParser tokenParser = TokenParser.INSTANCE;

   public HeaderElement parseHeader(CharArrayBuffer var1, ParserCursor var2) throws ParseException {
      Args.notNull(var1, "Char array buffer");
      Args.notNull(var2, "Parser cursor");
      NameValuePair var3 = this.parseNameValuePair(var1, var2);
      ArrayList var4 = new ArrayList();

      while (!var2.atEnd()) {
         NameValuePair var5 = this.parseNameValuePair(var1, var2);
         var4.add(var5);
      }

      return new BasicHeaderElement(var3.getName(), var3.getValue(), var4.toArray(new NameValuePair[var4.size()]));
   }

   private NameValuePair parseNameValuePair(CharArrayBuffer var1, ParserCursor var2) {
      String var3 = this.tokenParser.parseToken(var1, var2, TOKEN_DELIMS);
      if (var2.atEnd()) {
         return new BasicNameValuePair(var3, null);
      } else {
         char var4 = var1.charAt(var2.getPos());
         var2.updatePos(var2.getPos() + 1);
         if (var4 != '=') {
            return new BasicNameValuePair(var3, null);
         } else {
            String var5 = this.tokenParser.parseToken(var1, var2, VALUE_DELIMS);
            if (!var2.atEnd()) {
               var2.updatePos(var2.getPos() + 1);
            }

            return new BasicNameValuePair(var3, var5);
         }
      }
   }
}
