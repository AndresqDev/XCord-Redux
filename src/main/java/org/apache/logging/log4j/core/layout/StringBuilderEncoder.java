package org.apache.logging.log4j.core.layout;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Objects;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.status.StatusLogger;

public class StringBuilderEncoder implements Encoder<StringBuilder> {
   private static final int DEFAULT_BYTE_BUFFER_SIZE = 8192;
   private final ThreadLocal<Object[]> threadLocal = new ThreadLocal<>();
   private final Charset charset;
   private final int charBufferSize;
   private final int byteBufferSize;

   public StringBuilderEncoder(Charset var1) {
      this(var1, Constants.ENCODER_CHAR_BUFFER_SIZE, 8192);
   }

   public StringBuilderEncoder(Charset var1, int var2, int var3) {
      this.charBufferSize = var2;
      this.byteBufferSize = var3;
      this.charset = Objects.requireNonNull(var1, "charset");
   }

   public void encode(StringBuilder var1, ByteBufferDestination var2) {
      try {
         Object[] var3 = this.getThreadLocalState();
         CharsetEncoder var4 = (CharsetEncoder)var3[0];
         CharBuffer var5 = (CharBuffer)var3[1];
         ByteBuffer var6 = (ByteBuffer)var3[2];
         TextEncoderHelper.encodeText(var4, var5, var6, var1, var2);
      } catch (Exception var7) {
         this.logEncodeTextException(var7, var1, var2);
         TextEncoderHelper.encodeTextFallBack(this.charset, var1, var2);
      }
   }

   private Object[] getThreadLocalState() {
      Object[] var1 = this.threadLocal.get();
      if (var1 == null) {
         var1 = new Object[]{
            this.charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE),
            CharBuffer.allocate(this.charBufferSize),
            ByteBuffer.allocate(this.byteBufferSize)
         };
         this.threadLocal.set(var1);
      } else {
         ((CharsetEncoder)var1[0]).reset();
         ((CharBuffer)var1[1]).clear();
         ((ByteBuffer)var1[2]).clear();
      }

      return var1;
   }

   private void logEncodeTextException(Exception var1, StringBuilder var2, ByteBufferDestination var3) {
      StatusLogger.getLogger().error("Recovering from StringBuilderEncoder.encode('{}') error: {}", var2, var1, var1);
   }
}
