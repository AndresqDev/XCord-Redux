package org.codehaus.plexus.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

public class StringInputStream extends InputStream {
   private StringReader in;

   public StringInputStream(String var1) {
      this.in = new StringReader(var1);
   }

   @Override
   public int read() throws IOException {
      return this.in.read();
   }

   @Override
   public void close() throws IOException {
      this.in.close();
   }

   @Override
   public synchronized void mark(int var1) {
      try {
         this.in.mark(var1);
      } catch (IOException var3) {
         throw new RuntimeException(var3.getMessage());
      }
   }

   @Override
   public synchronized void reset() throws IOException {
      this.in.reset();
   }

   @Override
   public boolean markSupported() {
      return this.in.markSupported();
   }
}
