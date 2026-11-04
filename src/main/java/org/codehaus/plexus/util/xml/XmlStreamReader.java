package org.codehaus.plexus.util.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class XmlStreamReader extends XmlReader {
   public XmlStreamReader(File var1) throws IOException {
      super(var1);
   }

   public XmlStreamReader(InputStream var1) throws IOException {
      super(var1);
   }

   public XmlStreamReader(InputStream var1, boolean var2) throws IOException, XmlStreamReaderException {
      super(var1, var2);
   }

   public XmlStreamReader(URL var1) throws IOException {
      super(var1);
   }

   public XmlStreamReader(URLConnection var1) throws IOException {
      super(var1);
   }

   public XmlStreamReader(InputStream var1, String var2) throws IOException {
      super(var1, var2);
   }

   public XmlStreamReader(InputStream var1, String var2, boolean var3, String var4) throws IOException, XmlStreamReaderException {
      super(var1, var2, var3, var4);
   }

   public XmlStreamReader(InputStream var1, String var2, boolean var3) throws IOException, XmlStreamReaderException {
      super(var1, var2, var3);
   }
}
