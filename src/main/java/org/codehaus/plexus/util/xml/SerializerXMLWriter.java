package org.codehaus.plexus.util.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.codehaus.plexus.util.xml.pull.XmlSerializer;

public class SerializerXMLWriter implements XMLWriter {
   private final XmlSerializer serializer;
   private final String namespace;
   private final Stack<String> elements = new Stack<>();
   private List<Exception> exceptions;

   public SerializerXMLWriter(String var1, XmlSerializer var2) {
      this.serializer = var2;
      this.namespace = var1;
   }

   @Override
   public void startElement(String var1) {
      try {
         this.serializer.startTag(this.namespace, var1);
         this.elements.push(var1);
      } catch (IOException var3) {
         this.storeException(var3);
      }
   }

   @Override
   public void addAttribute(String var1, String var2) {
      try {
         this.serializer.attribute(this.namespace, var1, var2);
      } catch (IOException var4) {
         this.storeException(var4);
      }
   }

   @Override
   public void writeText(String var1) {
      try {
         this.serializer.text(var1);
      } catch (IOException var3) {
         this.storeException(var3);
      }
   }

   @Override
   public void writeMarkup(String var1) {
      try {
         this.serializer.cdsect(var1);
      } catch (IOException var3) {
         this.storeException(var3);
      }
   }

   @Override
   public void endElement() {
      try {
         this.serializer.endTag(this.namespace, this.elements.pop());
      } catch (IOException var2) {
         this.storeException(var2);
      }
   }

   private void storeException(IOException var1) {
      if (this.exceptions == null) {
         this.exceptions = new ArrayList<>();
      }

      this.exceptions.add(var1);
   }

   public List<Exception> getExceptions() {
      return this.exceptions == null ? Collections.emptyList() : this.exceptions;
   }
}
