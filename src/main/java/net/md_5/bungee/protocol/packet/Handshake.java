package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Handshake extends DefinedPacket {
   private int protocolVersion;
   private String host;
   private int port;
   private int requestedProtocol;
   public static final int EXPECTED_MAX_LENGTH = 1036;

   @Override
   public void read(ByteBuf var1) {
      this.protocolVersion = readVarInt(var1);
      this.host = readString(var1, 1036);
      this.port = var1.readUnsignedShort();
      this.requestedProtocol = readVarInt(var1);
   }

   @Override
   public void write(ByteBuf var1) {
      writeVarInt(this.protocolVersion, var1);
      writeString(this.host, var1);
      var1.writeShort(this.port);
      writeVarInt(this.requestedProtocol, var1);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   @Override
   public int expectedMaxLength(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      return 1036;
   }

   @Override
   public int expectedMinLength(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      return -1;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public int getRequestedProtocol() {
      return this.requestedProtocol;
   }

   public void setProtocolVersion(int var1) {
      this.protocolVersion = var1;
   }

   public void setHost(String var1) {
      this.host = var1;
   }

   public void setPort(int var1) {
      this.port = var1;
   }

   public void setRequestedProtocol(int var1) {
      this.requestedProtocol = var1;
   }

   @Override
   public String toString() {
      return "Handshake(protocolVersion="
         + this.getProtocolVersion()
         + ", host="
         + this.getHost()
         + ", port="
         + this.getPort()
         + ", requestedProtocol="
         + this.getRequestedProtocol()
         + ")";
   }

   public Handshake() {
   }

   public Handshake(int var1, String var2, int var3, int var4) {
      this.protocolVersion = var1;
      this.host = var2;
      this.port = var3;
      this.requestedProtocol = var4;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Handshake)) {
         return false;
      } else {
         Handshake var2 = (Handshake)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getProtocolVersion() != var2.getProtocolVersion()) {
            return false;
         } else if (this.getPort() != var2.getPort()) {
            return false;
         } else if (this.getRequestedProtocol() != var2.getRequestedProtocol()) {
            return false;
         } else {
            String var3 = this.getHost();
            String var4 = var2.getHost();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Handshake;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getProtocolVersion();
      var2 = var2 * 59 + this.getPort();
      var2 = var2 * 59 + this.getRequestedProtocol();
      String var3 = this.getHost();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
