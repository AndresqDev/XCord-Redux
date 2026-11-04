package org.codehaus.plexus.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.MXParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParser;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class XmlUtil {
   public static final int DEFAULT_INDENTATION_SIZE = 2;
   public static final String DEFAULT_LINE_SEPARATOR = System.getProperty("line.separator");

   public static boolean isXml(File var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("f could not be null.");
      } else if (!var0.isFile()) {
         throw new IllegalArgumentException("The file '" + var0.getAbsolutePath() + "' is not a file.");
      } else {
         XmlStreamReader var1 = null;

         boolean var3;
         try {
            var1 = ReaderFactory.newXmlReader(var0);
            MXParser var2 = new MXParser();
            var2.setInput(var1);
            var2.nextToken();
            var1.close();
            var1 = null;
            return true;
         } catch (Exception var7) {
            var3 = false;
         } finally {
            IOUtil.close(var1);
         }

         return var3;
      }
   }

   public static void prettyFormat(Reader var0, Writer var1) throws IOException {
      prettyFormat(var0, var1, 2, DEFAULT_LINE_SEPARATOR);
   }

   public static void prettyFormat(Reader var0, Writer var1, int var2, String var3) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("The reader is null");
      } else if (var1 == null) {
         throw new IllegalArgumentException("The writer is null");
      } else {
         if (var2 < 0) {
            var2 = 0;
         }

         PrettyPrintXMLWriter var4 = new PrettyPrintXMLWriter(var1);
         var4.setLineIndenter(StringUtils.repeat(" ", var2));
         var4.setLineSeparator(var3);
         MXParser var5 = new MXParser();

         try {
            var5.setInput(var0);
            prettyFormatInternal(var5, var4);
         } catch (XmlPullParserException var7) {
            throw new IOException("Unable to parse the XML: " + var7.getMessage());
         }
      }
   }

   public static void prettyFormat(InputStream var0, OutputStream var1) throws IOException {
      prettyFormat(var0, var1, 2, DEFAULT_LINE_SEPARATOR);
   }

   public static void prettyFormat(InputStream var0, OutputStream var1, int var2, String var3) throws IOException {
      if (var0 == null) {
         throw new IllegalArgumentException("The is is null");
      } else if (var1 == null) {
         throw new IllegalArgumentException("The os is null");
      } else {
         if (var2 < 0) {
            var2 = 0;
         }

         XmlStreamReader var4 = null;
         OutputStreamWriter var5 = null;

         try {
            var4 = ReaderFactory.newXmlReader(var0);
            var5 = new OutputStreamWriter(var1);
            PrettyPrintXMLWriter var6 = new PrettyPrintXMLWriter(var5);
            var6.setLineIndenter(StringUtils.repeat(" ", var2));
            var6.setLineSeparator(var3);
            MXParser var7 = new MXParser();
            var7.setInput(var4);
            prettyFormatInternal(var7, var6);
            var5.close();
            var5 = null;
            var4.close();
            var4 = null;
         } catch (XmlPullParserException var11) {
            throw new IOException("Unable to parse the XML: " + var11.getMessage());
         } finally {
            IOUtil.close(var5);
            IOUtil.close(var4);
         }
      }
   }

   private static void prettyFormatInternal(XmlPullParser var0, PrettyPrintXMLWriter var1) throws XmlPullParserException, IOException {
      boolean var2 = false;
      boolean var3 = false;

      for (int var4 = var0.getEventType(); var4 != 1; var4 = var0.nextToken()) {
         if (var4 == 2) {
            var2 = true;
            if (var3) {
               var1.writeText(var1.getLineIndenter());
               var3 = false;
            }

            var1.startElement(var0.getName());

            for (int var11 = 0; var11 < var0.getAttributeCount(); var11++) {
               String var6 = var0.getAttributeName(var11);
               String var7 = var0.getAttributeValue(var11);
               var1.addAttribute(var6, var7);
            }
         } else if (var4 == 4) {
            String var9 = var0.getText();
            if (!var9.trim().equals("")) {
               var9 = StringUtils.removeDuplicateWhitespace(var9);
               var1.writeText(var9);
            }
         } else if (var4 == 3) {
            var2 = false;
            var1.endElement();
         } else if (var4 != 9) {
            if (var4 == 10) {
               var1.writeMarkup("<!DOCTYPE" + var0.getText() + ">");
               var1.endOfLine();
            } else if (var4 == 8) {
               var1.writeMarkup("<?" + var0.getText() + "?>");
               var1.endOfLine();
            } else if (var4 == 5) {
               var1.writeMarkup("<![CDATA[" + var0.getText() + "]]>");
            } else if (var4 == 6) {
               var1.writeMarkup("&" + var0.getName() + ";");
            }
         } else {
            var3 = true;
            if (!var2) {
               var1.writeMarkup(var1.getLineSeparator());

               for (int var5 = 0; var5 < var1.getDepth(); var5++) {
                  var1.writeMarkup(var1.getLineIndenter());
               }
            }

            var1.writeMarkup("<!--" + var0.getText().trim() + " -->");
            if (!var2) {
               var1.writeMarkup(var1.getLineSeparator());

               for (int var8 = 0; var8 < var1.getDepth() - 1; var8++) {
                  var1.writeMarkup(var1.getLineIndenter());
               }
            }
         }
      }
   }
}
