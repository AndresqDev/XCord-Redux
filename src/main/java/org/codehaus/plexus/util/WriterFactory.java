package org.codehaus.plexus.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.codehaus.plexus.util.xml.XmlStreamWriter;

public class WriterFactory {
   public static final String ISO_8859_1 = "ISO-8859-1";
   public static final String US_ASCII = "US-ASCII";
   public static final String UTF_16 = "UTF-16";
   public static final String UTF_16BE = "UTF-16BE";
   public static final String UTF_16LE = "UTF-16LE";
   public static final String UTF_8 = "UTF-8";
   public static final String FILE_ENCODING = System.getProperty("file.encoding");

   public static XmlStreamWriter newXmlWriter(OutputStream var0) throws IOException {
      return new XmlStreamWriter(var0);
   }

   public static XmlStreamWriter newXmlWriter(File var0) throws IOException {
      return new XmlStreamWriter(var0);
   }

   public static Writer newPlatformWriter(OutputStream var0) {
      return new OutputStreamWriter(var0);
   }

   public static Writer newPlatformWriter(File var0) throws IOException {
      return new FileWriter(var0);
   }

   public static Writer newWriter(OutputStream var0, String var1) throws UnsupportedEncodingException {
      return new OutputStreamWriter(var0, var1);
   }

   public static Writer newWriter(File var0, String var1) throws UnsupportedEncodingException, FileNotFoundException {
      return newWriter(new FileOutputStream(var0), var1);
   }
}
