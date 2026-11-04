package org.codehaus.plexus.util.xml.pull;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import org.codehaus.plexus.util.ReaderFactory;

public class MXParser implements XmlPullParser {
   protected static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
   protected static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
   protected static final String FEATURE_XML_ROUNDTRIP = "http://xmlpull.org/v1/doc/features.html#xml-roundtrip";
   protected static final String FEATURE_NAMES_INTERNED = "http://xmlpull.org/v1/doc/features.html#names-interned";
   protected static final String PROPERTY_XMLDECL_VERSION = "http://xmlpull.org/v1/doc/properties.html#xmldecl-version";
   protected static final String PROPERTY_XMLDECL_STANDALONE = "http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone";
   protected static final String PROPERTY_XMLDECL_CONTENT = "http://xmlpull.org/v1/doc/properties.html#xmldecl-content";
   protected static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
   protected boolean allStringsInterned;
   private static final boolean TRACE_SIZING = false;
   protected boolean processNamespaces;
   protected boolean roundtripSupported;
   protected String location;
   protected int lineNumber;
   protected int columnNumber;
   protected boolean seenRoot;
   protected boolean reachedEnd;
   protected int eventType;
   protected boolean emptyElementTag;
   protected int depth;
   protected char[][] elRawName;
   protected int[] elRawNameEnd;
   protected int[] elRawNameLine;
   protected String[] elName;
   protected String[] elPrefix;
   protected String[] elUri;
   protected int[] elNamespaceCount;
   protected int attributeCount;
   protected String[] attributeName;
   protected int[] attributeNameHash;
   protected String[] attributePrefix;
   protected String[] attributeUri;
   protected String[] attributeValue;
   protected int namespaceEnd;
   protected String[] namespacePrefix;
   protected int[] namespacePrefixHash;
   protected String[] namespaceUri;
   protected int entityEnd;
   protected String[] entityName;
   protected char[][] entityNameBuf;
   protected String[] entityReplacement;
   protected char[][] entityReplacementBuf;
   protected int[] entityNameHash;
   private final EntityReplacementMap replacementMapTemplate;
   protected static final int READ_CHUNK_SIZE = 8192;
   protected Reader reader;
   protected String inputEncoding;
   protected int bufLoadFactor = 95;
   protected char[] buf = new char[Runtime.getRuntime().freeMemory() > 1000000L ? 8192 : 256];
   protected int bufSoftLimit = this.bufLoadFactor * this.buf.length / 100;
   protected boolean preventBufferCompaction;
   protected int bufAbsoluteStart;
   protected int bufStart;
   protected int bufEnd;
   protected int pos;
   protected int posStart;
   protected int posEnd;
   protected char[] pc = new char[Runtime.getRuntime().freeMemory() > 1000000L ? 8192 : 64];
   protected int pcStart;
   protected int pcEnd;
   protected boolean usePC;
   protected boolean seenStartTag;
   protected boolean seenEndTag;
   protected boolean pastEndTag;
   protected boolean seenAmpersand;
   protected boolean seenMarkup;
   protected boolean seenDocdecl;
   protected boolean tokenize;
   protected String text;
   protected String entityRefName;
   protected String xmlDeclVersion;
   protected Boolean xmlDeclStandalone;
   protected String xmlDeclContent;
   protected char[] charRefOneCharBuf = new char[1];
   protected static final char[] VERSION = "version".toCharArray();
   protected static final char[] NCODING = "ncoding".toCharArray();
   protected static final char[] TANDALONE = "tandalone".toCharArray();
   protected static final char[] YES = "yes".toCharArray();
   protected static final char[] NO = "no".toCharArray();
   protected static final int LOOKUP_MAX = 1024;
   protected static final char LOOKUP_MAX_CHAR = 'Ѐ';
   protected static boolean[] lookupNameStartChar = new boolean[1024];
   protected static boolean[] lookupNameChar = new boolean[1024];
   private static final char MIN_HIGH_SURROGATE = '\ud800';
   private static final char MAX_HIGH_SURROGATE = '\udbff';
   private static final int MAX_CODE_POINT = 1114111;
   private static final int MIN_SUPPLEMENTARY_CODE_POINT = 65536;

   protected void resetStringCache() {
   }

   protected String newString(char[] var1, int var2, int var3) {
      return new String(var1, var2, var3);
   }

   protected String newStringIntern(char[] var1, int var2, int var3) {
      return new String(var1, var2, var3).intern();
   }

   protected void ensureElementsCapacity() {
      int var1 = this.elName != null ? this.elName.length : 0;
      if (this.depth + 1 >= var1) {
         int var2 = (this.depth >= 7 ? 2 * this.depth : 8) + 2;
         boolean var3 = var1 > 0;
         Object var4 = null;
         var4 = new String[var2];
         if (var3) {
            System.arraycopy(this.elName, 0, var4, 0, var1);
         }

         this.elName = (String[])var4;
         var4 = new String[var2];
         if (var3) {
            System.arraycopy(this.elPrefix, 0, var4, 0, var1);
         }

         this.elPrefix = (String[])var4;
         var4 = new String[var2];
         if (var3) {
            System.arraycopy(this.elUri, 0, var4, 0, var1);
         }

         this.elUri = (String[])var4;
         int[] var5 = new int[var2];
         if (var3) {
            System.arraycopy(this.elNamespaceCount, 0, var5, 0, var1);
         } else {
            var5[0] = 0;
         }

         this.elNamespaceCount = var5;
         var5 = new int[var2];
         if (var3) {
            System.arraycopy(this.elRawNameEnd, 0, var5, 0, var1);
         }

         this.elRawNameEnd = var5;
         var5 = new int[var2];
         if (var3) {
            System.arraycopy(this.elRawNameLine, 0, var5, 0, var1);
         }

         this.elRawNameLine = var5;
         char[][] var6 = new char[var2][];
         if (var3) {
            System.arraycopy(this.elRawName, 0, var6, 0, var1);
         }

         this.elRawName = var6;
      }
   }

   protected void ensureAttributesCapacity(int var1) {
      int var2 = this.attributeName != null ? this.attributeName.length : 0;
      if (var1 >= var2) {
         int var3 = var1 > 7 ? 2 * var1 : 8;
         boolean var4 = var2 > 0;
         Object var5 = null;
         var5 = new String[var3];
         if (var4) {
            System.arraycopy(this.attributeName, 0, var5, 0, var2);
         }

         this.attributeName = (String[])var5;
         var5 = new String[var3];
         if (var4) {
            System.arraycopy(this.attributePrefix, 0, var5, 0, var2);
         }

         this.attributePrefix = (String[])var5;
         var5 = new String[var3];
         if (var4) {
            System.arraycopy(this.attributeUri, 0, var5, 0, var2);
         }

         this.attributeUri = (String[])var5;
         var5 = new String[var3];
         if (var4) {
            System.arraycopy(this.attributeValue, 0, var5, 0, var2);
         }

         this.attributeValue = (String[])var5;
         if (!this.allStringsInterned) {
            int[] var6 = new int[var3];
            if (var4) {
               System.arraycopy(this.attributeNameHash, 0, var6, 0, var2);
            }

            this.attributeNameHash = var6;
         }

         var5 = null;
      }
   }

   protected void ensureNamespacesCapacity(int var1) {
      int var2 = this.namespacePrefix != null ? this.namespacePrefix.length : 0;
      if (var1 >= var2) {
         int var3 = var1 > 7 ? 2 * var1 : 8;
         String[] var4 = new String[var3];
         String[] var5 = new String[var3];
         if (this.namespacePrefix != null) {
            System.arraycopy(this.namespacePrefix, 0, var4, 0, this.namespaceEnd);
            System.arraycopy(this.namespaceUri, 0, var5, 0, this.namespaceEnd);
         }

         this.namespacePrefix = var4;
         this.namespaceUri = var5;
         if (!this.allStringsInterned) {
            int[] var6 = new int[var3];
            if (this.namespacePrefixHash != null) {
               System.arraycopy(this.namespacePrefixHash, 0, var6, 0, this.namespaceEnd);
            }

            this.namespacePrefixHash = var6;
         }
      }
   }

   protected static final int fastHash(char[] var0, int var1, int var2) {
      if (var2 == 0) {
         return 0;
      } else {
         int var3 = var0[var1];
         var3 = (var3 << 7) + var0[var1 + var2 - 1];
         if (var2 > 16) {
            var3 = (var3 << 7) + var0[var1 + var2 / 4];
         }

         if (var2 > 8) {
            var3 = (var3 << 7) + var0[var1 + var2 / 2];
         }

         return var3;
      }
   }

   protected void ensureEntityCapacity() {
      int var1 = this.entityReplacementBuf != null ? this.entityReplacementBuf.length : 0;
      if (this.entityEnd >= var1) {
         int var2 = this.entityEnd > 7 ? 2 * this.entityEnd : 8;
         String[] var3 = new String[var2];
         char[][] var4 = new char[var2][];
         String[] var5 = new String[var2];
         char[][] var6 = new char[var2][];
         if (this.entityName != null) {
            System.arraycopy(this.entityName, 0, var3, 0, this.entityEnd);
            System.arraycopy(this.entityNameBuf, 0, var4, 0, this.entityEnd);
            System.arraycopy(this.entityReplacement, 0, var5, 0, this.entityEnd);
            System.arraycopy(this.entityReplacementBuf, 0, var6, 0, this.entityEnd);
         }

         this.entityName = var3;
         this.entityNameBuf = var4;
         this.entityReplacement = var5;
         this.entityReplacementBuf = var6;
         if (!this.allStringsInterned) {
            int[] var7 = new int[var2];
            if (this.entityNameHash != null) {
               System.arraycopy(this.entityNameHash, 0, var7, 0, this.entityEnd);
            }

            this.entityNameHash = var7;
         }
      }
   }

   protected void reset() {
      this.location = null;
      this.lineNumber = 1;
      this.columnNumber = 0;
      this.seenRoot = false;
      this.reachedEnd = false;
      this.eventType = 0;
      this.emptyElementTag = false;
      this.depth = 0;
      this.attributeCount = 0;
      this.namespaceEnd = 0;
      this.entityEnd = 0;
      this.setupFromTemplate();
      this.reader = null;
      this.inputEncoding = null;
      this.preventBufferCompaction = false;
      this.bufAbsoluteStart = 0;
      this.bufEnd = this.bufStart = 0;
      this.pos = this.posStart = this.posEnd = 0;
      this.pcEnd = this.pcStart = 0;
      this.usePC = false;
      this.seenStartTag = false;
      this.seenEndTag = false;
      this.pastEndTag = false;
      this.seenAmpersand = false;
      this.seenMarkup = false;
      this.seenDocdecl = false;
      this.xmlDeclVersion = null;
      this.xmlDeclStandalone = null;
      this.xmlDeclContent = null;
      this.resetStringCache();
   }

   public MXParser() {
      this.replacementMapTemplate = null;
   }

   public MXParser(EntityReplacementMap var1) {
      this.replacementMapTemplate = var1;
   }

   public void setupFromTemplate() {
      if (this.replacementMapTemplate != null) {
         int var1 = this.replacementMapTemplate.entityEnd;
         this.entityName = this.replacementMapTemplate.entityName;
         this.entityNameBuf = this.replacementMapTemplate.entityNameBuf;
         this.entityReplacement = this.replacementMapTemplate.entityReplacement;
         this.entityReplacementBuf = this.replacementMapTemplate.entityReplacementBuf;
         this.entityNameHash = this.replacementMapTemplate.entityNameHash;
         this.entityEnd = var1;
      }
   }

   @Override
   public void setFeature(String var1, boolean var2) throws XmlPullParserException {
      if (var1 == null) {
         throw new IllegalArgumentException("feature name should not be null");
      } else {
         if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(var1)) {
            if (this.eventType != 0) {
               throw new XmlPullParserException("namespace processing feature can only be changed before parsing", this, null);
            }

            this.processNamespaces = var2;
         } else if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(var1)) {
            if (var2) {
               throw new XmlPullParserException("interning names in this implementation is not supported");
            }
         } else if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(var1)) {
            if (var2) {
               throw new XmlPullParserException("processing DOCDECL is not supported");
            }
         } else {
            if (!"http://xmlpull.org/v1/doc/features.html#xml-roundtrip".equals(var1)) {
               throw new XmlPullParserException("unsupported feature " + var1);
            }

            this.roundtripSupported = var2;
         }
      }
   }

   @Override
   public boolean getFeature(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("feature name should not be null");
      } else if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(var1)) {
         return this.processNamespaces;
      } else if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(var1)) {
         return false;
      } else if ("http://xmlpull.org/v1/doc/features.html#process-docdecl".equals(var1)) {
         return false;
      } else {
         return "http://xmlpull.org/v1/doc/features.html#xml-roundtrip".equals(var1) ? this.roundtripSupported : false;
      }
   }

   @Override
   public void setProperty(String var1, Object var2) throws XmlPullParserException {
      if ("http://xmlpull.org/v1/doc/properties.html#location".equals(var1)) {
         this.location = (String)var2;
      } else {
         throw new XmlPullParserException("unsupported property: '" + var1 + "'");
      }
   }

   @Override
   public Object getProperty(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("property name should not be null");
      } else if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-version".equals(var1)) {
         return this.xmlDeclVersion;
      } else if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-standalone".equals(var1)) {
         return this.xmlDeclStandalone;
      } else if ("http://xmlpull.org/v1/doc/properties.html#xmldecl-content".equals(var1)) {
         return this.xmlDeclContent;
      } else {
         return "http://xmlpull.org/v1/doc/properties.html#location".equals(var1) ? this.location : null;
      }
   }

   @Override
   public void setInput(Reader var1) throws XmlPullParserException {
      this.reset();
      this.reader = var1;
   }

   @Override
   public void setInput(InputStream var1, String var2) throws XmlPullParserException {
      if (var1 == null) {
         throw new IllegalArgumentException("input stream can not be null");
      } else {
         Object var3;
         try {
            if (var2 != null) {
               var3 = ReaderFactory.newReader(var1, var2);
            } else {
               var3 = ReaderFactory.newXmlReader(var1);
            }
         } catch (UnsupportedEncodingException var5) {
            throw new XmlPullParserException("could not create reader for encoding " + var2 + " : " + var5, this, var5);
         } catch (IOException var6) {
            throw new XmlPullParserException("could not create reader : " + var6, this, var6);
         }

         this.setInput((Reader)var3);
         this.inputEncoding = var2;
      }
   }

   @Override
   public String getInputEncoding() {
      return this.inputEncoding;
   }

   @Override
   public void defineEntityReplacementText(String var1, String var2) throws XmlPullParserException {
      if (!var2.startsWith("&#") && this.entityName != null && var2.length() > 1) {
         String var3 = var2.substring(1, var2.length() - 1);

         for (int var4 = 0; var4 < this.entityName.length; var4++) {
            if (this.entityName[var4] != null && this.entityName[var4].equals(var3)) {
               var2 = this.entityReplacement[var4];
            }
         }
      }

      this.ensureEntityCapacity();
      char[] var5 = var1.toCharArray();
      this.entityName[this.entityEnd] = this.newString(var5, 0, var1.length());
      this.entityNameBuf[this.entityEnd] = var5;
      this.entityReplacement[this.entityEnd] = var2;
      this.entityReplacementBuf[this.entityEnd] = var2.toCharArray();
      if (!this.allStringsInterned) {
         this.entityNameHash[this.entityEnd] = fastHash(this.entityNameBuf[this.entityEnd], 0, this.entityNameBuf[this.entityEnd].length);
      }

      this.entityEnd++;
   }

   @Override
   public int getNamespaceCount(int var1) throws XmlPullParserException {
      if (!this.processNamespaces || var1 == 0) {
         return 0;
      } else if (var1 >= 0 && var1 <= this.depth) {
         return this.elNamespaceCount[var1];
      } else {
         throw new IllegalArgumentException("namespace count may be for depth 0.." + this.depth + " not " + var1);
      }
   }

   @Override
   public String getNamespacePrefix(int var1) throws XmlPullParserException {
      if (var1 < this.namespaceEnd) {
         return this.namespacePrefix[var1];
      } else {
         throw new XmlPullParserException("position " + var1 + " exceeded number of available namespaces " + this.namespaceEnd);
      }
   }

   @Override
   public String getNamespaceUri(int var1) throws XmlPullParserException {
      if (var1 < this.namespaceEnd) {
         return this.namespaceUri[var1];
      } else {
         throw new XmlPullParserException("position " + var1 + " exceeded number of available namespaces " + this.namespaceEnd);
      }
   }

   @Override
   public String getNamespace(String var1) {
      if (var1 != null) {
         for (int var2 = this.namespaceEnd - 1; var2 >= 0; var2--) {
            if (var1.equals(this.namespacePrefix[var2])) {
               return this.namespaceUri[var2];
            }
         }

         if ("xml".equals(var1)) {
            return "http://www.w3.org/XML/1998/namespace";
         }

         if ("xmlns".equals(var1)) {
            return "http://www.w3.org/2000/xmlns/";
         }
      } else {
         for (int var3 = this.namespaceEnd - 1; var3 >= 0; var3--) {
            if (this.namespacePrefix[var3] == null) {
               return this.namespaceUri[var3];
            }
         }
      }

      return null;
   }

   @Override
   public int getDepth() {
      return this.depth;
   }

   private static int findFragment(int var0, char[] var1, int var2, int var3) {
      if (var2 < var0) {
         var2 = var0;
         if (var0 > var3) {
            var2 = var3;
         }

         return var2;
      } else {
         if (var3 - var2 > 65) {
            var2 = var3 - 10;
         }

         int var4 = var2 + 1;

         char var5;
         do {
            var4--;
            if (var4 <= var0 || var3 - var4 > 65) {
               break;
            }

            var5 = var1[var4];
         } while (var5 != '<' || var2 - var4 <= 10);

         return var4;
      }
   }

   @Override
   public String getPositionDescription() {
      String var1 = null;
      if (this.posStart <= this.pos) {
         int var2 = findFragment(0, this.buf, this.posStart, this.pos);
         if (var2 < this.pos) {
            var1 = new String(this.buf, var2, this.pos - var2);
         }

         if (this.bufAbsoluteStart > 0 || var2 > 0) {
            var1 = "..." + var1;
         }
      }

      return " "
         + TYPES[this.eventType]
         + (var1 != null ? " seen " + this.printable(var1) + "..." : "")
         + " "
         + (this.location != null ? this.location : "")
         + "@"
         + this.getLineNumber()
         + ":"
         + this.getColumnNumber();
   }

   @Override
   public int getLineNumber() {
      return this.lineNumber;
   }

   @Override
   public int getColumnNumber() {
      return this.columnNumber;
   }

   @Override
   public boolean isWhitespace() throws XmlPullParserException {
      if (this.eventType != 4 && this.eventType != 5) {
         if (this.eventType == 7) {
            return true;
         } else {
            throw new XmlPullParserException("no content available to check for whitespaces");
         }
      } else if (this.usePC) {
         for (int var2 = this.pcStart; var2 < this.pcEnd; var2++) {
            if (!this.isS(this.pc[var2])) {
               return false;
            }
         }

         return true;
      } else {
         for (int var1 = this.posStart; var1 < this.posEnd; var1++) {
            if (!this.isS(this.buf[var1])) {
               return false;
            }
         }

         return true;
      }
   }

   @Override
   public String getText() {
      if (this.eventType == 0 || this.eventType == 1) {
         return null;
      } else if (this.eventType == 6) {
         return this.text;
      } else {
         if (this.text == null) {
            if (this.usePC && this.eventType != 2 && this.eventType != 3) {
               this.text = new String(this.pc, this.pcStart, this.pcEnd - this.pcStart);
            } else {
               this.text = new String(this.buf, this.posStart, this.posEnd - this.posStart);
            }
         }

         return this.text;
      }
   }

   @Override
   public char[] getTextCharacters(int[] var1) {
      if (this.eventType == 4) {
         if (this.usePC) {
            var1[0] = this.pcStart;
            var1[1] = this.pcEnd - this.pcStart;
            return this.pc;
         } else {
            var1[0] = this.posStart;
            var1[1] = this.posEnd - this.posStart;
            return this.buf;
         }
      } else if (this.eventType == 2
         || this.eventType == 3
         || this.eventType == 5
         || this.eventType == 9
         || this.eventType == 6
         || this.eventType == 8
         || this.eventType == 7
         || this.eventType == 10) {
         var1[0] = this.posStart;
         var1[1] = this.posEnd - this.posStart;
         return this.buf;
      } else if (this.eventType != 0 && this.eventType != 1) {
         throw new IllegalArgumentException("unknown text eventType: " + this.eventType);
      } else {
         var1[0] = var1[1] = -1;
         return null;
      }
   }

   @Override
   public String getNamespace() {
      if (this.eventType == 2) {
         return this.processNamespaces ? this.elUri[this.depth] : "";
      } else if (this.eventType == 3) {
         return this.processNamespaces ? this.elUri[this.depth] : "";
      } else {
         return null;
      }
   }

   @Override
   public String getName() {
      if (this.eventType == 2) {
         return this.elName[this.depth];
      } else if (this.eventType == 3) {
         return this.elName[this.depth];
      } else if (this.eventType == 6) {
         if (this.entityRefName == null) {
            this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
         }

         return this.entityRefName;
      } else {
         return null;
      }
   }

   @Override
   public String getPrefix() {
      if (this.eventType == 2) {
         return this.elPrefix[this.depth];
      } else {
         return this.eventType == 3 ? this.elPrefix[this.depth] : null;
      }
   }

   @Override
   public boolean isEmptyElementTag() throws XmlPullParserException {
      if (this.eventType != 2) {
         throw new XmlPullParserException("parser must be on START_TAG to check for empty element", this, null);
      } else {
         return this.emptyElementTag;
      }
   }

   @Override
   public int getAttributeCount() {
      return this.eventType != 2 ? -1 : this.attributeCount;
   }

   @Override
   public String getAttributeNamespace(int var1) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes");
      } else if (!this.processNamespaces) {
         return "";
      } else if (var1 >= 0 && var1 < this.attributeCount) {
         return this.attributeUri[var1];
      } else {
         throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + var1);
      }
   }

   @Override
   public String getAttributeName(int var1) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes");
      } else if (var1 >= 0 && var1 < this.attributeCount) {
         return this.attributeName[var1];
      } else {
         throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + var1);
      }
   }

   @Override
   public String getAttributePrefix(int var1) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes");
      } else if (!this.processNamespaces) {
         return null;
      } else if (var1 >= 0 && var1 < this.attributeCount) {
         return this.attributePrefix[var1];
      } else {
         throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + var1);
      }
   }

   @Override
   public String getAttributeType(int var1) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes");
      } else if (var1 >= 0 && var1 < this.attributeCount) {
         return "CDATA";
      } else {
         throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + var1);
      }
   }

   @Override
   public boolean isAttributeDefault(int var1) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes");
      } else if (var1 >= 0 && var1 < this.attributeCount) {
         return false;
      } else {
         throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + var1);
      }
   }

   @Override
   public String getAttributeValue(int var1) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes");
      } else if (var1 >= 0 && var1 < this.attributeCount) {
         return this.attributeValue[var1];
      } else {
         throw new IndexOutOfBoundsException("attribute position must be 0.." + (this.attributeCount - 1) + " and not " + var1);
      }
   }

   @Override
   public String getAttributeValue(String var1, String var2) {
      if (this.eventType != 2) {
         throw new IndexOutOfBoundsException("only START_TAG can have attributes" + this.getPositionDescription());
      } else if (var2 == null) {
         throw new IllegalArgumentException("attribute name can not be null");
      } else {
         if (this.processNamespaces) {
            if (var1 == null) {
               var1 = "";
            }

            for (int var3 = 0; var3 < this.attributeCount; var3++) {
               if ((var1 == this.attributeUri[var3] || var1.equals(this.attributeUri[var3])) && var2.equals(this.attributeName[var3])) {
                  return this.attributeValue[var3];
               }
            }
         } else {
            if (var1 != null && var1.length() == 0) {
               var1 = null;
            }

            if (var1 != null) {
               throw new IllegalArgumentException("when namespaces processing is disabled attribute namespace must be null");
            }

            for (int var4 = 0; var4 < this.attributeCount; var4++) {
               if (var2.equals(this.attributeName[var4])) {
                  return this.attributeValue[var4];
               }
            }
         }

         return null;
      }
   }

   @Override
   public int getEventType() throws XmlPullParserException {
      return this.eventType;
   }

   @Override
   public void require(int var1, String var2, String var3) throws XmlPullParserException, IOException {
      if (!this.processNamespaces && var2 != null) {
         throw new XmlPullParserException(
            "processing namespaces must be enabled on parser (or factory) to have possible namespaces declared on elements"
               + " (position:"
               + this.getPositionDescription()
               + ")"
         );
      } else if (var1 != this.getEventType() || var2 != null && !var2.equals(this.getNamespace()) || var3 != null && !var3.equals(this.getName())) {
         throw new XmlPullParserException(
            "expected event "
               + TYPES[var1]
               + (var3 != null ? " with name '" + var3 + "'" : "")
               + (var2 != null && var3 != null ? " and" : "")
               + (var2 != null ? " with namespace '" + var2 + "'" : "")
               + " but got"
               + (var1 != this.getEventType() ? " " + TYPES[this.getEventType()] : "")
               + (var3 != null && this.getName() != null && !var3.equals(this.getName()) ? " name '" + this.getName() + "'" : "")
               + (
                  var2 != null
                        && var3 != null
                        && this.getName() != null
                        && !var3.equals(this.getName())
                        && this.getNamespace() != null
                        && !var2.equals(this.getNamespace())
                     ? " and"
                     : ""
               )
               + (var2 != null && this.getNamespace() != null && !var2.equals(this.getNamespace()) ? " namespace '" + this.getNamespace() + "'" : "")
               + " (position:"
               + this.getPositionDescription()
               + ")"
         );
      }
   }

   public void skipSubTree() throws XmlPullParserException, IOException {
      this.require(2, null, null);
      int var1 = 1;

      while (var1 > 0) {
         int var2 = this.next();
         if (var2 == 3) {
            var1--;
         } else if (var2 == 2) {
            var1++;
         }
      }
   }

   @Override
   public String nextText() throws XmlPullParserException, IOException {
      if (this.getEventType() != 2) {
         throw new XmlPullParserException("parser must be on START_TAG to read next text", this, null);
      } else {
         int var1 = this.next();
         if (var1 == 4) {
            String var2 = this.getText();
            var1 = this.next();
            if (var1 != 3) {
               throw new XmlPullParserException("TEXT must be immediately followed by END_TAG and not " + TYPES[this.getEventType()], this, null);
            } else {
               return var2;
            }
         } else if (var1 == 3) {
            return "";
         } else {
            throw new XmlPullParserException("parser must be on START_TAG or TEXT to read text", this, null);
         }
      }
   }

   @Override
   public int nextTag() throws XmlPullParserException, IOException {
      this.next();
      if (this.eventType == 4 && this.isWhitespace()) {
         this.next();
      }

      if (this.eventType != 2 && this.eventType != 3) {
         throw new XmlPullParserException("expected START_TAG or END_TAG not " + TYPES[this.getEventType()], this, null);
      } else {
         return this.eventType;
      }
   }

   @Override
   public int next() throws XmlPullParserException, IOException {
      this.tokenize = false;
      return this.nextImpl();
   }

   @Override
   public int nextToken() throws XmlPullParserException, IOException {
      this.tokenize = true;
      return this.nextImpl();
   }

   protected int nextImpl() throws XmlPullParserException, IOException {
      this.text = null;
      this.pcEnd = this.pcStart = 0;
      this.usePC = false;
      this.bufStart = this.posEnd;
      if (this.pastEndTag) {
         this.pastEndTag = false;
         this.depth--;
         this.namespaceEnd = this.elNamespaceCount[this.depth];
      }

      if (this.emptyElementTag) {
         this.emptyElementTag = false;
         this.pastEndTag = true;
         return this.eventType = 3;
      } else if (this.depth <= 0) {
         return this.seenRoot ? this.parseEpilog() : this.parseProlog();
      } else if (this.seenStartTag) {
         this.seenStartTag = false;
         return this.eventType = this.parseStartTag();
      } else if (this.seenEndTag) {
         this.seenEndTag = false;
         return this.eventType = this.parseEndTag();
      } else {
         char var1;
         if (this.seenMarkup) {
            this.seenMarkup = false;
            var1 = '<';
         } else if (this.seenAmpersand) {
            this.seenAmpersand = false;
            var1 = '&';
         } else {
            var1 = this.more();
         }

         this.posStart = this.pos - 1;
         boolean var2 = false;
         boolean var3 = false;

         while (true) {
            while (true) {
               if (var1 == '<') {
                  if (var2 && this.tokenize) {
                     this.seenMarkup = true;
                     return this.eventType = 4;
                  }

                  var1 = this.more();
                  if (var1 == '/') {
                     if (!this.tokenize && var2) {
                        this.seenEndTag = true;
                        return this.eventType = 4;
                     }

                     return this.eventType = this.parseEndTag();
                  }

                  if (var1 == '!') {
                     var1 = this.more();
                     if (var1 == '-') {
                        this.parseComment();
                        if (this.tokenize) {
                           return this.eventType = 9;
                        }

                        if (!this.usePC && var2) {
                           var3 = true;
                           break;
                        }

                        this.posStart = this.pos;
                        break;
                     }

                     if (var1 != '[') {
                        throw new XmlPullParserException("unexpected character in markup " + this.printable(var1), this, null);
                     }

                     this.parseCDSect(var2);
                     if (this.tokenize) {
                        return this.eventType = 5;
                     }

                     int var14 = this.posStart;
                     int var16 = this.posEnd;
                     int var18 = var16 - var14;
                     if (var18 > 0) {
                        var2 = true;
                        if (!this.usePC) {
                           var3 = true;
                        }
                     }
                     break;
                  }

                  if (var1 != '?') {
                     if (this.isNameStartChar(var1)) {
                        if (!this.tokenize && var2) {
                           this.seenStartTag = true;
                           return this.eventType = 4;
                        }

                        return this.eventType = this.parseStartTag();
                     }

                     throw new XmlPullParserException("unexpected character in markup " + this.printable(var1), this, null);
                  }

                  this.parsePI();
                  if (this.tokenize) {
                     return this.eventType = 8;
                  }

                  if (!this.usePC && var2) {
                     var3 = true;
                     break;
                  }

                  this.posStart = this.pos;
                  break;
               }

               if (var1 == '&') {
                  if (this.tokenize && var2) {
                     this.seenAmpersand = true;
                     return this.eventType = 4;
                  }

                  int var13 = this.posStart + this.bufAbsoluteStart;
                  int var15 = this.posEnd + this.bufAbsoluteStart;
                  char[] var17 = this.parseEntityRef();
                  if (this.tokenize) {
                     return this.eventType = 6;
                  }

                  if (var17 == null) {
                     if (this.entityRefName == null) {
                        this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
                     }

                     throw new XmlPullParserException("could not resolve entity named '" + this.printable(this.entityRefName) + "'", this, null);
                  }

                  this.posStart = var13 - this.bufAbsoluteStart;
                  this.posEnd = var15 - this.bufAbsoluteStart;
                  if (!this.usePC) {
                     if (var2) {
                        this.joinPC();
                        var3 = false;
                     } else {
                        this.usePC = true;
                        this.pcStart = this.pcEnd = 0;
                     }
                  }

                  for (char var10 : var17) {
                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     this.pc[this.pcEnd++] = var10;
                  }

                  var2 = true;
                  break;
               }

               if (var3) {
                  this.joinPC();
                  var3 = false;
               }

               var2 = true;
               boolean var4 = false;
               boolean var5 = !this.tokenize || !this.roundtripSupported;
               boolean var6 = false;
               boolean var7 = false;

               do {
                  if (var1 == ']') {
                     if (var6) {
                        var7 = true;
                     } else {
                        var6 = true;
                     }
                  } else {
                     if (var7 && var1 == '>') {
                        throw new XmlPullParserException("characters ]]> are not allowed in content", this, null);
                     }

                     if (var6) {
                        var6 = false;
                        var7 = false;
                     }
                  }

                  if (var5) {
                     if (var1 == '\r') {
                        var4 = true;
                        this.posEnd = this.pos - 1;
                        if (!this.usePC) {
                           if (this.posEnd > this.posStart) {
                              this.joinPC();
                           } else {
                              this.usePC = true;
                              this.pcStart = this.pcEnd = 0;
                           }
                        }

                        if (this.pcEnd >= this.pc.length) {
                           this.ensurePC(this.pcEnd);
                        }

                        this.pc[this.pcEnd++] = '\n';
                     } else if (var1 == '\n') {
                        if (!var4 && this.usePC) {
                           if (this.pcEnd >= this.pc.length) {
                              this.ensurePC(this.pcEnd);
                           }

                           this.pc[this.pcEnd++] = '\n';
                        }

                        var4 = false;
                     } else {
                        if (this.usePC) {
                           if (this.pcEnd >= this.pc.length) {
                              this.ensurePC(this.pcEnd);
                           }

                           this.pc[this.pcEnd++] = var1;
                        }

                        var4 = false;
                     }
                  }

                  var1 = this.more();
               } while (var1 != '<' && var1 != '&');

               this.posEnd = this.pos - 1;
            }

            var1 = this.more();
         }
      }
   }

   protected int parseProlog() throws XmlPullParserException, IOException {
      char var1;
      if (this.seenMarkup) {
         var1 = this.buf[this.pos - 1];
      } else {
         var1 = this.more();
      }

      if (this.eventType == 0) {
         if (var1 == '\ufffe') {
            throw new XmlPullParserException("first character in input was UNICODE noncharacter (0xFFFE)- input requires int swapping", this, null);
         }

         if (var1 == '\ufeff') {
            var1 = this.more();
         }
      }

      this.seenMarkup = false;
      boolean var2 = false;
      this.posStart = this.pos - 1;
      boolean var3 = this.tokenize && !this.roundtripSupported;
      boolean var4 = false;

      while (true) {
         if (var1 == '<') {
            if (var2 && this.tokenize) {
               this.posEnd = this.pos - 1;
               this.seenMarkup = true;
               return this.eventType = 7;
            }

            var1 = this.more();
            if (var1 == '?') {
               boolean var5 = this.parsePI();
               if (this.tokenize) {
                  if (var5) {
                     return this.eventType = 0;
                  }

                  return this.eventType = 8;
               }
            } else {
               if (var1 != '!') {
                  if (var1 == '/') {
                     throw new XmlPullParserException("expected start tag name and not " + this.printable(var1), this, null);
                  }

                  if (this.isNameStartChar(var1)) {
                     this.seenRoot = true;
                     return this.parseStartTag();
                  }

                  throw new XmlPullParserException("expected start tag name and not " + this.printable(var1), this, null);
               }

               var1 = this.more();
               if (var1 == 'D') {
                  if (this.seenDocdecl) {
                     throw new XmlPullParserException("only one docdecl allowed in XML document", this, null);
                  }

                  this.seenDocdecl = true;
                  this.parseDocdecl();
                  if (this.tokenize) {
                     return this.eventType = 10;
                  }
               } else {
                  if (var1 != '-') {
                     throw new XmlPullParserException("unexpected markup <!" + this.printable(var1), this, null);
                  }

                  this.parseComment();
                  if (this.tokenize) {
                     return this.eventType = 9;
                  }
               }
            }
         } else {
            if (!this.isS(var1)) {
               throw new XmlPullParserException("only whitespace content allowed before start tag and not " + this.printable(var1), this, null);
            }

            var2 = true;
            if (var3) {
               if (var1 == '\r') {
                  var4 = true;
                  if (!this.usePC) {
                     this.posEnd = this.pos - 1;
                     if (this.posEnd > this.posStart) {
                        this.joinPC();
                     } else {
                        this.usePC = true;
                        this.pcStart = this.pcEnd = 0;
                     }
                  }

                  if (this.pcEnd >= this.pc.length) {
                     this.ensurePC(this.pcEnd);
                  }

                  this.pc[this.pcEnd++] = '\n';
               } else if (var1 == '\n') {
                  if (!var4 && this.usePC) {
                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     this.pc[this.pcEnd++] = '\n';
                  }

                  var4 = false;
               } else {
                  if (this.usePC) {
                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     this.pc[this.pcEnd++] = var1;
                  }

                  var4 = false;
               }
            }
         }

         var1 = this.more();
      }
   }

   protected int parseEpilog() throws XmlPullParserException, IOException {
      if (this.eventType == 1) {
         throw new XmlPullParserException("already reached end of XML input", this, null);
      } else if (this.reachedEnd) {
         return this.eventType = 1;
      } else {
         boolean var1 = false;
         boolean var2 = this.tokenize && !this.roundtripSupported;
         boolean var3 = false;

         try {
            char var4;
            if (this.seenMarkup) {
               var4 = this.buf[this.pos - 1];
            } else {
               var4 = this.more();
            }

            this.seenMarkup = false;
            this.posStart = this.pos - 1;
            if (!this.reachedEnd) {
               do {
                  if (var4 == '<') {
                     if (var1 && this.tokenize) {
                        this.posEnd = this.pos - 1;
                        this.seenMarkup = true;
                        return this.eventType = 7;
                     }

                     var4 = this.more();
                     if (this.reachedEnd) {
                        break;
                     }

                     if (var4 == '?') {
                        this.parsePI();
                        if (this.tokenize) {
                           return this.eventType = 8;
                        }
                     } else {
                        if (var4 != '!') {
                           if (var4 == '/') {
                              throw new XmlPullParserException("end tag not allowed in epilog but got " + this.printable(var4), this, null);
                           }

                           if (this.isNameStartChar(var4)) {
                              throw new XmlPullParserException("start tag not allowed in epilog but got " + this.printable(var4), this, null);
                           }

                           throw new XmlPullParserException("in epilog expected ignorable content and not " + this.printable(var4), this, null);
                        }

                        var4 = this.more();
                        if (this.reachedEnd) {
                           break;
                        }

                        if (var4 == 'D') {
                           this.parseDocdecl();
                           if (this.tokenize) {
                              return this.eventType = 10;
                           }
                        } else {
                           if (var4 != '-') {
                              throw new XmlPullParserException("unexpected markup <!" + this.printable(var4), this, null);
                           }

                           this.parseComment();
                           if (this.tokenize) {
                              return this.eventType = 9;
                           }
                        }
                     }
                  } else {
                     if (!this.isS(var4)) {
                        throw new XmlPullParserException("in epilog non whitespace content is not allowed but got " + this.printable(var4), this, null);
                     }

                     var1 = true;
                     if (var2) {
                        if (var4 == '\r') {
                           var3 = true;
                           if (!this.usePC) {
                              this.posEnd = this.pos - 1;
                              if (this.posEnd > this.posStart) {
                                 this.joinPC();
                              } else {
                                 this.usePC = true;
                                 this.pcStart = this.pcEnd = 0;
                              }
                           }

                           if (this.pcEnd >= this.pc.length) {
                              this.ensurePC(this.pcEnd);
                           }

                           this.pc[this.pcEnd++] = '\n';
                        } else if (var4 == '\n') {
                           if (!var3 && this.usePC) {
                              if (this.pcEnd >= this.pc.length) {
                                 this.ensurePC(this.pcEnd);
                              }

                              this.pc[this.pcEnd++] = '\n';
                           }

                           var3 = false;
                        } else {
                           if (this.usePC) {
                              if (this.pcEnd >= this.pc.length) {
                                 this.ensurePC(this.pcEnd);
                              }

                              this.pc[this.pcEnd++] = var4;
                           }

                           var3 = false;
                        }
                     }
                  }

                  var4 = this.more();
               } while (!this.reachedEnd);
            }
         } catch (EOFException var5) {
            this.reachedEnd = true;
         }

         if (this.reachedEnd) {
            if (this.tokenize && var1) {
               this.posEnd = this.pos;
               return this.eventType = 7;
            } else {
               return this.eventType = 1;
            }
         } else {
            throw new XmlPullParserException("internal error in parseEpilog");
         }
      }
   }

   public int parseEndTag() throws XmlPullParserException, IOException {
      char var1 = this.more();
      if (!this.isNameStartChar(var1)) {
         throw new XmlPullParserException("expected name start and not " + this.printable(var1), this, null);
      } else {
         this.posStart = this.pos - 3;
         int var2 = this.pos - 1 + this.bufAbsoluteStart;

         do {
            var1 = this.more();
         } while (this.isNameChar(var1));

         int var3 = var2 - this.bufAbsoluteStart;
         int var4 = this.pos - 1 - var3;
         char[] var5 = this.elRawName[this.depth];
         if (this.elRawNameEnd[this.depth] != var4) {
            String var10 = new String(var5, 0, this.elRawNameEnd[this.depth]);
            String var11 = new String(this.buf, var3, var4);
            throw new XmlPullParserException(
               "end tag name </" + var11 + "> must match start tag name <" + var10 + ">" + " from line " + this.elRawNameLine[this.depth], this, null
            );
         } else {
            for (int var6 = 0; var6 < var4; var6++) {
               if (this.buf[var3++] != var5[var6]) {
                  String var7 = new String(var5, 0, var4);
                  String var8 = new String(this.buf, var3 - var6 - 1, var4);
                  throw new XmlPullParserException(
                     "end tag name </" + var8 + "> must be the same as start tag <" + var7 + ">" + " from line " + this.elRawNameLine[this.depth], this, null
                  );
               }
            }

            while (this.isS(var1)) {
               var1 = this.more();
            }

            if (var1 != '>') {
               throw new XmlPullParserException(
                  "expected > to finsh end tag not " + this.printable(var1) + " from line " + this.elRawNameLine[this.depth], this, null
               );
            } else {
               this.posEnd = this.pos;
               this.pastEndTag = true;
               return this.eventType = 3;
            }
         }
      }
   }

   public int parseStartTag() throws XmlPullParserException, IOException {
      this.depth++;
      this.posStart = this.pos - 2;
      this.emptyElementTag = false;
      this.attributeCount = 0;
      int var1 = this.pos - 1 + this.bufAbsoluteStart;
      int var2 = -1;
      char var3 = this.buf[this.pos - 1];
      if (var3 == ':' && this.processNamespaces) {
         throw new XmlPullParserException("when namespaces processing enabled colon can not be at element name start", this, null);
      } else {
         while (true) {
            var3 = this.more();
            if (!this.isNameChar(var3)) {
               this.ensureElementsCapacity();
               int var4 = this.pos - 1 - (var1 - this.bufAbsoluteStart);
               if (this.elRawName[this.depth] == null || this.elRawName[this.depth].length < var4) {
                  this.elRawName[this.depth] = new char[2 * var4];
               }

               System.arraycopy(this.buf, var1 - this.bufAbsoluteStart, this.elRawName[this.depth], 0, var4);
               this.elRawNameEnd[this.depth] = var4;
               this.elRawNameLine[this.depth] = this.lineNumber;
               Object var5 = null;
               String var6 = null;
               if (this.processNamespaces) {
                  if (var2 != -1) {
                     var6 = this.elPrefix[this.depth] = this.newString(this.buf, var1 - this.bufAbsoluteStart, var2 - var1);
                     var5 = this.elName[this.depth] = this.newString(this.buf, var2 + 1 - this.bufAbsoluteStart, this.pos - 2 - (var2 - this.bufAbsoluteStart));
                  } else {
                     var6 = this.elPrefix[this.depth] = null;
                     var5 = this.elName[this.depth] = this.newString(this.buf, var1 - this.bufAbsoluteStart, var4);
                  }
               } else {
                  var5 = this.elName[this.depth] = this.newString(this.buf, var1 - this.bufAbsoluteStart, var4);
               }

               while (true) {
                  while (this.isS(var3)) {
                     var3 = this.more();
                  }

                  if (var3 == '>') {
                     break;
                  }

                  if (var3 == '/') {
                     if (this.emptyElementTag) {
                        throw new XmlPullParserException("repeated / in tag declaration", this, null);
                     }

                     this.emptyElementTag = true;
                     var3 = this.more();
                     if (var3 != '>') {
                        throw new XmlPullParserException("expected > to end empty tag not " + this.printable(var3), this, null);
                     }
                     break;
                  }

                  if (!this.isNameStartChar(var3)) {
                     throw new XmlPullParserException("start tag unexpected character " + this.printable(var3), this, null);
                  }

                  var3 = this.parseAttribute();
                  var3 = this.more();
               }

               if (this.processNamespaces) {
                  String var18 = this.getNamespace(var6);
                  if (var18 == null) {
                     if (var6 != null) {
                        throw new XmlPullParserException("could not determine namespace bound to element prefix " + var6, this, null);
                     }

                     var18 = "";
                  }

                  this.elUri[this.depth] = var18;

                  for (int var19 = 0; var19 < this.attributeCount; var19++) {
                     String var21 = this.attributePrefix[var19];
                     if (var21 != null) {
                        String var23 = this.getNamespace(var21);
                        if (var23 == null) {
                           throw new XmlPullParserException("could not determine namespace bound to attribute prefix " + var21, this, null);
                        }

                        this.attributeUri[var19] = var23;
                     } else {
                        this.attributeUri[var19] = "";
                     }
                  }

                  for (int var20 = 1; var20 < this.attributeCount; var20++) {
                     for (int var22 = 0; var22 < var20; var22++) {
                        if (this.attributeUri[var22] == this.attributeUri[var20]
                           && (
                              this.allStringsInterned && this.attributeName[var22].equals(this.attributeName[var20])
                                 || !this.allStringsInterned
                                    && this.attributeNameHash[var22] == this.attributeNameHash[var20]
                                    && this.attributeName[var22].equals(this.attributeName[var20])
                           )) {
                           String var24 = this.attributeName[var22];
                           if (this.attributeUri[var22] != null) {
                              var24 = this.attributeUri[var22] + ":" + var24;
                           }

                           String var11 = this.attributeName[var20];
                           if (this.attributeUri[var20] != null) {
                              var11 = this.attributeUri[var20] + ":" + var11;
                           }

                           throw new XmlPullParserException("duplicated attributes " + var24 + " and " + var11, this, null);
                        }
                     }
                  }
               } else {
                  for (int var7 = 1; var7 < this.attributeCount; var7++) {
                     for (int var8 = 0; var8 < var7; var8++) {
                        if (this.allStringsInterned && this.attributeName[var8].equals(this.attributeName[var7])
                           || !this.allStringsInterned
                              && this.attributeNameHash[var8] == this.attributeNameHash[var7]
                              && this.attributeName[var8].equals(this.attributeName[var7])) {
                           String var9 = this.attributeName[var8];
                           String var10 = this.attributeName[var7];
                           throw new XmlPullParserException("duplicated attributes " + var9 + " and " + var10, this, null);
                        }
                     }
                  }
               }

               this.elNamespaceCount[this.depth] = this.namespaceEnd;
               this.posEnd = this.pos;
               return this.eventType = 2;
            }

            if (var3 == ':' && this.processNamespaces) {
               if (var2 != -1) {
                  throw new XmlPullParserException("only one colon is allowed in name of element when namespaces are enabled", this, null);
               }

               var2 = this.pos - 1 + this.bufAbsoluteStart;
            }
         }
      }
   }

   protected char parseAttribute() throws XmlPullParserException, IOException {
      int var1 = this.posStart + this.bufAbsoluteStart;
      int var2 = this.pos - 1 + this.bufAbsoluteStart;
      int var3 = -1;
      char var4 = this.buf[this.pos - 1];
      if (var4 == ':' && this.processNamespaces) {
         throw new XmlPullParserException("when namespaces processing enabled colon can not be at attribute name start", this, null);
      } else {
         boolean var5 = this.processNamespaces && var4 == 'x';
         int var6 = 0;

         for (var4 = this.more(); this.isNameChar(var4); var4 = this.more()) {
            if (this.processNamespaces) {
               if (var5 && var6 < 5) {
                  if (++var6 == 1) {
                     if (var4 != 'm') {
                        var5 = false;
                     }
                  } else if (var6 == 2) {
                     if (var4 != 'l') {
                        var5 = false;
                     }
                  } else if (var6 == 3) {
                     if (var4 != 'n') {
                        var5 = false;
                     }
                  } else if (var6 == 4) {
                     if (var4 != 's') {
                        var5 = false;
                     }
                  } else if (var6 == 5 && var4 != ':') {
                     throw new XmlPullParserException("after xmlns in attribute name must be colonwhen namespaces are enabled", this, null);
                  }
               }

               if (var4 == ':') {
                  if (var3 != -1) {
                     throw new XmlPullParserException("only one colon is allowed in attribute name when namespaces are enabled", this, null);
                  }

                  var3 = this.pos - 1 + this.bufAbsoluteStart;
               }
            }
         }

         this.ensureAttributesCapacity(this.attributeCount);
         String var7 = null;
         Object var8 = null;
         if (this.processNamespaces) {
            if (var6 < 4) {
               var5 = false;
            }

            if (var5) {
               if (var3 != -1) {
                  int var9 = this.pos - 2 - (var3 - this.bufAbsoluteStart);
                  if (var9 == 0) {
                     throw new XmlPullParserException("namespace prefix is required after xmlns:  when namespaces are enabled", this, null);
                  }

                  var7 = this.newString(this.buf, var3 - this.bufAbsoluteStart + 1, var9);
               }
            } else {
               if (var3 != -1) {
                  int var21 = var3 - var2;
                  var8 = this.attributePrefix[this.attributeCount] = this.newString(this.buf, var2 - this.bufAbsoluteStart, var21);
                  int var10 = this.pos - 2 - (var3 - this.bufAbsoluteStart);
                  var7 = this.attributeName[this.attributeCount] = this.newString(this.buf, var3 - this.bufAbsoluteStart + 1, var10);
               } else {
                  var8 = this.attributePrefix[this.attributeCount] = null;
                  var7 = this.attributeName[this.attributeCount] = this.newString(
                     this.buf, var2 - this.bufAbsoluteStart, this.pos - 1 - (var2 - this.bufAbsoluteStart)
                  );
               }

               if (!this.allStringsInterned) {
                  this.attributeNameHash[this.attributeCount] = var7.hashCode();
               }
            }
         } else {
            var7 = this.attributeName[this.attributeCount] = this.newString(
               this.buf, var2 - this.bufAbsoluteStart, this.pos - 1 - (var2 - this.bufAbsoluteStart)
            );
            if (!this.allStringsInterned) {
               this.attributeNameHash[this.attributeCount] = var7.hashCode();
            }
         }

         while (this.isS(var4)) {
            var4 = this.more();
         }

         if (var4 != '=') {
            throw new XmlPullParserException("expected = after attribute name", this, null);
         } else {
            var4 = this.more();

            while (this.isS(var4)) {
               var4 = this.more();
            }

            char var22 = var4;
            if (var4 != '"' && var4 != '\'') {
               throw new XmlPullParserException("attribute value must start with quotation or apostrophe not " + this.printable(var4), this, null);
            } else {
               boolean var23 = false;
               this.usePC = false;
               this.pcStart = this.pcEnd;
               this.posStart = this.pos;

               while (true) {
                  var4 = this.more();
                  if (var4 == var22) {
                     if (this.processNamespaces && var5) {
                        Object var25 = null;
                        if (!this.usePC) {
                           var25 = this.newStringIntern(this.buf, this.posStart, this.pos - 1 - this.posStart);
                        } else {
                           var25 = this.newStringIntern(this.pc, this.pcStart, this.pcEnd - this.pcStart);
                        }

                        this.ensureNamespacesCapacity(this.namespaceEnd);
                        int var27 = -1;
                        if (var3 != -1) {
                           if (var25.length() == 0) {
                              throw new XmlPullParserException("non-default namespace can not be declared to be empty string", this, null);
                           }

                           this.namespacePrefix[this.namespaceEnd] = var7;
                           if (!this.allStringsInterned) {
                              var27 = this.namespacePrefixHash[this.namespaceEnd] = var7.hashCode();
                           }
                        } else {
                           this.namespacePrefix[this.namespaceEnd] = null;
                           if (!this.allStringsInterned) {
                              var27 = this.namespacePrefixHash[this.namespaceEnd] = -1;
                           }
                        }

                        this.namespaceUri[this.namespaceEnd] = (String)var25;
                        int var28 = this.elNamespaceCount[this.depth - 1];

                        for (int var29 = this.namespaceEnd - 1; var29 >= var28; var29--) {
                           if ((this.allStringsInterned || var7 == null) && this.namespacePrefix[var29] == var7
                              || !this.allStringsInterned
                                 && var7 != null
                                 && this.namespacePrefixHash[var29] == var27
                                 && var7.equals(this.namespacePrefix[var29])) {
                              String var30 = var7 == null ? "default" : "'" + var7 + "'";
                              throw new XmlPullParserException("duplicated namespace declaration for " + var30 + " prefix", this, null);
                           }
                        }

                        this.namespaceEnd++;
                     } else {
                        if (!this.usePC) {
                           this.attributeValue[this.attributeCount] = new String(this.buf, this.posStart, this.pos - 1 - this.posStart);
                        } else {
                           this.attributeValue[this.attributeCount] = new String(this.pc, this.pcStart, this.pcEnd - this.pcStart);
                        }

                        this.attributeCount++;
                     }

                     this.posStart = var1 - this.bufAbsoluteStart;
                     return var4;
                  }

                  if (var4 == '<') {
                     throw new XmlPullParserException("markup not allowed inside attribute value - illegal < ", this, null);
                  }

                  if (var4 == '&') {
                     this.posEnd = this.pos - 1;
                     if (!this.usePC) {
                        boolean var11 = this.posEnd > this.posStart;
                        if (var11) {
                           this.joinPC();
                        } else {
                           this.usePC = true;
                           this.pcStart = this.pcEnd = 0;
                        }
                     }

                     char[] var24 = this.parseEntityRef();
                     if (var24 == null) {
                        if (this.entityRefName == null) {
                           this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);
                        }

                        throw new XmlPullParserException("could not resolve entity named '" + this.printable(this.entityRefName) + "'", this, null);
                     }

                     for (char var15 : var24) {
                        if (this.pcEnd >= this.pc.length) {
                           this.ensurePC(this.pcEnd);
                        }

                        this.pc[this.pcEnd++] = var15;
                     }
                  } else if (var4 != '\t' && var4 != '\n' && var4 != '\r') {
                     if (this.usePC) {
                        if (this.pcEnd >= this.pc.length) {
                           this.ensurePC(this.pcEnd);
                        }

                        this.pc[this.pcEnd++] = var4;
                     }
                  } else {
                     if (!this.usePC) {
                        this.posEnd = this.pos - 1;
                        if (this.posEnd > this.posStart) {
                           this.joinPC();
                        } else {
                           this.usePC = true;
                           this.pcEnd = this.pcStart = 0;
                        }
                     }

                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     if (var4 != '\n' || !var23) {
                        this.pc[this.pcEnd++] = ' ';
                     }
                  }

                  var23 = var4 == '\r';
               }
            }
         }
      }
   }

   protected char[] parseEntityRef() throws XmlPullParserException, IOException {
      this.entityRefName = null;
      this.posStart = this.pos;
      char var1 = this.more();
      if (var1 == '#') {
         char var10 = 0;
         var1 = this.more();
         StringBuilder var11 = new StringBuilder();
         boolean var4 = var1 == 'x';
         if (var4) {
            while (true) {
               var1 = this.more();
               if (var1 >= '0' && var1 <= '9') {
                  var10 = (char)(var10 * 16 + (var1 - '0'));
                  var11.append(var1);
               } else if (var1 < 'a' || var1 > 'f') {
                  if (var1 < 'A' || var1 > 'F') {
                     if (var1 != ';') {
                        throw new XmlPullParserException("character reference (with hex value) may not contain " + this.printable(var1), this, null);
                     }
                     break;
                  }

                  var10 = (char)(var10 * 16 + (var1 - '7'));
                  var11.append(var1);
               } else {
                  var10 = (char)(var10 * 16 + (var1 - 'W'));
                  var11.append(var1);
               }
            }
         } else {
            while (var1 >= '0' && var1 <= '9') {
               var10 = (char)(var10 * '\n' + (var1 - '0'));
               var11.append(var1);
               var1 = this.more();
            }

            if (var1 != ';') {
               throw new XmlPullParserException("character reference (with decimal value) may not contain " + this.printable(var1), this, null);
            }
         }

         this.posEnd = this.pos - 1;

         try {
            this.charRefOneCharBuf = toChars(Integer.parseInt(var11.toString(), var4 ? 16 : 10));
         } catch (IllegalArgumentException var6) {
            throw new XmlPullParserException(
               "character reference (with " + (var4 ? "hex" : "decimal") + " value " + var11.toString() + ") is invalid", this, null
            );
         }

         if (this.tokenize) {
            this.text = this.newString(this.charRefOneCharBuf, 0, this.charRefOneCharBuf.length);
         }

         return this.charRefOneCharBuf;
      } else if (!this.isNameStartChar(var1)) {
         throw new XmlPullParserException("entity reference names can not start with character '" + this.printable(var1) + "'", this, null);
      } else {
         do {
            var1 = this.more();
            if (var1 == ';') {
               this.posEnd = this.pos - 1;
               int var2 = this.posEnd - this.posStart;
               if (var2 == 2 && this.buf[this.posStart] == 'l' && this.buf[this.posStart + 1] == 't') {
                  if (this.tokenize) {
                     this.text = "<";
                  }

                  this.charRefOneCharBuf[0] = '<';
                  return this.charRefOneCharBuf;
               } else if (var2 == 3 && this.buf[this.posStart] == 'a' && this.buf[this.posStart + 1] == 'm' && this.buf[this.posStart + 2] == 'p') {
                  if (this.tokenize) {
                     this.text = "&";
                  }

                  this.charRefOneCharBuf[0] = '&';
                  return this.charRefOneCharBuf;
               } else if (var2 == 2 && this.buf[this.posStart] == 'g' && this.buf[this.posStart + 1] == 't') {
                  if (this.tokenize) {
                     this.text = ">";
                  }

                  this.charRefOneCharBuf[0] = '>';
                  return this.charRefOneCharBuf;
               } else if (var2 == 4
                  && this.buf[this.posStart] == 'a'
                  && this.buf[this.posStart + 1] == 'p'
                  && this.buf[this.posStart + 2] == 'o'
                  && this.buf[this.posStart + 3] == 's') {
                  if (this.tokenize) {
                     this.text = "'";
                  }

                  this.charRefOneCharBuf[0] = '\'';
                  return this.charRefOneCharBuf;
               } else if (var2 == 4
                  && this.buf[this.posStart] == 'q'
                  && this.buf[this.posStart + 1] == 'u'
                  && this.buf[this.posStart + 2] == 'o'
                  && this.buf[this.posStart + 3] == 't') {
                  if (this.tokenize) {
                     this.text = "\"";
                  }

                  this.charRefOneCharBuf[0] = '"';
                  return this.charRefOneCharBuf;
               } else {
                  char[] var3 = this.lookuEntityReplacement(var2);
                  if (var3 != null) {
                     return var3;
                  } else {
                     if (this.tokenize) {
                        this.text = null;
                     }

                     return null;
                  }
               }
            }
         } while (this.isNameChar(var1));

         throw new XmlPullParserException("entity reference name can not contain character " + this.printable(var1) + "'", this, null);
      }
   }

   protected char[] lookuEntityReplacement(int var1) throws XmlPullParserException, IOException {
      if (!this.allStringsInterned) {
         int var2 = fastHash(this.buf, this.posStart, this.posEnd - this.posStart);

         label53:
         for (int var3 = this.entityEnd - 1; var3 >= 0; var3--) {
            if (var2 == this.entityNameHash[var3] && var1 == this.entityNameBuf[var3].length) {
               char[] var4 = this.entityNameBuf[var3];

               for (int var5 = 0; var5 < var1; var5++) {
                  if (this.buf[this.posStart + var5] != var4[var5]) {
                     continue label53;
                  }
               }

               if (this.tokenize) {
                  this.text = this.entityReplacement[var3];
               }

               return this.entityReplacementBuf[var3];
            }
         }
      } else {
         this.entityRefName = this.newString(this.buf, this.posStart, this.posEnd - this.posStart);

         for (int var6 = this.entityEnd - 1; var6 >= 0; var6--) {
            if (this.entityRefName == this.entityName[var6]) {
               if (this.tokenize) {
                  this.text = this.entityReplacement[var6];
               }

               return this.entityReplacementBuf[var6];
            }
         }
      }

      return null;
   }

   protected void parseComment() throws XmlPullParserException, IOException {
      char var1 = this.more();
      if (var1 != '-') {
         throw new XmlPullParserException("expected <!-- for comment start", this, null);
      } else {
         if (this.tokenize) {
            this.posStart = this.pos;
         }

         int var2 = this.lineNumber;
         int var3 = this.columnNumber;

         try {
            boolean var4 = this.tokenize && !this.roundtripSupported;
            boolean var5 = false;
            boolean var6 = false;
            boolean var7 = false;

            while (true) {
               var1 = this.more();
               if (var7 && var1 != '>') {
                  throw new XmlPullParserException("in comment after two dashes (--) next character must be > not " + this.printable(var1), this, null);
               }

               if (var1 == '-') {
                  if (!var6) {
                     var6 = true;
                  } else {
                     var7 = true;
                     var6 = false;
                  }
               } else if (var1 == '>') {
                  if (var7) {
                     break;
                  }

                  var7 = false;
                  var6 = false;
               } else {
                  var6 = false;
               }

               if (var4) {
                  if (var1 == '\r') {
                     var5 = true;
                     if (!this.usePC) {
                        this.posEnd = this.pos - 1;
                        if (this.posEnd > this.posStart) {
                           this.joinPC();
                        } else {
                           this.usePC = true;
                           this.pcStart = this.pcEnd = 0;
                        }
                     }

                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     this.pc[this.pcEnd++] = '\n';
                  } else if (var1 == '\n') {
                     if (!var5 && this.usePC) {
                        if (this.pcEnd >= this.pc.length) {
                           this.ensurePC(this.pcEnd);
                        }

                        this.pc[this.pcEnd++] = '\n';
                     }

                     var5 = false;
                  } else {
                     if (this.usePC) {
                        if (this.pcEnd >= this.pc.length) {
                           this.ensurePC(this.pcEnd);
                        }

                        this.pc[this.pcEnd++] = var1;
                     }

                     var5 = false;
                  }
               }
            }
         } catch (EOFException var8) {
            throw new XmlPullParserException("comment started on line " + var2 + " and column " + var3 + " was not closed", this, var8);
         }

         if (this.tokenize) {
            this.posEnd = this.pos - 3;
            if (this.usePC) {
               this.pcEnd -= 2;
            }
         }
      }
   }

   protected boolean parsePI() throws XmlPullParserException, IOException {
      if (this.tokenize) {
         this.posStart = this.pos;
      }

      int var1 = this.lineNumber;
      int var2 = this.columnNumber;
      int var3 = this.pos;
      int var4 = -1;
      boolean var5 = this.tokenize && !this.roundtripSupported;
      boolean var6 = false;

      try {
         boolean var7 = false;
         boolean var8 = false;
         char var9 = this.more();
         if (this.isS(var9)) {
            throw new XmlPullParserException("processing instruction PITarget must be exactly after <? and not white space character", this, null);
         }

         while (true) {
            if (var9 == '?') {
               if (!var7) {
                  throw new XmlPullParserException("processing instruction PITarget name not found", this, null);
               }

               var8 = true;
            } else if (var9 == '>') {
               if (var8) {
                  break;
               }

               if (!var7) {
                  throw new XmlPullParserException("processing instruction PITarget name not found", this, null);
               }
            } else {
               if (var4 == -1 && this.isS(var9)) {
                  var4 = this.pos - 1;
                  if (var4 - var3 == 3
                     && (this.buf[var3] == 'x' || this.buf[var3] == 'X')
                     && (this.buf[var3 + 1] == 'm' || this.buf[var3 + 1] == 'M')
                     && (this.buf[var3 + 2] == 'l' || this.buf[var3 + 2] == 'L')) {
                     if (var3 > 3) {
                        throw new XmlPullParserException("processing instruction can not have PITarget with reserved xml name", this, null);
                     }

                     if (this.buf[var3] != 'x' && this.buf[var3 + 1] != 'm' && this.buf[var3 + 2] != 'l') {
                        throw new XmlPullParserException("XMLDecl must have xml name in lowercase", this, null);
                     }

                     this.parseXmlDecl(var9);
                     if (this.tokenize) {
                        this.posEnd = this.pos - 2;
                     }

                     int var10 = var3 + 3;
                     int var11 = this.pos - 2 - var10;
                     this.xmlDeclContent = this.newString(this.buf, var10, var11);
                     return false;
                  }
               }

               var8 = false;
            }

            if (var5) {
               if (var9 == '\r') {
                  var6 = true;
                  if (!this.usePC) {
                     this.posEnd = this.pos - 1;
                     if (this.posEnd > this.posStart) {
                        this.joinPC();
                     } else {
                        this.usePC = true;
                        this.pcStart = this.pcEnd = 0;
                     }
                  }

                  if (this.pcEnd >= this.pc.length) {
                     this.ensurePC(this.pcEnd);
                  }

                  this.pc[this.pcEnd++] = '\n';
               } else if (var9 == '\n') {
                  if (!var6 && this.usePC) {
                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     this.pc[this.pcEnd++] = '\n';
                  }

                  var6 = false;
               } else {
                  if (this.usePC) {
                     if (this.pcEnd >= this.pc.length) {
                        this.ensurePC(this.pcEnd);
                     }

                     this.pc[this.pcEnd++] = var9;
                  }

                  var6 = false;
               }
            }

            var7 = true;
            var9 = this.more();
         }
      } catch (EOFException var12) {
         throw new XmlPullParserException("processing instruction started on line " + var1 + " and column " + var2 + " was not closed", this, var12);
      }

      if (var4 == -1) {
         var4 = this.pos - 2 + this.bufAbsoluteStart;
      }

      if (this.tokenize) {
         this.posEnd = this.pos - 2;
         if (var5) {
            this.pcEnd--;
         }
      }

      return true;
   }

   protected void parseXmlDecl(char var1) throws XmlPullParserException, IOException {
      this.preventBufferCompaction = true;
      this.bufStart = 0;
      var1 = this.skipS(var1);
      var1 = this.requireInput(var1, VERSION);
      var1 = this.skipS(var1);
      if (var1 != '=') {
         throw new XmlPullParserException("expected equals sign (=) after version and not " + this.printable(var1), this, null);
      } else {
         var1 = this.more();
         var1 = this.skipS(var1);
         if (var1 != '\'' && var1 != '"') {
            throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after version and not " + this.printable(var1), this, null);
         } else {
            char var2 = var1;
            int var3 = this.pos;

            for (char var10 = this.more(); var10 != var2; var10 = this.more()) {
               if ((var10 < 'a' || var10 > 'z')
                  && (var10 < 'A' || var10 > 'Z')
                  && (var10 < '0' || var10 > '9')
                  && var10 != '_'
                  && var10 != '.'
                  && var10 != ':'
                  && var10 != '-') {
                  throw new XmlPullParserException("<?xml version value expected to be in ([a-zA-Z0-9_.:] | '-') not " + this.printable(var10), this, null);
               }
            }

            int var4 = this.pos - 1;
            this.parseXmlDeclWithVersion(var3, var4);
            this.preventBufferCompaction = false;
         }
      }
   }

   protected void parseXmlDeclWithVersion(int var1, int var2) throws XmlPullParserException, IOException {
      if (var2 - var1 == 3 && this.buf[var1] == '1' && this.buf[var1 + 1] == '.' && this.buf[var1 + 2] == '0') {
         this.xmlDeclVersion = this.newString(this.buf, var1, var2 - var1);
         char var3 = this.more();
         var3 = this.skipS(var3);
         if (var3 == 'e') {
            var3 = this.more();
            var3 = this.requireInput(var3, NCODING);
            var3 = this.skipS(var3);
            if (var3 != '=') {
               throw new XmlPullParserException("expected equals sign (=) after encoding and not " + this.printable(var3), this, null);
            }

            var3 = this.more();
            var3 = this.skipS(var3);
            if (var3 != '\'' && var3 != '"') {
               throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after encoding and not " + this.printable(var3), this, null);
            }

            char var4 = var3;
            int var5 = this.pos;
            var3 = this.more();
            if ((var3 < 'a' || var3 > 'z') && (var3 < 'A' || var3 > 'Z')) {
               throw new XmlPullParserException("<?xml encoding name expected to start with [A-Za-z] not " + this.printable(var3), this, null);
            }

            for (char var14 = this.more(); var14 != var4; var14 = this.more()) {
               if ((var14 < 'a' || var14 > 'z') && (var14 < 'A' || var14 > 'Z') && (var14 < '0' || var14 > '9') && var14 != '.' && var14 != '_' && var14 != '-'
                  )
                {
                  throw new XmlPullParserException("<?xml encoding value expected to be in ([A-Za-z0-9._] | '-') not " + this.printable(var14), this, null);
               }
            }

            int var6 = this.pos - 1;
            this.inputEncoding = this.newString(this.buf, var5, var6 - var5);
            var3 = this.more();
         }

         var3 = this.skipS(var3);
         if (var3 == 's') {
            var3 = this.more();
            var3 = this.requireInput(var3, TANDALONE);
            var3 = this.skipS(var3);
            if (var3 != '=') {
               throw new XmlPullParserException("expected equals sign (=) after standalone and not " + this.printable(var3), this, null);
            }

            var3 = this.more();
            var3 = this.skipS(var3);
            if (var3 != '\'' && var3 != '"') {
               throw new XmlPullParserException("expected apostrophe (') or quotation mark (\") after encoding and not " + this.printable(var3), this, null);
            }

            int var26 = this.pos;
            var3 = this.more();
            if (var3 == 'y') {
               var3 = this.requireInput(var3, YES);
               this.xmlDeclStandalone = true;
            } else {
               if (var3 != 'n') {
                  throw new XmlPullParserException("expected 'yes' or 'no' after standalone and not " + this.printable(var3), this, null);
               }

               var3 = this.requireInput(var3, NO);
               this.xmlDeclStandalone = false;
            }

            if (var3 != var3) {
               throw new XmlPullParserException("expected " + var3 + " after standalone value not " + this.printable(var3), this, null);
            }

            var3 = this.more();
         }

         var3 = this.skipS(var3);
         if (var3 != '?') {
            throw new XmlPullParserException("expected ?> as last part of <?xml not " + this.printable(var3), this, null);
         } else {
            var3 = this.more();
            if (var3 != '>') {
               throw new XmlPullParserException("expected ?> as last part of <?xml not " + this.printable(var3), this, null);
            }
         }
      } else {
         throw new XmlPullParserException(
            "only 1.0 is supported as <?xml version not '" + this.printable(new String(this.buf, var1, var2 - var1)) + "'", this, null
         );
      }
   }

   protected void parseDocdecl() throws XmlPullParserException, IOException {
      char var1 = this.more();
      if (var1 != 'O') {
         throw new XmlPullParserException("expected <!DOCTYPE", this, null);
      } else {
         var1 = this.more();
         if (var1 != 'C') {
            throw new XmlPullParserException("expected <!DOCTYPE", this, null);
         } else {
            var1 = this.more();
            if (var1 != 'T') {
               throw new XmlPullParserException("expected <!DOCTYPE", this, null);
            } else {
               var1 = this.more();
               if (var1 != 'Y') {
                  throw new XmlPullParserException("expected <!DOCTYPE", this, null);
               } else {
                  var1 = this.more();
                  if (var1 != 'P') {
                     throw new XmlPullParserException("expected <!DOCTYPE", this, null);
                  } else {
                     var1 = this.more();
                     if (var1 != 'E') {
                        throw new XmlPullParserException("expected <!DOCTYPE", this, null);
                     } else {
                        this.posStart = this.pos;
                        int var2 = 0;
                        boolean var3 = this.tokenize && !this.roundtripSupported;
                        boolean var4 = false;

                        while (true) {
                           var1 = this.more();
                           if (var1 == '[') {
                              var2++;
                           }

                           if (var1 == ']') {
                              var2--;
                           }

                           if (var1 == '>' && var2 == 0) {
                              this.posEnd = this.pos - 1;
                              return;
                           }

                           if (var3) {
                              if (var1 == '\r') {
                                 var4 = true;
                                 if (!this.usePC) {
                                    this.posEnd = this.pos - 1;
                                    if (this.posEnd > this.posStart) {
                                       this.joinPC();
                                    } else {
                                       this.usePC = true;
                                       this.pcStart = this.pcEnd = 0;
                                    }
                                 }

                                 if (this.pcEnd >= this.pc.length) {
                                    this.ensurePC(this.pcEnd);
                                 }

                                 this.pc[this.pcEnd++] = '\n';
                              } else if (var1 == '\n') {
                                 if (!var4 && this.usePC) {
                                    if (this.pcEnd >= this.pc.length) {
                                       this.ensurePC(this.pcEnd);
                                    }

                                    this.pc[this.pcEnd++] = '\n';
                                 }

                                 var4 = false;
                              } else {
                                 if (this.usePC) {
                                    if (this.pcEnd >= this.pc.length) {
                                       this.ensurePC(this.pcEnd);
                                    }

                                    this.pc[this.pcEnd++] = var1;
                                 }

                                 var4 = false;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   protected void parseCDSect(boolean var1) throws XmlPullParserException, IOException {
      char var2 = this.more();
      if (var2 != 'C') {
         throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
      } else {
         var2 = this.more();
         if (var2 != 'D') {
            throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
         } else {
            var2 = this.more();
            if (var2 != 'A') {
               throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
            } else {
               var2 = this.more();
               if (var2 != 'T') {
                  throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
               } else {
                  var2 = this.more();
                  if (var2 != 'A') {
                     throw new XmlPullParserException("expected <[CDATA[ for comment start", this, null);
                  } else {
                     var2 = this.more();
                     if (var2 != '[') {
                        throw new XmlPullParserException("expected <![CDATA[ for comment start", this, null);
                     } else {
                        int var3 = this.pos + this.bufAbsoluteStart;
                        int var4 = this.lineNumber;
                        int var5 = this.columnNumber;
                        boolean var6 = !this.tokenize || !this.roundtripSupported;

                        try {
                           if (var6 && var1 && !this.usePC) {
                              if (this.posEnd > this.posStart) {
                                 this.joinPC();
                              } else {
                                 this.usePC = true;
                                 this.pcStart = this.pcEnd = 0;
                              }
                           }

                           boolean var7 = false;
                           boolean var8 = false;
                           boolean var9 = false;

                           while (true) {
                              var2 = this.more();
                              if (var2 == ']') {
                                 if (!var7) {
                                    var7 = true;
                                 } else {
                                    var8 = true;
                                 }
                              } else if (var2 == '>') {
                                 if (var7 && var8) {
                                    break;
                                 }

                                 var8 = false;
                                 var7 = false;
                              } else if (var7) {
                                 var7 = false;
                              }

                              if (var6) {
                                 if (var2 == '\r') {
                                    var9 = true;
                                    this.posStart = var3 - this.bufAbsoluteStart;
                                    this.posEnd = this.pos - 1;
                                    if (!this.usePC) {
                                       if (this.posEnd > this.posStart) {
                                          this.joinPC();
                                       } else {
                                          this.usePC = true;
                                          this.pcStart = this.pcEnd = 0;
                                       }
                                    }

                                    if (this.pcEnd >= this.pc.length) {
                                       this.ensurePC(this.pcEnd);
                                    }

                                    this.pc[this.pcEnd++] = '\n';
                                 } else if (var2 == '\n') {
                                    if (!var9 && this.usePC) {
                                       if (this.pcEnd >= this.pc.length) {
                                          this.ensurePC(this.pcEnd);
                                       }

                                       this.pc[this.pcEnd++] = '\n';
                                    }

                                    var9 = false;
                                 } else {
                                    if (this.usePC) {
                                       if (this.pcEnd >= this.pc.length) {
                                          this.ensurePC(this.pcEnd);
                                       }

                                       this.pc[this.pcEnd++] = var2;
                                    }

                                    var9 = false;
                                 }
                              }
                           }
                        } catch (EOFException var10) {
                           throw new XmlPullParserException("CDATA section started on line " + var4 + " and column " + var5 + " was not closed", this, var10);
                        }

                        if (var6 && this.usePC) {
                           this.pcEnd -= 2;
                        }

                        this.posStart = var3 - this.bufAbsoluteStart;
                        this.posEnd = this.pos - 3;
                     }
                  }
               }
            }
         }
      }
   }

   protected void fillBuf() throws IOException, XmlPullParserException {
      if (this.reader == null) {
         throw new XmlPullParserException("reader must be set before parsing is started");
      } else {
         if (this.bufEnd > this.bufSoftLimit) {
            boolean var1 = this.bufStart > this.bufSoftLimit;
            boolean var2 = false;
            if (this.preventBufferCompaction) {
               var1 = false;
               var2 = true;
            } else if (!var1) {
               if (this.bufStart < this.buf.length / 2) {
                  var2 = true;
               } else {
                  var1 = true;
               }
            }

            if (var1) {
               System.arraycopy(this.buf, this.bufStart, this.buf, 0, this.bufEnd - this.bufStart);
            } else {
               if (!var2) {
                  throw new XmlPullParserException("internal error in fillBuffer()");
               }

               int var3 = 2 * this.buf.length;
               char[] var4 = new char[var3];
               System.arraycopy(this.buf, this.bufStart, var4, 0, this.bufEnd - this.bufStart);
               this.buf = var4;
               if (this.bufLoadFactor > 0) {
                  this.bufSoftLimit = this.bufLoadFactor * this.buf.length / 100;
               }
            }

            this.bufEnd = this.bufEnd - this.bufStart;
            this.pos = this.pos - this.bufStart;
            this.posStart = this.posStart - this.bufStart;
            this.posEnd = this.posEnd - this.bufStart;
            this.bufAbsoluteStart = this.bufAbsoluteStart + this.bufStart;
            this.bufStart = 0;
         }

         int var6 = this.buf.length - this.bufEnd > 8192 ? 8192 : this.buf.length - this.bufEnd;
         int var7 = this.reader.read(this.buf, this.bufEnd, var6);
         if (var7 > 0) {
            this.bufEnd += var7;
         } else if (var7 != -1) {
            throw new IOException("error reading input, returned " + var7);
         } else if (this.bufAbsoluteStart == 0 && this.pos == 0) {
            throw new EOFException("input contained no data");
         } else if (this.seenRoot && this.depth == 0) {
            this.reachedEnd = true;
         } else {
            StringBuilder var8 = new StringBuilder();
            if (this.depth > 0) {
               if (this.elRawName != null && this.elRawName[this.depth] != null) {
                  var8.append(" - expected end tag");
                  if (this.depth > 1) {
                     var8.append("s");
                  }

                  var8.append(" ");

                  for (int var10 = this.depth; var10 > 0; var10--) {
                     if (this.elRawName != null && this.elRawName[var10] != null) {
                        String var12 = new String(this.elRawName[var10], 0, this.elRawNameEnd[var10]);
                        var8.append("</").append(var12).append('>');
                     } else {
                        String var5 = new String(this.buf, this.posStart + 1, this.pos - this.posStart - 1);
                        var8.append(" - expected the opening tag <").append(var5).append("...>");
                     }
                  }

                  var8.append(" to close");

                  for (int var11 = this.depth; var11 > 0; var11--) {
                     if (var11 != this.depth) {
                        var8.append(" and");
                     }

                     if (this.elRawName != null && this.elRawName[var11] != null) {
                        String var14 = new String(this.elRawName[var11], 0, this.elRawNameEnd[var11]);
                        var8.append(" start tag <").append(var14).append(">");
                        var8.append(" from line ").append(this.elRawNameLine[var11]);
                     } else {
                        String var13 = new String(this.buf, this.posStart + 1, this.pos - this.posStart - 1);
                        var8.append(" start tag <").append(var13).append(">");
                        var8.append(" from line ").append(this.elRawNameLine[var11]);
                     }
                  }

                  var8.append(", parser stopped on");
               } else {
                  String var9 = new String(this.buf, this.posStart + 1, this.pos - this.posStart - 1);
                  var8.append(" - expected the opening tag <").append(var9).append("...>");
               }
            }

            throw new EOFException("no more data available" + var8.toString() + this.getPositionDescription());
         }
      }
   }

   protected char more() throws IOException, XmlPullParserException {
      if (this.pos >= this.bufEnd) {
         this.fillBuf();
         if (this.reachedEnd) {
            return '\uffff';
         }
      }

      char var1 = this.buf[this.pos++];
      if (var1 == '\n') {
         this.lineNumber++;
         this.columnNumber = 1;
      } else {
         this.columnNumber++;
      }

      return var1;
   }

   protected void ensurePC(int var1) {
      int var2 = var1 > 8192 ? 2 * var1 : 16384;
      char[] var3 = new char[var2];
      System.arraycopy(this.pc, 0, var3, 0, this.pcEnd);
      this.pc = var3;
   }

   protected void joinPC() {
      int var1 = this.posEnd - this.posStart;
      int var2 = this.pcEnd + var1 + 1;
      if (var2 >= this.pc.length) {
         this.ensurePC(var2);
      }

      System.arraycopy(this.buf, this.posStart, this.pc, this.pcEnd, var1);
      this.pcEnd += var1;
      this.usePC = true;
   }

   protected char requireInput(char var1, char[] var2) throws XmlPullParserException, IOException {
      for (char var6 : var2) {
         if (var1 != var6) {
            throw new XmlPullParserException("expected " + this.printable(var6) + " in " + new String(var2) + " and not " + this.printable(var1), this, null);
         }

         var1 = this.more();
      }

      return var1;
   }

   protected char requireNextS() throws XmlPullParserException, IOException {
      char var1 = this.more();
      if (!this.isS(var1)) {
         throw new XmlPullParserException("white space is required and not " + this.printable(var1), this, null);
      } else {
         return this.skipS(var1);
      }
   }

   protected char skipS(char var1) throws XmlPullParserException, IOException {
      while (this.isS(var1)) {
         var1 = this.more();
      }

      return var1;
   }

   private static final void setName(char var0) {
      lookupNameChar[var0] = true;
   }

   private static final void setNameStart(char var0) {
      lookupNameStartChar[var0] = true;
      setName(var0);
   }

   protected boolean isNameStartChar(char var1) {
      return var1 < 1024 && lookupNameStartChar[var1] || var1 >= 1024 && var1 <= 8231 || var1 >= 8234 && var1 <= 8591 || var1 >= 10240 && var1 <= '\uffef';
   }

   protected boolean isNameChar(char var1) {
      return var1 < 1024 && lookupNameChar[var1] || var1 >= 1024 && var1 <= 8231 || var1 >= 8234 && var1 <= 8591 || var1 >= 10240 && var1 <= '\uffef';
   }

   protected boolean isS(char var1) {
      return var1 == ' ' || var1 == '\n' || var1 == '\r' || var1 == '\t';
   }

   protected String printable(char var1) {
      if (var1 == '\n') {
         return "\\n";
      } else if (var1 == '\r') {
         return "\\r";
      } else if (var1 == '\t') {
         return "\\t";
      } else if (var1 == '\'') {
         return "\\'";
      } else {
         return var1 <= 127 && var1 >= ' ' ? "" + var1 : "\\u" + Integer.toHexString(var1);
      }
   }

   protected String printable(String var1) {
      if (var1 == null) {
         return null;
      } else {
         int var2 = var1.length();
         StringBuilder var3 = new StringBuilder(var2 + 10);

         for (int var4 = 0; var4 < var2; var4++) {
            var3.append(this.printable(var1.charAt(var4)));
         }

         return var3.toString();
      }
   }

   private static int toCodePoint(char var0, char var1) {
      int var2 = (var0 & 1023) << 10;
      int var3 = var1 & 1023;
      return (var2 | var3) + 65536;
   }

   private static boolean isHighSurrogate(char var0) {
      return '\ud800' <= var0 && '\udbff' >= var0;
   }

   private static boolean isValidCodePoint(int var0) {
      return var0 == 9 || var0 == 10 || var0 == 13 || 32 <= var0 && var0 <= 55295 || 57344 <= var0 && var0 <= 65533 || 65536 <= var0 && var0 <= 1114111;
   }

   private static boolean isSupplementaryCodePoint(int var0) {
      return 65536 <= var0 && 1114111 >= var0;
   }

   public static char[] toChars(int var0) {
      if (!isValidCodePoint(var0)) {
         throw new IllegalArgumentException();
      } else if (isSupplementaryCodePoint(var0)) {
         int var1 = var0 - 65536;
         int var2 = 55296 | var1 >> 10 & 1023;
         int var3 = 56320 | var1 & 1023;
         return new char[]{(char)var2, (char)var3};
      } else {
         return new char[]{(char)var0};
      }
   }

   static {
      setNameStart(':');

      for (char var0 = 'A'; var0 <= 'Z'; var0++) {
         setNameStart(var0);
      }

      setNameStart('_');

      for (char var1 = 'a'; var1 <= 'z'; var1++) {
         setNameStart(var1);
      }

      for (char var2 = 192; var2 <= 767; var2++) {
         setNameStart(var2);
      }

      for (char var3 = 880; var3 <= 893; var3++) {
         setNameStart(var3);
      }

      for (char var4 = 895; var4 < 1024; var4++) {
         setNameStart(var4);
      }

      setName('-');
      setName('.');

      for (char var5 = '0'; var5 <= '9'; var5++) {
         setName(var5);
      }

      setName('·');

      for (char var6 = 768; var6 <= 879; var6++) {
         setName(var6);
      }
   }
}
