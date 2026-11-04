package org.apache.logging.log4j.core.util;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream {
   private static final NullOutputStream INSTANCE = new NullOutputStream();
   @Deprecated
   public static final NullOutputStream NULL_OUTPUT_STREAM = INSTANCE;

   public static NullOutputStream getInstance() {
      return INSTANCE;
   }

   private NullOutputStream() {
   }

   @Override
   public void write(byte[] var1, int var2, int var3) {
   }

   @Override
   public void write(int var1) {
   }

   @Override
   public void write(byte[] var1) throws IOException {
   }
}
