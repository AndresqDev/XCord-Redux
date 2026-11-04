package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.ArrayDeque;
import java.util.Queue;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class LineReader {
   private final Readable readable;
   private final Reader reader;
   private final CharBuffer cbuf = CharStreams.createBuffer();
   private final char[] buf = this.cbuf.array();
   private final Queue<String> lines = new ArrayDeque<>();
   private final LineBuffer lineBuf = new LineBuffer() {
      @Override
      protected void handleLine(String var1, String var2) {
         LineReader.this.lines.add(var1);
      }
   };

   public LineReader(Readable var1) {
      this.readable = Preconditions.checkNotNull(var1);
      this.reader = var1 instanceof Reader ? (Reader)var1 : null;
   }

   @CanIgnoreReturnValue
   public String readLine() throws IOException {
      while (this.lines.peek() == null) {
         Java8Compatibility.clear(this.cbuf);
         int var1 = this.reader != null ? this.reader.read(this.buf, 0, this.buf.length) : this.readable.read(this.cbuf);
         if (var1 == -1) {
            this.lineBuf.finish();
            break;
         }

         this.lineBuf.add(this.buf, 0, var1);
      }

      return this.lines.poll();
   }
}
