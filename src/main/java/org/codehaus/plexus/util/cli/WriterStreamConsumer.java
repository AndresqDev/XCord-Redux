package org.codehaus.plexus.util.cli;

import java.io.PrintWriter;
import java.io.Writer;

public class WriterStreamConsumer implements StreamConsumer {
   private PrintWriter writer;

   public WriterStreamConsumer(Writer var1) {
      this.writer = new PrintWriter(var1);
   }

   @Override
   public void consumeLine(String var1) {
      this.writer.println(var1);
      this.writer.flush();
   }
}
