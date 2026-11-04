package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PlayerListHeaderFooter extends DefinedPacket {
   private String header;
   private String footer;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.header = readString(var1);
      this.footer = readString(var1);
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.header, var1);
      writeString(this.footer, var1);
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getHeader() {
      return this.header;
   }

   public String getFooter() {
      return this.footer;
   }

   public void setHeader(String var1) {
      this.header = var1;
   }

   public void setFooter(String var1) {
      this.footer = var1;
   }

   @Override
   public String toString() {
      return "PlayerListHeaderFooter(header=" + this.getHeader() + ", footer=" + this.getFooter() + ")";
   }

   public PlayerListHeaderFooter() {
   }

   public PlayerListHeaderFooter(String var1, String var2) {
      this.header = var1;
      this.footer = var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PlayerListHeaderFooter)) {
         return false;
      } else {
         PlayerListHeaderFooter var2 = (PlayerListHeaderFooter)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            String var3 = this.getHeader();
            String var4 = var2.getHeader();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getFooter();
               String var6 = var2.getFooter();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof PlayerListHeaderFooter;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      String var3 = this.getHeader();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getFooter();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
