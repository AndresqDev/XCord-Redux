package io.netty.buffer;

import io.netty.util.ByteProcessor;

@Deprecated
public interface ByteBufProcessor extends ByteProcessor {
   @Deprecated
   ByteBufProcessor FIND_NUL = new ByteBufProcessor() {
      @Override
      public boolean process(byte var1) throws Exception {
         return var1 != 0;
      }
   };
   @Deprecated
   ByteBufProcessor FIND_NON_NUL = new ByteBufProcessor() {
      @Override
      public boolean process(byte var1) throws Exception {
         return var1 == 0;
      }
   };
   @Deprecated
   ByteBufProcessor FIND_CR = new ByteBufProcessor() {
      @Override
      public boolean process(byte var1) throws Exception {
         return var1 != 13;
      }
   };
   @Deprecated
   ByteBufProcessor FIND_NON_CR = new ByteBufProcessor() {
      @Override
      public boolean process(byte var1) throws Exception {
         return var1 == 13;
      }
   };
   @Deprecated
   ByteBufProcessor FIND_LF = new ByteBufProcessor() {
      @Override
      public boolean process(byte var1) throws Exception {
         return var1 != 10;
      }
   };
   @Deprecated
   ByteBufProcessor FIND_NON_LF = new ByteBufProcessor() {
      @Override
      public boolean process(byte var1) throws Exception {
         return var1 == 10;
      }
   };
   @Deprecated
   ByteBufProcessor FIND_CRLF = new ByteBufProcessor() {
      @Override
      public boolean process(byte var1) throws Exception {
         return var1 != 13 && var1 != 10;
      }
   };
   @Deprecated
   ByteBufProcessor FIND_NON_CRLF = new ByteBufProcessor() {
      @Override
      public boolean process(byte var1) throws Exception {
         return var1 == 13 || var1 == 10;
      }
   };
   @Deprecated
   ByteBufProcessor FIND_LINEAR_WHITESPACE = new ByteBufProcessor() {
      @Override
      public boolean process(byte var1) throws Exception {
         return var1 != 32 && var1 != 9;
      }
   };
   @Deprecated
   ByteBufProcessor FIND_NON_LINEAR_WHITESPACE = new ByteBufProcessor() {
      @Override
      public boolean process(byte var1) throws Exception {
         return var1 == 32 || var1 == 9;
      }
   };
}
