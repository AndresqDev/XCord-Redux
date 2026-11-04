package org.codehaus.plexus.util.xml.pull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class MXSerializer implements XmlSerializer {
   protected static final String XML_URI = "http://www.w3.org/XML/1998/namespace";
   protected static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
   private static final boolean TRACE_SIZING = false;
   protected final String FEATURE_SERIALIZER_ATTVALUE_USE_APOSTROPHE = "http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe";
   protected final String FEATURE_NAMES_INTERNED = "http://xmlpull.org/v1/doc/features.html#names-interned";
   protected final String PROPERTY_SERIALIZER_INDENTATION = "http://xmlpull.org/v1/doc/properties.html#serializer-indentation";
   protected final String PROPERTY_SERIALIZER_LINE_SEPARATOR = "http://xmlpull.org/v1/doc/properties.html#serializer-line-separator";
   protected static final String PROPERTY_LOCATION = "http://xmlpull.org/v1/doc/properties.html#location";
   protected boolean namesInterned;
   protected boolean attributeUseApostrophe;
   protected String indentationString = null;
   protected String lineSeparator = "\n";
   protected String location;
   protected Writer out;
   protected int autoDeclaredPrefixes;
   protected int depth = 0;
   protected String[] elNamespace = new String[2];
   protected String[] elName = new String[this.elNamespace.length];
   protected int[] elNamespaceCount = new int[this.elNamespace.length];
   protected int namespaceEnd = 0;
   protected String[] namespacePrefix = new String[8];
   protected String[] namespaceUri = new String[this.namespacePrefix.length];
   protected boolean finished;
   protected boolean pastRoot;
   protected boolean setPrefixCalled;
   protected boolean startTagIncomplete;
   protected boolean doIndent;
   protected boolean seenTag;
   protected boolean seenBracket;
   protected boolean seenBracketBracket;
   private static final int BUF_LEN = Runtime.getRuntime().freeMemory() > 1000000L ? 8192 : 256;
   protected char[] buf = new char[BUF_LEN];
   protected static final String[] precomputedPrefixes = new String[32];
   private boolean checkNamesInterned = false;
   protected int offsetNewLine;
   protected int indentationJump;
   protected char[] indentationBuf;
   protected int maxIndentLevel;
   protected boolean writeLineSeparator;
   protected boolean writeIndentation;

   private void checkInterning(String var1) {
      if (this.namesInterned && var1 != var1.intern()) {
         throw new IllegalArgumentException("all names passed as arguments must be internedwhen NAMES INTERNED feature is enabled");
      }
   }

   protected void reset() {
      this.location = null;
      this.out = null;
      this.autoDeclaredPrefixes = 0;
      this.depth = 0;

      for (int var1 = 0; var1 < this.elNamespaceCount.length; var1++) {
         this.elName[var1] = null;
         this.elNamespace[var1] = null;
         this.elNamespaceCount[var1] = 2;
      }

      this.namespaceEnd = 0;
      this.namespacePrefix[this.namespaceEnd] = "xmlns";
      this.namespaceUri[this.namespaceEnd] = "http://www.w3.org/2000/xmlns/";
      this.namespaceEnd++;
      this.namespacePrefix[this.namespaceEnd] = "xml";
      this.namespaceUri[this.namespaceEnd] = "http://www.w3.org/XML/1998/namespace";
      this.namespaceEnd++;
      this.finished = false;
      this.pastRoot = false;
      this.setPrefixCalled = false;
      this.startTagIncomplete = false;
      this.seenTag = false;
      this.seenBracket = false;
      this.seenBracketBracket = false;
   }

   protected void ensureElementsCapacity() {
      int var1 = this.elName.length;
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
         System.arraycopy(this.elNamespace, 0, var4, 0, var1);
      }

      this.elNamespace = (String[])var4;
      int[] var5 = new int[var2];
      if (var3) {
         System.arraycopy(this.elNamespaceCount, 0, var5, 0, var1);
      } else {
         var5[0] = 0;
      }

      this.elNamespaceCount = var5;
   }

   protected void ensureNamespacesCapacity() {
      int var1 = this.namespaceEnd > 7 ? 2 * this.namespaceEnd : 8;
      String[] var2 = new String[var1];
      String[] var3 = new String[var1];
      if (this.namespacePrefix != null) {
         System.arraycopy(this.namespacePrefix, 0, var2, 0, this.namespaceEnd);
         System.arraycopy(this.namespaceUri, 0, var3, 0, this.namespaceEnd);
      }

      this.namespacePrefix = var2;
      this.namespaceUri = var3;
   }

   @Override
   public void setFeature(String var1, boolean var2) throws IllegalArgumentException, IllegalStateException {
      if (var1 == null) {
         throw new IllegalArgumentException("feature name can not be null");
      } else {
         if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(var1)) {
            this.namesInterned = var2;
         } else {
            if (!"http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe".equals(var1)) {
               throw new IllegalStateException("unsupported feature " + var1);
            }

            this.attributeUseApostrophe = var2;
         }
      }
   }

   @Override
   public boolean getFeature(String var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("feature name can not be null");
      } else if ("http://xmlpull.org/v1/doc/features.html#names-interned".equals(var1)) {
         return this.namesInterned;
      } else {
         return "http://xmlpull.org/v1/doc/features.html#serializer-attvalue-use-apostrophe".equals(var1) ? this.attributeUseApostrophe : false;
      }
   }

   protected void rebuildIndentationBuf() {
      if (this.doIndent) {
         byte var1 = 65;
         int var2 = 0;
         this.offsetNewLine = 0;
         if (this.writeLineSeparator) {
            this.offsetNewLine = this.lineSeparator.length();
            var2 += this.offsetNewLine;
         }

         this.maxIndentLevel = 0;
         if (this.writeIndentation) {
            this.indentationJump = this.indentationString.length();
            this.maxIndentLevel = 65 / this.indentationJump;
            var2 += this.maxIndentLevel * this.indentationJump;
         }

         if (this.indentationBuf == null || this.indentationBuf.length < var2) {
            this.indentationBuf = new char[var2 + 8];
         }

         int var3 = 0;
         if (this.writeLineSeparator) {
            for (int var4 = 0; var4 < this.lineSeparator.length(); var4++) {
               this.indentationBuf[var3++] = this.lineSeparator.charAt(var4);
            }
         }

         if (this.writeIndentation) {
            for (int var6 = 0; var6 < this.maxIndentLevel; var6++) {
               for (int var5 = 0; var5 < this.indentationString.length(); var5++) {
                  this.indentationBuf[var3++] = this.indentationString.charAt(var5);
               }
            }
         }
      }
   }

   protected void writeIndent() throws IOException {
      int var1 = this.writeLineSeparator ? 0 : this.offsetNewLine;
      int var2 = this.depth > this.maxIndentLevel ? this.maxIndentLevel : this.depth;
      this.out.write(this.indentationBuf, var1, var2 * this.indentationJump + this.offsetNewLine);
   }

   @Override
   public void setProperty(String var1, Object var2) throws IllegalArgumentException, IllegalStateException {
      if (var1 == null) {
         throw new IllegalArgumentException("property name can not be null");
      } else {
         if ("http://xmlpull.org/v1/doc/properties.html#serializer-indentation".equals(var1)) {
            this.indentationString = (String)var2;
         } else if ("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator".equals(var1)) {
            this.lineSeparator = (String)var2;
         } else {
            if (!"http://xmlpull.org/v1/doc/properties.html#location".equals(var1)) {
               throw new IllegalStateException("unsupported property " + var1);
            }

            this.location = (String)var2;
         }

         this.writeLineSeparator = this.lineSeparator != null && this.lineSeparator.length() > 0;
         this.writeIndentation = this.indentationString != null && this.indentationString.length() > 0;
         this.doIndent = this.indentationString != null && (this.writeLineSeparator || this.writeIndentation);
         this.rebuildIndentationBuf();
         this.seenTag = false;
      }
   }

   @Override
   public Object getProperty(String var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new IllegalArgumentException("property name can not be null");
      } else if ("http://xmlpull.org/v1/doc/properties.html#serializer-indentation".equals(var1)) {
         return this.indentationString;
      } else if ("http://xmlpull.org/v1/doc/properties.html#serializer-line-separator".equals(var1)) {
         return this.lineSeparator;
      } else {
         return "http://xmlpull.org/v1/doc/properties.html#location".equals(var1) ? this.location : null;
      }
   }

   private String getLocation() {
      return this.location != null ? " @" + this.location : "";
   }

   public Writer getWriter() {
      return this.out;
   }

   @Override
   public void setOutput(Writer var1) {
      this.reset();
      this.out = var1;
   }

   @Override
   public void setOutput(OutputStream var1, String var2) throws IOException {
      if (var1 == null) {
         throw new IllegalArgumentException("output stream can not be null");
      } else {
         this.reset();
         if (var2 != null) {
            this.out = new OutputStreamWriter(var1, var2);
         } else {
            this.out = new OutputStreamWriter(var1);
         }
      }
   }

   @Override
   public void startDocument(String var1, Boolean var2) throws IOException {
      int var3 = this.attributeUseApostrophe ? 39 : 34;
      if (this.attributeUseApostrophe) {
         this.out.write("<?xml version='1.0'");
      } else {
         this.out.write("<?xml version=\"1.0\"");
      }

      if (var1 != null) {
         this.out.write(" encoding=");
         this.out.write(this.attributeUseApostrophe ? 39 : 34);
         this.out.write(var1);
         this.out.write(this.attributeUseApostrophe ? 39 : 34);
      }

      if (var2 != null) {
         this.out.write(" standalone=");
         this.out.write(this.attributeUseApostrophe ? 39 : 34);
         if (var2) {
            this.out.write("yes");
         } else {
            this.out.write("no");
         }

         this.out.write(this.attributeUseApostrophe ? 39 : 34);
      }

      this.out.write("?>");
      if (this.writeLineSeparator) {
         this.out.write(this.lineSeparator);
      }
   }

   @Override
   public void endDocument() throws IOException {
      while (this.depth > 0) {
         this.endTag(this.elNamespace[this.depth], this.elName[this.depth]);
      }

      if (this.writeLineSeparator) {
         this.out.write(this.lineSeparator);
      }

      this.finished = this.pastRoot = this.startTagIncomplete = true;
      this.out.flush();
   }

   @Override
   public void setPrefix(String var1, String var2) throws IOException {
      if (this.startTagIncomplete) {
         this.closeStartTag();
      }

      if (var1 == null) {
         var1 = "";
      }

      if (!this.namesInterned) {
         var1 = var1.intern();
      } else if (this.checkNamesInterned) {
         this.checkInterning(var1);
      } else if (var1 == null) {
         throw new IllegalArgumentException("prefix must be not null" + this.getLocation());
      }

      for (int var3 = this.elNamespaceCount[this.depth]; var3 < this.namespaceEnd; var3++) {
         if (var1 == this.namespacePrefix[var3]) {
            throw new IllegalStateException("duplicated prefix " + printable(var1) + this.getLocation());
         }
      }

      if (!this.namesInterned) {
         var2 = var2.intern();
      } else if (this.checkNamesInterned) {
         this.checkInterning(var2);
      } else if (var2 == null) {
         throw new IllegalArgumentException("namespace must be not null" + this.getLocation());
      }

      if (this.namespaceEnd >= this.namespacePrefix.length) {
         this.ensureNamespacesCapacity();
      }

      this.namespacePrefix[this.namespaceEnd] = var1;
      this.namespaceUri[this.namespaceEnd] = var2;
      this.namespaceEnd++;
      this.setPrefixCalled = true;
   }

   protected String lookupOrDeclarePrefix(String var1) {
      return this.getPrefix(var1, true);
   }

   @Override
   public String getPrefix(String var1, boolean var2) {
      if (!this.namesInterned) {
         var1 = var1.intern();
      } else if (this.checkNamesInterned) {
         this.checkInterning(var1);
      }

      if (var1 == null) {
         throw new IllegalArgumentException("namespace must be not null" + this.getLocation());
      } else if (var1.length() == 0) {
         throw new IllegalArgumentException("default namespace cannot have prefix" + this.getLocation());
      } else {
         for (int var3 = this.namespaceEnd - 1; var3 >= 0; var3--) {
            if (var1 == this.namespaceUri[var3]) {
               String var4 = this.namespacePrefix[var3];

               for (int var5 = this.namespaceEnd - 1; var5 > var3; var5--) {
                  if (var4 == this.namespacePrefix[var5]) {
                  }
               }

               return var4;
            }
         }

         return !var2 ? null : this.generatePrefix(var1);
      }
   }

   private String generatePrefix(String var1) {
      this.autoDeclaredPrefixes++;
      String var2 = this.autoDeclaredPrefixes < precomputedPrefixes.length
         ? precomputedPrefixes[this.autoDeclaredPrefixes]
         : ("n" + this.autoDeclaredPrefixes).intern();

      for (int var3 = this.namespaceEnd - 1; var3 >= 0; var3--) {
         if (var2 == this.namespacePrefix[var3]) {
         }
      }

      if (this.namespaceEnd >= this.namespacePrefix.length) {
         this.ensureNamespacesCapacity();
      }

      this.namespacePrefix[this.namespaceEnd] = var2;
      this.namespaceUri[this.namespaceEnd] = var1;
      this.namespaceEnd++;
      return var2;
   }

   @Override
   public int getDepth() {
      return this.depth;
   }

   @Override
   public String getNamespace() {
      return this.elNamespace[this.depth];
   }

   @Override
   public String getName() {
      return this.elName[this.depth];
   }

   @Override
   public XmlSerializer startTag(String var1, String var2) throws IOException {
      if (this.startTagIncomplete) {
         this.closeStartTag();
      }

      this.seenBracket = this.seenBracketBracket = false;
      if (this.doIndent && this.depth > 0 && this.seenTag) {
         this.writeIndent();
      }

      this.seenTag = true;
      this.setPrefixCalled = false;
      this.startTagIncomplete = true;
      this.depth++;
      if (this.depth + 1 >= this.elName.length) {
         this.ensureElementsCapacity();
      }

      if (this.checkNamesInterned && this.namesInterned) {
         this.checkInterning(var1);
      }

      this.elNamespace[this.depth] = !this.namesInterned && var1 != null ? var1.intern() : var1;
      if (this.checkNamesInterned && this.namesInterned) {
         this.checkInterning(var2);
      }

      this.elName[this.depth] = !this.namesInterned && var2 != null ? var2.intern() : var2;
      if (this.out == null) {
         throw new IllegalStateException("setOutput() must called set before serialization can start");
      } else {
         this.out.write(60);
         if (var1 != null) {
            if (var1.length() > 0) {
               String var3 = null;
               if (this.depth > 0 && this.namespaceEnd - this.elNamespaceCount[this.depth - 1] == 1) {
                  String var4 = this.namespaceUri[this.namespaceEnd - 1];
                  if (var4 == var1 || var4.equals(var1)) {
                     String var5 = this.namespacePrefix[this.namespaceEnd - 1];

                     for (int var6 = this.elNamespaceCount[this.depth - 1] - 1; var6 >= 2; var6--) {
                        String var7 = this.namespacePrefix[var6];
                        if (var7 == var5 || var7.equals(var5)) {
                           String var8 = this.namespaceUri[var6];
                           if (var8 == var4 || var8.equals(var4)) {
                              this.namespaceEnd--;
                              var3 = var5;
                           }
                           break;
                        }
                     }
                  }
               }

               if (var3 == null) {
                  var3 = this.lookupOrDeclarePrefix(var1);
               }

               if (var3.length() > 0) {
                  this.out.write(var3);
                  this.out.write(58);
               }
            } else {
               for (int var9 = this.namespaceEnd - 1; var9 >= 0; var9--) {
                  if (this.namespacePrefix[var9] == "") {
                     String var10 = this.namespaceUri[var9];
                     if (var10 == null) {
                        this.setPrefix("", "");
                     } else if (var10.length() > 0) {
                        throw new IllegalStateException(
                           "start tag can not be written in empty default namespace as default namespace is currently bound to '"
                              + var10
                              + "'"
                              + this.getLocation()
                        );
                     }
                     break;
                  }
               }
            }
         }

         this.out.write(var2);
         return this;
      }
   }

   @Override
   public XmlSerializer attribute(String var1, String var2, String var3) throws IOException {
      if (!this.startTagIncomplete) {
         throw new IllegalArgumentException("startTag() must be called before attribute()" + this.getLocation());
      } else {
         this.out.write(32);
         if (var1 != null && var1.length() > 0) {
            if (!this.namesInterned) {
               var1 = var1.intern();
            } else if (this.checkNamesInterned) {
               this.checkInterning(var1);
            }

            String var4 = this.lookupOrDeclarePrefix(var1);
            if (var4.length() == 0) {
               var4 = this.generatePrefix(var1);
            }

            this.out.write(var4);
            this.out.write(58);
         }

         this.out.write(var2);
         this.out.write(61);
         this.out.write(this.attributeUseApostrophe ? 39 : 34);
         this.writeAttributeValue(var3, this.out);
         this.out.write(this.attributeUseApostrophe ? 39 : 34);
         return this;
      }
   }

   protected void closeStartTag() throws IOException {
      if (this.finished) {
         throw new IllegalArgumentException("trying to write past already finished output" + this.getLocation());
      } else {
         if (this.seenBracket) {
            this.seenBracket = this.seenBracketBracket = false;
         }

         if (this.startTagIncomplete || this.setPrefixCalled) {
            if (this.setPrefixCalled) {
               throw new IllegalArgumentException("startTag() must be called immediately after setPrefix()" + this.getLocation());
            }

            if (!this.startTagIncomplete) {
               throw new IllegalArgumentException("trying to close start tag that is not opened" + this.getLocation());
            }

            this.writeNamespaceDeclarations();
            this.out.write(62);
            this.elNamespaceCount[this.depth] = this.namespaceEnd;
            this.startTagIncomplete = false;
         }
      }
   }

   private void writeNamespaceDeclarations() throws IOException {
      for (int var1 = this.elNamespaceCount[this.depth - 1]; var1 < this.namespaceEnd; var1++) {
         if (this.doIndent && this.namespaceUri[var1].length() > 40) {
            this.writeIndent();
            this.out.write(" ");
         }

         if (this.namespacePrefix[var1] != "") {
            this.out.write(" xmlns:");
            this.out.write(this.namespacePrefix[var1]);
            this.out.write(61);
         } else {
            this.out.write(" xmlns=");
         }

         this.out.write(this.attributeUseApostrophe ? 39 : 34);
         this.writeAttributeValue(this.namespaceUri[var1], this.out);
         this.out.write(this.attributeUseApostrophe ? 39 : 34);
      }
   }

   @Override
   public XmlSerializer endTag(String var1, String var2) throws IOException {
      this.seenBracket = this.seenBracketBracket = false;
      if (var1 != null) {
         if (!this.namesInterned) {
            var1 = var1.intern();
         } else if (this.checkNamesInterned) {
            this.checkInterning(var1);
         }
      }

      if (var1 != this.elNamespace[this.depth]) {
         throw new IllegalArgumentException(
            "expected namespace " + printable(this.elNamespace[this.depth]) + " and not " + printable(var1) + this.getLocation()
         );
      } else if (var2 == null) {
         throw new IllegalArgumentException("end tag name can not be null" + this.getLocation());
      } else {
         if (this.checkNamesInterned && this.namesInterned) {
            this.checkInterning(var2);
         }

         if ((this.namesInterned || var2.equals(this.elName[this.depth])) && (!this.namesInterned || var2 == this.elName[this.depth])) {
            if (this.startTagIncomplete) {
               this.writeNamespaceDeclarations();
               this.out.write(" />");
               this.depth--;
            } else {
               this.depth--;
               if (this.doIndent && this.seenTag) {
                  this.writeIndent();
               }

               this.out.write("</");
               if (var1 != null && var1.length() > 0) {
                  String var3 = this.lookupOrDeclarePrefix(var1);
                  if (var3.length() > 0) {
                     this.out.write(var3);
                     this.out.write(58);
                  }
               }

               this.out.write(var2);
               this.out.write(62);
            }

            this.namespaceEnd = this.elNamespaceCount[this.depth];
            this.startTagIncomplete = false;
            this.seenTag = true;
            return this;
         } else {
            throw new IllegalArgumentException(
               "expected element name " + printable(this.elName[this.depth]) + " and not " + printable(var2) + this.getLocation()
            );
         }
      }
   }

   @Override
   public XmlSerializer text(String var1) throws IOException {
      if (this.startTagIncomplete || this.setPrefixCalled) {
         this.closeStartTag();
      }

      if (this.doIndent && this.seenTag) {
         this.seenTag = false;
      }

      this.writeElementContent(var1, this.out);
      return this;
   }

   @Override
   public XmlSerializer text(char[] var1, int var2, int var3) throws IOException {
      if (this.startTagIncomplete || this.setPrefixCalled) {
         this.closeStartTag();
      }

      if (this.doIndent && this.seenTag) {
         this.seenTag = false;
      }

      this.writeElementContent(var1, var2, var3, this.out);
      return this;
   }

   @Override
   public void cdsect(String var1) throws IOException {
      if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket) {
         this.closeStartTag();
      }

      if (this.doIndent && this.seenTag) {
         this.seenTag = false;
      }

      this.out.write("<![CDATA[");
      this.out.write(var1);
      this.out.write("]]>");
   }

   @Override
   public void entityRef(String var1) throws IOException {
      if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket) {
         this.closeStartTag();
      }

      if (this.doIndent && this.seenTag) {
         this.seenTag = false;
      }

      this.out.write(38);
      this.out.write(var1);
      this.out.write(59);
   }

   @Override
   public void processingInstruction(String var1) throws IOException {
      if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket) {
         this.closeStartTag();
      }

      if (this.doIndent && this.seenTag) {
         this.seenTag = false;
      }

      this.out.write("<?");
      this.out.write(var1);
      this.out.write("?>");
   }

   @Override
   public void comment(String var1) throws IOException {
      if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket) {
         this.closeStartTag();
      }

      if (this.doIndent && this.seenTag) {
         this.seenTag = false;
      }

      this.out.write("<!--");
      this.out.write(var1);
      this.out.write("-->");
   }

   @Override
   public void docdecl(String var1) throws IOException {
      if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket) {
         this.closeStartTag();
      }

      if (this.doIndent && this.seenTag) {
         this.seenTag = false;
      }

      this.out.write("<!DOCTYPE ");
      this.out.write(var1);
      this.out.write(">");
   }

   @Override
   public void ignorableWhitespace(String var1) throws IOException {
      if (this.startTagIncomplete || this.setPrefixCalled || this.seenBracket) {
         this.closeStartTag();
      }

      if (this.doIndent && this.seenTag) {
         this.seenTag = false;
      }

      if (var1.length() == 0) {
         throw new IllegalArgumentException("empty string is not allowed for ignorable whitespace" + this.getLocation());
      } else {
         this.out.write(var1);
      }
   }

   @Override
   public void flush() throws IOException {
      if (!this.finished && this.startTagIncomplete) {
         this.closeStartTag();
      }

      this.out.flush();
   }

   protected void writeAttributeValue(String var1, Writer var2) throws IOException {
      int var3 = this.attributeUseApostrophe ? 39 : 34;
      String var4 = this.attributeUseApostrophe ? "&apos;" : "&quot;";
      int var5 = 0;

      for (int var6 = 0; var6 < var1.length(); var6++) {
         char var7 = var1.charAt(var6);
         if (var7 == '&') {
            if (var6 > var5) {
               var2.write(var1.substring(var5, var6));
            }

            var2.write("&amp;");
            var5 = var6 + 1;
         }

         if (var7 == '<') {
            if (var6 > var5) {
               var2.write(var1.substring(var5, var6));
            }

            var2.write("&lt;");
            var5 = var6 + 1;
         } else if (var7 == var3) {
            if (var6 > var5) {
               var2.write(var1.substring(var5, var6));
            }

            var2.write(var4);
            var5 = var6 + 1;
         } else if (var7 < ' ') {
            if (var7 != '\r' && var7 != '\n' && var7 != '\t') {
               throw new IllegalStateException("character " + Integer.toString(var7) + " is not allowed in output" + this.getLocation());
            }

            if (var6 > var5) {
               var2.write(var1.substring(var5, var6));
            }

            var2.write("&#");
            var2.write(Integer.toString(var7));
            var2.write(59);
            var5 = var6 + 1;
         }
      }

      if (var5 > 0) {
         var2.write(var1.substring(var5));
      } else {
         var2.write(var1);
      }
   }

   protected void writeElementContent(String var1, Writer var2) throws IOException {
      int var3 = 0;

      for (int var4 = 0; var4 < var1.length(); var4++) {
         char var5 = var1.charAt(var4);
         if (var5 == ']') {
            if (this.seenBracket) {
               this.seenBracketBracket = true;
            } else {
               this.seenBracket = true;
            }
         } else {
            if (var5 == '&') {
               if (var4 > var3) {
                  var2.write(var1.substring(var3, var4));
               }

               var2.write("&amp;");
               var3 = var4 + 1;
            } else if (var5 == '<') {
               if (var4 > var3) {
                  var2.write(var1.substring(var3, var4));
               }

               var2.write("&lt;");
               var3 = var4 + 1;
            } else if (this.seenBracketBracket && var5 == '>') {
               if (var4 > var3) {
                  var2.write(var1.substring(var3, var4));
               }

               var2.write("&gt;");
               var3 = var4 + 1;
            } else if (var5 < ' ' && var5 != '\t' && var5 != '\n' && var5 != '\r') {
               throw new IllegalStateException("character " + Integer.toString(var5) + " is not allowed in output" + this.getLocation());
            }

            if (this.seenBracket) {
               this.seenBracketBracket = this.seenBracket = false;
            }
         }
      }

      if (var3 > 0) {
         var2.write(var1.substring(var3));
      } else {
         var2.write(var1);
      }
   }

   protected void writeElementContent(char[] var1, int var2, int var3, Writer var4) throws IOException {
      int var5 = var2 + var3;
      int var6 = var2;

      for (int var7 = var2; var7 < var5; var7++) {
         char var8 = var1[var7];
         if (var8 == ']') {
            if (this.seenBracket) {
               this.seenBracketBracket = true;
            } else {
               this.seenBracket = true;
            }
         } else {
            if (var8 == '&') {
               if (var7 > var6) {
                  var4.write(var1, var6, var7 - var6);
               }

               var4.write("&amp;");
               var6 = var7 + 1;
            } else if (var8 == '<') {
               if (var7 > var6) {
                  var4.write(var1, var6, var7 - var6);
               }

               var4.write("&lt;");
               var6 = var7 + 1;
            } else if (this.seenBracketBracket && var8 == '>') {
               if (var7 > var6) {
                  var4.write(var1, var6, var7 - var6);
               }

               var4.write("&gt;");
               var6 = var7 + 1;
            } else if (var8 < ' ' && var8 != '\t' && var8 != '\n' && var8 != '\r') {
               throw new IllegalStateException("character " + Integer.toString(var8) + " is not allowed in output" + this.getLocation());
            }

            if (this.seenBracket) {
               this.seenBracketBracket = this.seenBracket = false;
            }
         }
      }

      if (var5 > var6) {
         var4.write(var1, var6, var5 - var6);
      }
   }

   protected static final String printable(String var0) {
      if (var0 == null) {
         return "null";
      } else {
         StringBuilder var1 = new StringBuilder(var0.length() + 16);
         var1.append("'");

         for (int var3 = 0; var3 < var0.length(); var3++) {
            addPrintable(var1, var0.charAt(var3));
         }

         var1.append("'");
         return var1.toString();
      }
   }

   protected static final String printable(char var0) {
      StringBuilder var1 = new StringBuilder();
      addPrintable(var1, var0);
      return var1.toString();
   }

   private static void addPrintable(StringBuilder var0, char var1) {
      switch (var1) {
         case '\b':
            var0.append("\\b");
            break;
         case '\t':
            var0.append("\\t");
            break;
         case '\n':
            var0.append("\\n");
            break;
         case '\f':
            var0.append("\\f");
            break;
         case '\r':
            var0.append("\\r");
            break;
         case '"':
            var0.append("\\\"");
            break;
         case '\'':
            var0.append("\\'");
            break;
         case '\\':
            var0.append("\\\\");
            break;
         default:
            if (var1 >= ' ' && var1 <= '~') {
               var0.append(var1);
            } else {
               String var2 = "0000" + Integer.toString(var1, 16);
               var0.append("\\u").append(var2, var2.length() - 4, var2.length());
            }
      }
   }

   static {
      for (int var0 = 0; var0 < precomputedPrefixes.length; var0++) {
         precomputedPrefixes[var0] = ("n" + var0).intern();
      }
   }
}
