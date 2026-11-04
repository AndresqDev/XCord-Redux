package org.codehaus.plexus.util.xml;

import java.io.PrintWriter;
import java.io.Writer;

public class CompactXMLWriter extends PrettyPrintXMLWriter {
   public CompactXMLWriter(PrintWriter var1) {
      super(var1);
   }

   public CompactXMLWriter(Writer var1) {
      super(var1);
   }

   @Override
   protected void endOfLine() {
   }
}
