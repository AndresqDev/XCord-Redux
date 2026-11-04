package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.io.InputStream;

public class XmlReaderException extends IOException {
   private String _bomEncoding;
   private String _xmlGuessEncoding;
   private String _xmlEncoding;
   private String _contentTypeMime;
   private String _contentTypeEncoding;
   private InputStream _is;

   public XmlReaderException(String var1, String var2, String var3, String var4, InputStream var5) {
      this(var1, null, null, var2, var3, var4, var5);
   }

   public XmlReaderException(String var1, String var2, String var3, String var4, String var5, String var6, InputStream var7) {
      super(var1);
      this._contentTypeMime = var2;
      this._contentTypeEncoding = var3;
      this._bomEncoding = var4;
      this._xmlGuessEncoding = var5;
      this._xmlEncoding = var6;
      this._is = var7;
   }

   public String getBomEncoding() {
      return this._bomEncoding;
   }

   public String getXmlGuessEncoding() {
      return this._xmlGuessEncoding;
   }

   public String getXmlEncoding() {
      return this._xmlEncoding;
   }

   public String getContentTypeMime() {
      return this._contentTypeMime;
   }

   public String getContentTypeEncoding() {
      return this._contentTypeEncoding;
   }

   public InputStream getInputStream() {
      return this._is;
   }
}
