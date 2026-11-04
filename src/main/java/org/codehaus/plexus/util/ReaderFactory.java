package org.codehaus.plexus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import org.codehaus.plexus.util.xml.XmlStreamReader;

public class ReaderFactory {
   public static final String ISO_8859_1 = "ISO-8859-1";
   public static final String US_ASCII = "US-ASCII";
   public static final String UTF_16 = "UTF-16";
   public static final String UTF_16BE = "UTF-16BE";
   public static final String UTF_16LE = "UTF-16LE";
   public static final String UTF_8 = "UTF-8";
   public static final String FILE_ENCODING = System.getProperty("file.encoding");

   public static XmlStreamReader newXmlReader(InputStream var0) throws IOException {
      return new XmlStreamReader(var0);
   }

   public static XmlStreamReader newXmlReader(File var0) throws IOException {
      return new XmlStreamReader(var0);
   }

   public static XmlStreamReader newXmlReader(URL var0) throws IOException {
      return new XmlStreamReader(var0);
   }

   public static Reader newPlatformReader(InputStream var0) {
      return new InputStreamReader(var0);
   }

   public static Reader newPlatformReader(File var0) throws FileNotFoundException {
      return new FileReader(var0);
   }

   public static Reader newPlatformReader(URL var0) throws IOException {
      return new InputStreamReader(var0.openStream());
   }

   public static Reader newReader(InputStream var0, String var1) throws UnsupportedEncodingException {
      return new InputStreamReader(var0, var1);
   }

   public static Reader newReader(File var0, String var1) throws FileNotFoundException, UnsupportedEncodingException {
      return new InputStreamReader(new FileInputStream(var0), var1);
   }

   public static Reader newReader(URL var0, String var1) throws IOException {
      return new InputStreamReader(var0.openStream(), var1);
   }
}
