package org.codehaus.plexus.util.xml;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XmlReader extends Reader {
   private static final int BUFFER_SIZE = 4096;
   private static final String UTF_8 = "UTF-8";
   private static final String US_ASCII = "US-ASCII";
   private static final String UTF_16BE = "UTF-16BE";
   private static final String UTF_16LE = "UTF-16LE";
   private static final String UTF_16 = "UTF-16";
   private static final String EBCDIC = "CP1047";
   private static String _staticDefaultEncoding = null;
   private Reader _reader;
   private String _encoding;
   private String _defaultEncoding;
   private static final Pattern CHARSET_PATTERN = Pattern.compile("charset=([.[^; ]]*)");
   static final Pattern ENCODING_PATTERN = Pattern.compile("<\\?xml.*encoding[\\s]*=[\\s]*((?:\".[^\"]*\")|(?:'.[^']*'))", 8);
   private static final MessageFormat RAW_EX_1 = new MessageFormat("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] encoding mismatch");
   private static final MessageFormat RAW_EX_2 = new MessageFormat("Invalid encoding, BOM [{0}] XML guess [{1}] XML prolog [{2}] unknown BOM");
   private static final MessageFormat HTTP_EX_1 = new MessageFormat(
      "Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], BOM must be NULL"
   );
   private static final MessageFormat HTTP_EX_2 = new MessageFormat(
      "Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], encoding mismatch"
   );
   private static final MessageFormat HTTP_EX_3 = new MessageFormat(
      "Invalid encoding, CT-MIME [{0}] CT-Enc [{1}] BOM [{2}] XML guess [{3}] XML prolog [{4}], Invalid MIME"
   );

   public static void setDefaultEncoding(String var0) {
      _staticDefaultEncoding = var0;
   }

   public static String getDefaultEncoding() {
      return _staticDefaultEncoding;
   }

   public XmlReader(File var1) throws IOException {
      this(new FileInputStream(var1));
   }

   public XmlReader(InputStream var1) throws IOException {
      this(var1, true);
   }

   public XmlReader(InputStream var1, boolean var2) throws IOException, XmlStreamReaderException {
      this._defaultEncoding = _staticDefaultEncoding;

      try {
         this.doRawStream(var1, var2);
      } catch (XmlStreamReaderException var4) {
         if (!var2) {
            throw var4;
         }

         this.doLenientDetection(null, var4);
      }
   }

   public XmlReader(URL var1) throws IOException {
      this(var1.openConnection());
   }

   public XmlReader(URLConnection var1) throws IOException {
      this._defaultEncoding = _staticDefaultEncoding;
      boolean var2 = true;
      if (var1 instanceof HttpURLConnection) {
         try {
            this.doHttpStream(var1.getInputStream(), var1.getContentType(), var2);
         } catch (XmlStreamReaderException var6) {
            this.doLenientDetection(var1.getContentType(), var6);
         }
      } else if (var1.getContentType() != null) {
         try {
            this.doHttpStream(var1.getInputStream(), var1.getContentType(), var2);
         } catch (XmlStreamReaderException var5) {
            this.doLenientDetection(var1.getContentType(), var5);
         }
      } else {
         try {
            this.doRawStream(var1.getInputStream(), var2);
         } catch (XmlStreamReaderException var4) {
            this.doLenientDetection(null, var4);
         }
      }
   }

   public XmlReader(InputStream var1, String var2) throws IOException {
      this(var1, var2, true);
   }

   public XmlReader(InputStream var1, String var2, boolean var3, String var4) throws IOException, XmlStreamReaderException {
      this._defaultEncoding = var4 == null ? _staticDefaultEncoding : var4;

      try {
         this.doHttpStream(var1, var2, var3);
      } catch (XmlStreamReaderException var6) {
         if (!var3) {
            throw var6;
         }

         this.doLenientDetection(var2, var6);
      }
   }

   public XmlReader(InputStream var1, String var2, boolean var3) throws IOException, XmlStreamReaderException {
      this(var1, var2, var3, null);
   }

   private void doLenientDetection(String var1, XmlStreamReaderException var2) throws IOException {
      if (var1 != null && var1.startsWith("text/html")) {
         var1 = var1.substring("text/html".length());
         var1 = "text/xml" + var1;

         try {
            this.doHttpStream(var2.getInputStream(), var1, true);
            var2 = null;
         } catch (XmlStreamReaderException var4) {
            var2 = var4;
         }
      }

      if (var2 != null) {
         String var3 = var2.getXmlEncoding();
         if (var3 == null) {
            var3 = var2.getContentTypeEncoding();
         }

         if (var3 == null) {
            var3 = this._defaultEncoding == null ? "UTF-8" : this._defaultEncoding;
         }

         this.prepareReader(var2.getInputStream(), var3);
      }
   }

   public String getEncoding() {
      return this._encoding;
   }

   @Override
   public int read(char[] var1, int var2, int var3) throws IOException {
      return this._reader.read(var1, var2, var3);
   }

   @Override
   public void close() throws IOException {
      this._reader.close();
   }

   private void doRawStream(InputStream var1, boolean var2) throws IOException {
      BufferedInputStream var3 = new BufferedInputStream(var1, 4096);
      String var4 = getBOMEncoding(var3);
      String var5 = getXMLGuessEncoding(var3);
      String var6 = getXmlProlog(var3, var5);
      String var7 = this.calculateRawEncoding(var4, var5, var6, var3);
      this.prepareReader(var3, var7);
   }

   private void doHttpStream(InputStream var1, String var2, boolean var3) throws IOException {
      BufferedInputStream var4 = new BufferedInputStream(var1, 4096);
      String var5 = getContentTypeMime(var2);
      String var6 = getContentTypeEncoding(var2);
      String var7 = getBOMEncoding(var4);
      String var8 = getXMLGuessEncoding(var4);
      String var9 = getXmlProlog(var4, var8);
      String var10 = this.calculateHttpEncoding(var5, var6, var7, var8, var9, var4, var3);
      this.prepareReader(var4, var10);
   }

   private void prepareReader(InputStream var1, String var2) throws IOException {
      this._reader = new InputStreamReader(var1, var2);
      this._encoding = var2;
   }

   private String calculateRawEncoding(String var1, String var2, String var3, InputStream var4) throws IOException {
      String var5;
      if (var1 == null) {
         if (var2 != null && var3 != null) {
            if (!var3.equals("UTF-16") || !var2.equals("UTF-16BE") && !var2.equals("UTF-16LE")) {
               var5 = var3;
            } else {
               var5 = var2;
            }
         } else {
            var5 = this._defaultEncoding == null ? "UTF-8" : this._defaultEncoding;
         }
      } else if (var1.equals("UTF-8")) {
         if (var2 != null && !var2.equals("UTF-8")) {
            throw new XmlStreamReaderException(RAW_EX_1.format(new Object[]{var1, var2, var3}), var1, var2, var3, var4);
         }

         if (var3 != null && !var3.equals("UTF-8")) {
            throw new XmlStreamReaderException(RAW_EX_1.format(new Object[]{var1, var2, var3}), var1, var2, var3, var4);
         }

         var5 = "UTF-8";
      } else {
         if (!var1.equals("UTF-16BE") && !var1.equals("UTF-16LE")) {
            throw new XmlStreamReaderException(RAW_EX_2.format(new Object[]{var1, var2, var3}), var1, var2, var3, var4);
         }

         if (var2 != null && !var2.equals(var1)) {
            throw new IOException(RAW_EX_1.format(new Object[]{var1, var2, var3}));
         }

         if (var3 != null && !var3.equals("UTF-16") && !var3.equals(var1)) {
            throw new XmlStreamReaderException(RAW_EX_1.format(new Object[]{var1, var2, var3}), var1, var2, var3, var4);
         }

         var5 = var1;
      }

      return var5;
   }

   private String calculateHttpEncoding(String var1, String var2, String var3, String var4, String var5, InputStream var6, boolean var7) throws IOException {
      String var8;
      if (var7 & var5 != null) {
         var8 = var5;
      } else {
         boolean var9 = isAppXml(var1);
         boolean var10 = isTextXml(var1);
         if (!var9 && !var10) {
            throw new XmlStreamReaderException(HTTP_EX_3.format(new Object[]{var1, var2, var3, var4, var5}), var1, var2, var3, var4, var5, var6);
         }

         if (var2 == null) {
            if (var9) {
               var8 = this.calculateRawEncoding(var3, var4, var5, var6);
            } else {
               var8 = this._defaultEncoding == null ? "US-ASCII" : this._defaultEncoding;
            }
         } else {
            if (var3 != null && (var2.equals("UTF-16BE") || var2.equals("UTF-16LE"))) {
               throw new XmlStreamReaderException(HTTP_EX_1.format(new Object[]{var1, var2, var3, var4, var5}), var1, var2, var3, var4, var5, var6);
            }

            if (var2.equals("UTF-16")) {
               if (var3 == null || !var3.startsWith("UTF-16")) {
                  throw new XmlStreamReaderException(HTTP_EX_2.format(new Object[]{var1, var2, var3, var4, var5}), var1, var2, var3, var4, var5, var6);
               }

               var8 = var3;
            } else {
               var8 = var2;
            }
         }
      }

      return var8;
   }

   private static String getContentTypeMime(String var0) {
      String var1 = null;
      if (var0 != null) {
         int var2 = var0.indexOf(";");
         var1 = (var2 == -1 ? var0 : var0.substring(0, var2)).trim();
      }

      return var1;
   }

   private static String getContentTypeEncoding(String var0) {
      String var1 = null;
      if (var0 != null) {
         int var2 = var0.indexOf(";");
         if (var2 > -1) {
            String var3 = var0.substring(var2 + 1);
            Matcher var4 = CHARSET_PATTERN.matcher(var3);
            var1 = var4.find() ? var4.group(1) : null;
            var1 = var1 != null ? var1.toUpperCase(Locale.ENGLISH) : null;
         }
      }

      return var1;
   }

   private static String getBOMEncoding(BufferedInputStream var0) throws IOException {
      String var1 = null;
      int[] var2 = new int[3];
      var0.mark(3);
      var2[0] = var0.read();
      var2[1] = var0.read();
      var2[2] = var0.read();
      if (var2[0] == 254 && var2[1] == 255) {
         var1 = "UTF-16BE";
         var0.reset();
         var0.read();
         var0.read();
      } else if (var2[0] == 255 && var2[1] == 254) {
         var1 = "UTF-16LE";
         var0.reset();
         var0.read();
         var0.read();
      } else if (var2[0] == 239 && var2[1] == 187 && var2[2] == 191) {
         var1 = "UTF-8";
      } else {
         var0.reset();
      }

      return var1;
   }

   private static String getXMLGuessEncoding(BufferedInputStream var0) throws IOException {
      String var1 = null;
      int[] var2 = new int[4];
      var0.mark(4);
      var2[0] = var0.read();
      var2[1] = var0.read();
      var2[2] = var0.read();
      var2[3] = var0.read();
      var0.reset();
      if (var2[0] == 0 && var2[1] == 60 && var2[2] == 0 && var2[3] == 63) {
         var1 = "UTF-16BE";
      } else if (var2[0] == 60 && var2[1] == 0 && var2[2] == 63 && var2[3] == 0) {
         var1 = "UTF-16LE";
      } else if (var2[0] == 60 && var2[1] == 63 && var2[2] == 120 && var2[3] == 109) {
         var1 = "UTF-8";
      } else if (var2[0] == 76 && var2[1] == 111 && var2[2] == 167 && var2[3] == 148) {
         var1 = "CP1047";
      }

      return var1;
   }

   private static String getXmlProlog(BufferedInputStream var0, String var1) throws IOException {
      String var2 = null;
      if (var1 != null) {
         byte[] var3 = new byte[4096];
         var0.mark(4096);
         int var4 = 0;
         int var5 = 4096;
         int var6 = var0.read(var3, var4, var5);
         int var7 = -1;

         String var8;
         for (var8 = null; var6 != -1 && var7 == -1 && var4 < 4096; var7 = var8.indexOf(62)) {
            var4 += var6;
            var5 -= var6;
            var6 = var0.read(var3, var4, var5);
            var8 = new String(var3, 0, var4, var1);
         }

         if (var7 == -1) {
            if (var6 == -1) {
               throw new IOException("Unexpected end of XML stream");
            }

            throw new IOException("XML prolog or ROOT element not found on first " + var4 + " bytes");
         }

         if (var4 > 0) {
            var0.reset();
            BufferedReader var10 = new BufferedReader(new StringReader(var8.substring(0, var7 + 1)));
            StringBuilder var11 = new StringBuilder();

            for (String var12 = var10.readLine(); var12 != null; var12 = var10.readLine()) {
               var11.append(var12);
            }

            Matcher var13 = ENCODING_PATTERN.matcher(var11);
            if (var13.find()) {
               var2 = var13.group(1).toUpperCase(Locale.ENGLISH);
               var2 = var2.substring(1, var2.length() - 1);
            }
         }
      }

      return var2;
   }

   private static boolean isAppXml(String var0) {
      return var0 != null
         && (
            var0.equals("application/xml")
               || var0.equals("application/xml-dtd")
               || var0.equals("application/xml-external-parsed-entity")
               || var0.startsWith("application/") && var0.endsWith("+xml")
         );
   }

   private static boolean isTextXml(String var0) {
      return var0 != null && (var0.equals("text/xml") || var0.equals("text/xml-external-parsed-entity") || var0.startsWith("text/") && var0.endsWith("+xml"));
   }
}
