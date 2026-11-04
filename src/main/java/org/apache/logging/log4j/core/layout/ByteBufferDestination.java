package org.apache.logging.log4j.core.layout;

import java.nio.ByteBuffer;

public interface ByteBufferDestination {
   ByteBuffer getByteBuffer();

   ByteBuffer drain(ByteBuffer var1);

   void writeBytes(ByteBuffer var1);

   void writeBytes(byte[] var1, int var2, int var3);
}
