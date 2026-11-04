package org.codehaus.plexus.util.xml;

import java.io.PrintWriter;
import java.io.Writer;

public class Xpp3DomWriter {
   public static void write(Writer var0, Xpp3Dom var1) {
      write(new PrettyPrintXMLWriter(var0), var1);
   }

   public static void write(PrintWriter var0, Xpp3Dom var1) {
      write(new PrettyPrintXMLWriter(var0), var1);
   }

   public static void write(XMLWriter var0, Xpp3Dom var1) {
      write(var0, var1, true);
   }

   public static void write(XMLWriter var0, Xpp3Dom var1, boolean var2) {
      var0.startElement(var1.getName());
      String[] var3 = var1.getAttributeNames();

      for (String var7 : var3) {
         var0.addAttribute(var7, var1.getAttribute(var7));
      }

      Xpp3Dom[] var9 = var1.getChildren();

      for (Xpp3Dom var8 : var9) {
         write(var0, var8, var2);
      }

      String var11 = var1.getValue();
      if (var11 != null) {
         if (var2) {
            var0.writeText(var11);
         } else {
            var0.writeMarkup(var11);
         }
      }

      var0.endElement();
   }
}
