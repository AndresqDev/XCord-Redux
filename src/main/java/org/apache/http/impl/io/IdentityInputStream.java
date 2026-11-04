package org.apache.http.impl.io;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.io.BufferInfo;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.util.Args;

public class IdentityInputStream extends InputStream {
   private final SessionInputBuffer in;
   private boolean closed = false;

   public IdentityInputStream(SessionInputBuffer var1) {
      this.in = Args.notNull(var1, "Session input buffer");
   }

   @Override
   public int available() throws IOException {
      return this.in instanceof BufferInfo ? ((BufferInfo)this.in).length() : 0;
   }

   @Override
   public void close() throws IOException {
      this.closed = true;
   }

   @Override
   public int read() throws IOException {
      return this.closed ? -1 : this.in.read();
   }

   @Override
   public int read(byte[] var1, int var2, int var3) throws IOException {
      return this.closed ? -1 : this.in.read(var1, var2, var3);
   }
}
