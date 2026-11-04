package io.netty.handler.pcap;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;

final class PcapWriter implements Closeable {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PcapWriter.class);
   private final OutputStream outputStream;
   private boolean isClosed;

   PcapWriter(OutputStream var1) {
      this.outputStream = var1;
   }

   PcapWriter(OutputStream var1, ByteBuf var2) throws IOException {
      this.outputStream = var1;
      PcapHeaders.writeGlobalHeader(var2);
      var2.readBytes(var1, var2.readableBytes());
   }

   void writePacket(ByteBuf var1, ByteBuf var2) throws IOException {
      if (this.isClosed) {
         logger.debug("Pcap Write attempted on closed PcapWriter");
      }

      long var3 = System.currentTimeMillis();
      PcapHeaders.writePacketHeader(var1, (int)(var3 / 1000L), (int)(var3 % 1000L * 1000L), var2.readableBytes(), var2.readableBytes());
      var1.readBytes(this.outputStream, var1.readableBytes());
      var2.readBytes(this.outputStream, var2.readableBytes());
   }

   @Override
   public void close() throws IOException {
      if (this.isClosed) {
         logger.debug("PcapWriter is already closed");
      } else {
         this.isClosed = true;
         this.outputStream.flush();
         this.outputStream.close();
         logger.debug("PcapWriter is now closed");
      }
   }
}
