package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class BossBar extends DefinedPacket {
   private UUID uuid;
   private int action;
   private String title;
   private float health;
   private int color;
   private int division;
   private byte flags;

   public BossBar(UUID var1, int var2) {
      this.uuid = var1;
      this.action = var2;
   }

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.uuid = readUUID(var1);
      this.action = readVarInt(var1);
      switch (this.action) {
         case 0:
            this.title = readString(var1);
            this.health = var1.readFloat();
            this.color = readVarInt(var1);
            this.division = readVarInt(var1);
            this.flags = var1.readByte();
         case 1:
         default:
            break;
         case 2:
            this.health = var1.readFloat();
            break;
         case 3:
            this.title = readString(var1);
            break;
         case 4:
            this.color = readVarInt(var1);
            this.division = readVarInt(var1);
            break;
         case 5:
            this.flags = var1.readByte();
      }
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeUUID(this.uuid, var1);
      writeVarInt(this.action, var1);
      switch (this.action) {
         case 0:
            writeString(this.title, var1);
            var1.writeFloat(this.health);
            writeVarInt(this.color, var1);
            writeVarInt(this.division, var1);
            var1.writeByte(this.flags);
         case 1:
         default:
            break;
         case 2:
            var1.writeFloat(this.health);
            break;
         case 3:
            writeString(this.title, var1);
            break;
         case 4:
            writeVarInt(this.color, var1);
            writeVarInt(this.division, var1);
            break;
         case 5:
            var1.writeByte(this.flags);
      }
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public int getAction() {
      return this.action;
   }

   public String getTitle() {
      return this.title;
   }

   public float getHealth() {
      return this.health;
   }

   public int getColor() {
      return this.color;
   }

   public int getDivision() {
      return this.division;
   }

   public byte getFlags() {
      return this.flags;
   }

   public void setUuid(UUID var1) {
      this.uuid = var1;
   }

   public void setAction(int var1) {
      this.action = var1;
   }

   public void setTitle(String var1) {
      this.title = var1;
   }

   public void setHealth(float var1) {
      this.health = var1;
   }

   public void setColor(int var1) {
      this.color = var1;
   }

   public void setDivision(int var1) {
      this.division = var1;
   }

   public void setFlags(byte var1) {
      this.flags = var1;
   }

   @Override
   public String toString() {
      return "BossBar(uuid="
         + this.getUuid()
         + ", action="
         + this.getAction()
         + ", title="
         + this.getTitle()
         + ", health="
         + this.getHealth()
         + ", color="
         + this.getColor()
         + ", division="
         + this.getDivision()
         + ", flags="
         + this.getFlags()
         + ")";
   }

   public BossBar() {
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof BossBar)) {
         return false;
      } else {
         BossBar var2 = (BossBar)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getAction() != var2.getAction()) {
            return false;
         } else if (Float.compare(this.getHealth(), var2.getHealth()) != 0) {
            return false;
         } else if (this.getColor() != var2.getColor()) {
            return false;
         } else if (this.getDivision() != var2.getDivision()) {
            return false;
         } else if (this.getFlags() != var2.getFlags()) {
            return false;
         } else {
            UUID var3 = this.getUuid();
            UUID var4 = var2.getUuid();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getTitle();
               String var6 = var2.getTitle();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof BossBar;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getAction();
      var2 = var2 * 59 + Float.floatToIntBits(this.getHealth());
      var2 = var2 * 59 + this.getColor();
      var2 = var2 * 59 + this.getDivision();
      var2 = var2 * 59 + this.getFlags();
      UUID var3 = this.getUuid();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getTitle();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
