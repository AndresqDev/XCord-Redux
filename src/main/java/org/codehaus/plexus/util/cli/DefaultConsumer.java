package org.codehaus.plexus.util.cli;

import java.io.IOException;

public class DefaultConsumer implements StreamConsumer {
   @Override
   public void consumeLine(String var1) throws IOException {
      System.out.println(var1);
      if (System.out.checkError()) {
         throw new IOException(String.format("Failure printing line '%s' to stdout.", var1));
      }
   }
}
