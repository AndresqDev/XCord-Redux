package org.codehaus.plexus.util.xml;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.plexus.util.StringUtils;

public class PrettyPrintXMLWriter implements XMLWriter {
   protected static final String LS = System.getProperty("line.separator");
   private PrintWriter writer;
   private LinkedList<String> elementStack = new LinkedList<>();
   private boolean tagInProgress;
   private int depth;
   private String lineIndenter;
   private String lineSeparator;
   private String encoding;
   private String docType;
   private boolean readyForNewLine;
   private boolean tagIsEmpty;
   private static final Pattern amp = Pattern.compile("&");
   private static final Pattern lt = Pattern.compile("<");
   private static final Pattern gt = Pattern.compile(">");
   private static final Pattern dqoute = Pattern.compile("\"");
   private static final Pattern sqoute = Pattern.compile("'");
   private static final String crlf_str = "\r\n";
   private static final Pattern crlf = Pattern.compile("\r\n");
   private static final Pattern lowers = Pattern.compile("([\u0000-\u001f])");

   public PrettyPrintXMLWriter(PrintWriter var1, String var2) {
      this(var1, var2, null, null);
   }

   public PrettyPrintXMLWriter(Writer var1, String var2) {
      this(new PrintWriter(var1), var2);
   }

   public PrettyPrintXMLWriter(PrintWriter var1) {
      this(var1, null, null);
   }

   public PrettyPrintXMLWriter(Writer var1) {
      this(new PrintWriter(var1));
   }

   public PrettyPrintXMLWriter(PrintWriter var1, String var2, String var3, String var4) {
      this(var1, var2, LS, var3, var4);
   }

   public PrettyPrintXMLWriter(Writer var1, String var2, String var3, String var4) {
      this(new PrintWriter(var1), var2, var3, var4);
   }

   public PrettyPrintXMLWriter(PrintWriter var1, String var2, String var3) {
      this(var1, "  ", var2, var3);
   }

   public PrettyPrintXMLWriter(Writer var1, String var2, String var3) {
      this(new PrintWriter(var1), var2, var3);
   }

   public PrettyPrintXMLWriter(PrintWriter var1, String var2, String var3, String var4, String var5) {
      this.setWriter(var1);
      this.setLineIndenter(var2);
      this.setLineSeparator(var3);
      this.setEncoding(var4);
      this.setDocType(var5);
      if (var5 != null || var4 != null) {
         this.writeDocumentHeaders();
      }
   }

   @Override
   public void startElement(String var1) {
      this.tagIsEmpty = false;
      this.finishTag();
      this.write("<");
      this.write(var1);
      this.elementStack.addLast(var1);
      this.tagInProgress = true;
      this.setDepth(this.getDepth() + 1);
      this.readyForNewLine = true;
      this.tagIsEmpty = true;
   }

   @Override
   public void writeText(String var1) {
      this.writeText(var1, true);
   }

   @Override
   public void writeMarkup(String var1) {
      this.writeText(var1, false);
   }

   private void writeText(String var1, boolean var2) {
      this.readyForNewLine = false;
      this.tagIsEmpty = false;
      this.finishTag();
      if (var2) {
         var1 = escapeXml(var1);
      }

      this.write(StringUtils.unifyLineSeparators(var1, this.lineSeparator));
   }

   private static String escapeXml(String var0) {
      if (var0.indexOf(38) >= 0) {
         var0 = amp.matcher(var0).replaceAll("&amp;");
      }

      if (var0.indexOf(60) >= 0) {
         var0 = lt.matcher(var0).replaceAll("&lt;");
      }

      if (var0.indexOf(62) >= 0) {
         var0 = gt.matcher(var0).replaceAll("&gt;");
      }

      if (var0.indexOf(34) >= 0) {
         var0 = dqoute.matcher(var0).replaceAll("&quot;");
      }

      if (var0.indexOf(39) >= 0) {
         var0 = sqoute.matcher(var0).replaceAll("&apos;");
      }

      return var0;
   }

   private static String escapeXmlAttribute(String var0) {
      var0 = escapeXml(var0);
      Matcher var1 = crlf.matcher(var0);
      if (var0.contains("\r\n")) {
         var0 = var1.replaceAll("&#10;");
      }

      Matcher var2 = lowers.matcher(var0);
      StringBuffer var3 = new StringBuffer();

      while (var2.find()) {
         var2 = var2.appendReplacement(var3, "&#" + Integer.toString(var2.group(1).charAt(0)) + ";");
      }

      var2.appendTail(var3);
      return var3.toString();
   }

   @Override
   public void addAttribute(String var1, String var2) {
      this.write(" ");
      this.write(var1);
      this.write("=\"");
      this.write(escapeXmlAttribute(var2));
      this.write("\"");
   }

   @Override
   public void endElement() {
      this.setDepth(this.getDepth() - 1);
      if (this.tagIsEmpty) {
         this.write("/");
         this.readyForNewLine = false;
         this.finishTag();
         this.elementStack.removeLast();
      } else {
         this.finishTag();
         this.write("</");
         this.write(this.elementStack.removeLast());
         this.write(">");
      }

      this.readyForNewLine = true;
   }

   private void write(String var1) {
      this.getWriter().write(var1);
   }

   private void finishTag() {
      if (this.tagInProgress) {
         this.write(">");
      }

      this.tagInProgress = false;
      if (this.readyForNewLine) {
         this.endOfLine();
      }

      this.readyForNewLine = false;
      this.tagIsEmpty = false;
   }

   protected String getLineIndenter() {
      return this.lineIndenter;
   }

   protected void setLineIndenter(String var1) {
      this.lineIndenter = var1;
   }

   protected String getLineSeparator() {
      return this.lineSeparator;
   }

   protected void setLineSeparator(String var1) {
      this.lineSeparator = var1;
   }

   protected void endOfLine() {
      this.write(this.getLineSeparator());

      for (int var1 = 0; var1 < this.getDepth(); var1++) {
         this.write(this.getLineIndenter());
      }
   }

   private void writeDocumentHeaders() {
      this.write("<?xml version=\"1.0\"");
      if (this.getEncoding() != null) {
         this.write(" encoding=\"" + this.getEncoding() + "\"");
      }

      this.write("?>");
      this.endOfLine();
      if (this.getDocType() != null) {
         this.write("<!DOCTYPE ");
         this.write(this.getDocType());
         this.write(">");
         this.endOfLine();
      }
   }

   protected void setWriter(PrintWriter var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("writer could not be null");
      } else {
         this.writer = var1;
      }
   }

   protected PrintWriter getWriter() {
      return this.writer;
   }

   protected void setDepth(int var1) {
      this.depth = var1;
   }

   protected int getDepth() {
      return this.depth;
   }

   protected void setEncoding(String var1) {
      this.encoding = var1;
   }

   protected String getEncoding() {
      return this.encoding;
   }

   protected void setDocType(String var1) {
      this.docType = var1;
   }

   protected String getDocType() {
      return this.docType;
   }

   protected LinkedList<String> getElementStack() {
      return this.elementStack;
   }
}
