package org.apache.http.entity;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.message.BasicHeaderValueFormatter;
import org.apache.http.message.BasicHeaderValueParser;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.ParserCursor;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.TextUtils;

@Contract(
   threading = ThreadingBehavior.IMMUTABLE
)
public final class ContentType implements Serializable {
   private static final long serialVersionUID = -7768694718232371896L;
   public static final ContentType APPLICATION_ATOM_XML = create("application/atom+xml", Consts.ISO_8859_1);
   public static final ContentType APPLICATION_FORM_URLENCODED = create("application/x-www-form-urlencoded", Consts.ISO_8859_1);
   public static final ContentType APPLICATION_JSON = create("application/json", Consts.UTF_8);
   public static final ContentType APPLICATION_OCTET_STREAM = create("application/octet-stream", (Charset)null);
   public static final ContentType APPLICATION_SOAP_XML = create("application/soap+xml", Consts.UTF_8);
   public static final ContentType APPLICATION_SVG_XML = create("application/svg+xml", Consts.ISO_8859_1);
   public static final ContentType APPLICATION_XHTML_XML = create("application/xhtml+xml", Consts.ISO_8859_1);
   public static final ContentType APPLICATION_XML = create("application/xml", Consts.ISO_8859_1);
   public static final ContentType IMAGE_BMP = create("image/bmp");
   public static final ContentType IMAGE_GIF = create("image/gif");
   public static final ContentType IMAGE_JPEG = create("image/jpeg");
   public static final ContentType IMAGE_PNG = create("image/png");
   public static final ContentType IMAGE_SVG = create("image/svg+xml");
   public static final ContentType IMAGE_TIFF = create("image/tiff");
   public static final ContentType IMAGE_WEBP = create("image/webp");
   public static final ContentType MULTIPART_FORM_DATA = create("multipart/form-data", Consts.ISO_8859_1);
   public static final ContentType TEXT_HTML = create("text/html", Consts.ISO_8859_1);
   public static final ContentType TEXT_PLAIN = create("text/plain", Consts.ISO_8859_1);
   public static final ContentType TEXT_XML = create("text/xml", Consts.ISO_8859_1);
   public static final ContentType WILDCARD = create("*/*", (Charset)null);
   private static final Map<String, ContentType> CONTENT_TYPE_MAP;
   public static final ContentType DEFAULT_TEXT;
   public static final ContentType DEFAULT_BINARY;
   private final String mimeType;
   private final Charset charset;
   private final NameValuePair[] params;

   ContentType(String var1, Charset var2) {
      this.mimeType = var1;
      this.charset = var2;
      this.params = null;
   }

   ContentType(String var1, Charset var2, NameValuePair[] var3) {
      this.mimeType = var1;
      this.charset = var2;
      this.params = var3;
   }

   public String getMimeType() {
      return this.mimeType;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public String getParameter(String var1) {
      Args.notEmpty(var1, "Parameter name");
      if (this.params == null) {
         return null;
      } else {
         for (NameValuePair var5 : this.params) {
            if (var5.getName().equalsIgnoreCase(var1)) {
               return var5.getValue();
            }
         }

         return null;
      }
   }

   @Override
   public String toString() {
      CharArrayBuffer var1 = new CharArrayBuffer(64);
      var1.append(this.mimeType);
      if (this.params != null) {
         var1.append("; ");
         BasicHeaderValueFormatter.INSTANCE.formatParameters(var1, this.params, false);
      } else if (this.charset != null) {
         var1.append("; charset=");
         var1.append(this.charset.name());
      }

      return var1.toString();
   }

   private static boolean valid(String var0) {
      for (int var1 = 0; var1 < var0.length(); var1++) {
         char var2 = var0.charAt(var1);
         if (var2 == '"' || var2 == ',' || var2 == ';') {
            return false;
         }
      }

      return true;
   }

   public static ContentType create(String var0, Charset var1) {
      String var2 = Args.notBlank(var0, "MIME type").toLowerCase(Locale.ROOT);
      Args.check(valid(var2), "MIME type may not contain reserved characters");
      return new ContentType(var2, var1);
   }

   public static ContentType create(String var0) {
      return create(var0, (Charset)null);
   }

   public static ContentType create(String var0, String var1) throws UnsupportedCharsetException {
      return create(var0, !TextUtils.isBlank(var1) ? Charset.forName(var1) : null);
   }

   private static ContentType create(HeaderElement var0, boolean var1) {
      return create(var0.getName(), var0.getParameters(), var1);
   }

   private static ContentType create(String var0, NameValuePair[] var1, boolean var2) {
      Charset var3 = null;

      for (NameValuePair var7 : var1) {
         if (var7.getName().equalsIgnoreCase("charset")) {
            String var8 = var7.getValue();
            if (TextUtils.isBlank(var8)) {
               break;
            }

            try {
               var3 = Charset.forName(var8);
               break;
            } catch (UnsupportedCharsetException var10) {
               if (!var2) {
                  break;
               }

               throw var10;
            }
         }
      }

      return new ContentType(var0, var3, var1 != null && var1.length > 0 ? var1 : null);
   }

   public static ContentType create(String var0, NameValuePair... var1) throws UnsupportedCharsetException {
      String var2 = Args.notBlank(var0, "MIME type").toLowerCase(Locale.ROOT);
      Args.check(valid(var2), "MIME type may not contain reserved characters");
      return create(var0, var1, true);
   }

   public static ContentType parse(String var0) throws ParseException, UnsupportedCharsetException {
      Args.notNull(var0, "Content type");
      CharArrayBuffer var1 = new CharArrayBuffer(var0.length());
      var1.append(var0);
      ParserCursor var2 = new ParserCursor(0, var0.length());
      HeaderElement[] var3 = BasicHeaderValueParser.INSTANCE.parseElements(var1, var2);
      if (var3.length > 0) {
         return create(var3[0], true);
      } else {
         throw new ParseException("Invalid content type: " + var0);
      }
   }

   public static ContentType get(HttpEntity var0) throws ParseException, UnsupportedCharsetException {
      if (var0 == null) {
         return null;
      } else {
         Header var1 = var0.getContentType();
         if (var1 != null) {
            HeaderElement[] var2 = var1.getElements();
            if (var2.length > 0) {
               return create(var2[0], true);
            }
         }

         return null;
      }
   }

   public static ContentType getLenient(HttpEntity var0) {
      if (var0 == null) {
         return null;
      } else {
         Header var1 = var0.getContentType();
         if (var1 != null) {
            try {
               HeaderElement[] var2 = var1.getElements();
               if (var2.length > 0) {
                  return create(var2[0], false);
               }
            } catch (ParseException var3) {
            }
         }

         return null;
      }
   }

   public static ContentType getOrDefault(HttpEntity var0) throws ParseException, UnsupportedCharsetException {
      ContentType var1 = get(var0);
      return var1 != null ? var1 : DEFAULT_TEXT;
   }

   public static ContentType getLenientOrDefault(HttpEntity var0) throws ParseException, UnsupportedCharsetException {
      ContentType var1 = get(var0);
      return var1 != null ? var1 : DEFAULT_TEXT;
   }

   public static ContentType getByMimeType(String var0) {
      return var0 == null ? null : CONTENT_TYPE_MAP.get(var0);
   }

   public ContentType withCharset(Charset var1) {
      return create(this.getMimeType(), var1);
   }

   public ContentType withCharset(String var1) {
      return create(this.getMimeType(), var1);
   }

   public ContentType withParameters(NameValuePair... var1) throws UnsupportedCharsetException {
      if (var1.length == 0) {
         return this;
      } else {
         LinkedHashMap var2 = new LinkedHashMap();
         if (this.params != null) {
            for (NameValuePair var6 : this.params) {
               var2.put(var6.getName(), var6.getValue());
            }
         }

         for (NameValuePair var13 : var1) {
            var2.put(var13.getName(), var13.getValue());
         }

         ArrayList var8 = new ArrayList(var2.size() + 1);
         if (this.charset != null && !var2.containsKey("charset")) {
            var8.add(new BasicNameValuePair("charset", this.charset.name()));
         }

         for (Entry var12 : var2.entrySet()) {
            var8.add(new BasicNameValuePair((String)var12.getKey(), (String)var12.getValue()));
         }

         return create(this.getMimeType(), var8.toArray(new NameValuePair[var8.size()]), true);
      }
   }

   static {
      ContentType[] var0 = new ContentType[]{
         APPLICATION_ATOM_XML,
         APPLICATION_FORM_URLENCODED,
         APPLICATION_JSON,
         APPLICATION_SVG_XML,
         APPLICATION_XHTML_XML,
         APPLICATION_XML,
         IMAGE_BMP,
         IMAGE_GIF,
         IMAGE_JPEG,
         IMAGE_PNG,
         IMAGE_SVG,
         IMAGE_TIFF,
         IMAGE_WEBP,
         MULTIPART_FORM_DATA,
         TEXT_HTML,
         TEXT_PLAIN,
         TEXT_XML
      };
      HashMap var1 = new HashMap();

      for (ContentType var5 : var0) {
         var1.put(var5.getMimeType(), var5);
      }

      CONTENT_TYPE_MAP = Collections.unmodifiableMap(var1);
      DEFAULT_TEXT = TEXT_PLAIN;
      DEFAULT_BINARY = APPLICATION_OCTET_STREAM;
   }
}
