package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ServerData extends DefinedPacket {
   private String motd;
   private String icon;
   private boolean preview;
   private boolean enforceSecure;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var1.readBoolean()) {
         this.motd = readString(var1, 262144);
      }

      if (var1.readBoolean()) {
         this.icon = readString(var1);
      }

      this.preview = var1.readBoolean();
      if (var3 >= 760) {
         this.enforceSecure = var1.readBoolean();
      }
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (this.motd != null) {
         var1.writeBoolean(true);
         writeString(this.motd, var1);
      } else {
         var1.writeBoolean(false);
      }

      if (this.icon != null) {
         var1.writeBoolean(true);
         writeString(this.icon, var1);
      } else {
         var1.writeBoolean(false);
      }

      var1.writeBoolean(this.preview);
      if (var3 >= 760) {
         var1.writeBoolean(this.enforceSecure);
      }
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getMotd() {
      return this.motd;
   }

   public String getIcon() {
      return this.icon;
   }

   public boolean isPreview() {
      return this.preview;
   }

   public boolean isEnforceSecure() {
      return this.enforceSecure;
   }

   public void setMotd(String var1) {
      this.motd = var1;
   }

   public void setIcon(String var1) {
      this.icon = var1;
   }

   public void setPreview(boolean var1) {
      this.preview = var1;
   }

   public void setEnforceSecure(boolean var1) {
      this.enforceSecure = var1;
   }

   @Override
   public String toString() {
      return "ServerData(motd="
         + this.getMotd()
         + ", icon="
         + this.getIcon()
         + ", preview="
         + this.isPreview()
         + ", enforceSecure="
         + this.isEnforceSecure()
         + ")";
   }

   public ServerData() {
   }

   public ServerData(String var1, String var2, boolean var3, boolean var4) {
      this.motd = var1;
      this.icon = var2;
      this.preview = var3;
      this.enforceSecure = var4;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ServerData)) {
         return false;
      } else {
         ServerData var2 = (ServerData)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.isPreview() != var2.isPreview()) {
            return false;
         } else if (this.isEnforceSecure() != var2.isEnforceSecure()) {
            return false;
         } else {
            String var3 = this.getMotd();
            String var4 = var2.getMotd();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getIcon();
               String var6 = var2.getIcon();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ServerData;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + (this.isPreview() ? 79 : 97);
      var2 = var2 * 59 + (this.isEnforceSecure() ? 79 : 97);
      String var3 = this.getMotd();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getIcon();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
