package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Team extends MultiVersionPacketV17 {
   private String name;
   private byte mode;
   private String displayName;
   private String prefix;
   private String suffix;
   private String nameTagVisibility;
   private String collisionRule;
   private int color;
   private byte friendlyFire;
   private String[] players;

   public Team(String var1) {
      this.name = var1;
      this.mode = 1;
   }

   public void v17Read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.name = readString(var1);
      this.mode = var1.readByte();
      if (this.mode == 0 || this.mode == 2) {
         this.displayName = readString(var1);
         this.prefix = readString(var1);
         this.suffix = readString(var1);
         this.friendlyFire = var1.readByte();
      }

      if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
         short var4 = var1.readShort();
         this.players = new String[var4];

         for (int var5 = 0; var5 < var4; var5++) {
            this.players[var5] = readString(var1);
         }
      }
   }

   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.name = readString(var1);
      this.mode = var1.readByte();
      if (this.mode == 0 || this.mode == 2) {
         this.displayName = readString(var1);
         if (var3 < 393) {
            this.prefix = readString(var1);
            this.suffix = readString(var1);
         }

         this.friendlyFire = var1.readByte();
         this.nameTagVisibility = readString(var1);
         if (var3 >= 107) {
            this.collisionRule = readString(var1);
         }

         this.color = var3 >= 393 ? readVarInt(var1) : var1.readByte();
         if (var3 >= 393) {
            this.prefix = readString(var1);
            this.suffix = readString(var1);
         }
      }

      if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
         int var4 = readVarInt(var1);
         this.players = new String[var4];

         for (int var5 = 0; var5 < var4; var5++) {
            this.players[var5] = readString(var1);
         }
      }
   }

   public void v17Write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.name, var1);
      var1.writeByte(this.mode);
      if (this.mode == 0 || this.mode == 2) {
         writeString(this.displayName, var1);
         writeString(this.prefix, var1);
         writeString(this.suffix, var1);
         var1.writeByte(this.friendlyFire);
      }

      if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
         var1.writeShort(this.players.length);

         for (String var7 : this.players) {
            writeString(var7, var1);
         }
      }
   }

   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.name, var1);
      var1.writeByte(this.mode);
      if (this.mode == 0 || this.mode == 2) {
         writeString(this.displayName, var1);
         if (var3 < 393) {
            writeString(this.prefix, var1);
            writeString(this.suffix, var1);
         }

         var1.writeByte(this.friendlyFire);
         writeString(this.nameTagVisibility, var1);
         if (var3 >= 107) {
            writeString(this.collisionRule, var1);
         }

         if (var3 >= 393) {
            writeVarInt(this.color, var1);
            writeString(this.prefix, var1);
            writeString(this.suffix, var1);
         } else {
            var1.writeByte(this.color);
         }
      }

      if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
         writeVarInt(this.players.length, var1);

         for (String var7 : this.players) {
            writeString(var7, var1);
         }
      }
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getName() {
      return this.name;
   }

   public byte getMode() {
      return this.mode;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public String getSuffix() {
      return this.suffix;
   }

   public String getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   public String getCollisionRule() {
      return this.collisionRule;
   }

   public int getColor() {
      return this.color;
   }

   public byte getFriendlyFire() {
      return this.friendlyFire;
   }

   public String[] getPlayers() {
      return this.players;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setMode(byte var1) {
      this.mode = var1;
   }

   public void setDisplayName(String var1) {
      this.displayName = var1;
   }

   public void setPrefix(String var1) {
      this.prefix = var1;
   }

   public void setSuffix(String var1) {
      this.suffix = var1;
   }

   public void setNameTagVisibility(String var1) {
      this.nameTagVisibility = var1;
   }

   public void setCollisionRule(String var1) {
      this.collisionRule = var1;
   }

   public void setColor(int var1) {
      this.color = var1;
   }

   public void setFriendlyFire(byte var1) {
      this.friendlyFire = var1;
   }

   public void setPlayers(String[] var1) {
      this.players = var1;
   }

   public String toString() {
      return "Team(name="
         + this.getName()
         + ", mode="
         + this.getMode()
         + ", displayName="
         + this.getDisplayName()
         + ", prefix="
         + this.getPrefix()
         + ", suffix="
         + this.getSuffix()
         + ", nameTagVisibility="
         + this.getNameTagVisibility()
         + ", collisionRule="
         + this.getCollisionRule()
         + ", color="
         + this.getColor()
         + ", friendlyFire="
         + this.getFriendlyFire()
         + ", players="
         + Arrays.deepToString(this.getPlayers())
         + ")";
   }

   public Team() {
   }

   public Team(String var1, byte var2, String var3, String var4, String var5, String var6, String var7, int var8, byte var9, String[] var10) {
      this.name = var1;
      this.mode = var2;
      this.displayName = var3;
      this.prefix = var4;
      this.suffix = var5;
      this.nameTagVisibility = var6;
      this.collisionRule = var7;
      this.color = var8;
      this.friendlyFire = var9;
      this.players = var10;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Team)) {
         return false;
      } else {
         Team var2 = (Team)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getMode() != var2.getMode()) {
            return false;
         } else if (this.getColor() != var2.getColor()) {
            return false;
         } else if (this.getFriendlyFire() != var2.getFriendlyFire()) {
            return false;
         } else {
            String var3 = this.getName();
            String var4 = var2.getName();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getDisplayName();
               String var6 = var2.getDisplayName();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  String var7 = this.getPrefix();
                  String var8 = var2.getPrefix();
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     String var9 = this.getSuffix();
                     String var10 = var2.getSuffix();
                     if (var9 == null ? var10 == null : var9.equals(var10)) {
                        String var11 = this.getNameTagVisibility();
                        String var12 = var2.getNameTagVisibility();
                        if (var11 == null ? var12 == null : var11.equals(var12)) {
                           String var13 = this.getCollisionRule();
                           String var14 = var2.getCollisionRule();
                           return (var13 == null ? var14 == null : var13.equals(var14)) ? Arrays.deepEquals(this.getPlayers(), var2.getPlayers()) : false;
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Team;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getMode();
      var2 = var2 * 59 + this.getColor();
      var2 = var2 * 59 + this.getFriendlyFire();
      String var3 = this.getName();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getDisplayName();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      String var5 = this.getPrefix();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      String var6 = this.getSuffix();
      var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      String var7 = this.getNameTagVisibility();
      var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
      String var8 = this.getCollisionRule();
      var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
      return var2 * 59 + Arrays.deepHashCode(this.getPlayers());
   }
}
