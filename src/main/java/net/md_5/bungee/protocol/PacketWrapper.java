package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;

public class PacketWrapper {
   public final DefinedPacket packet;
   public final ByteBuf buf;
   private boolean released;

   public void trySingleRelease() {
      if (!this.released) {
         this.buf.release();
         this.released = true;
      }
   }

   public PacketWrapper(DefinedPacket var1, ByteBuf var2) {
      this.packet = var1;
      this.buf = var2;
   }

   public void setReleased(boolean var1) {
      this.released = var1;
   }
}
