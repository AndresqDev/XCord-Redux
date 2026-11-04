package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ScoreboardScore extends MultiVersionPacketV17 {
   private String itemName;
   private byte action;
   private String scoreName;
   private int value;

   public void v17Read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.itemName = readString(var1);
      this.action = var1.readByte();
      if (this.action != 1) {
         this.scoreName = readString(var1);
         this.value = var1.readInt();
      }
   }

   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.itemName = readString(var1);
      this.action = var1.readByte();
      this.scoreName = readString(var1);
      if (this.action != 1) {
         this.value = readVarInt(var1);
      }
   }

   public void v17Write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.itemName, var1);
      var1.writeByte(this.action);
      if (this.action != 1) {
         writeString(this.scoreName, var1);
         var1.writeInt(this.value);
      }
   }

   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.itemName, var1);
      var1.writeByte(this.action);
      writeString(this.scoreName, var1);
      if (this.action != 1) {
         writeVarInt(this.value, var1);
      }
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getItemName() {
      return this.itemName;
   }

   public byte getAction() {
      return this.action;
   }

   public String getScoreName() {
      return this.scoreName;
   }

   public int getValue() {
      return this.value;
   }

   public void setItemName(String var1) {
      this.itemName = var1;
   }

   public void setAction(byte var1) {
      this.action = var1;
   }

   public void setScoreName(String var1) {
      this.scoreName = var1;
   }

   public void setValue(int var1) {
      this.value = var1;
   }

   public String toString() {
      return "ScoreboardScore(itemName="
         + this.getItemName()
         + ", action="
         + this.getAction()
         + ", scoreName="
         + this.getScoreName()
         + ", value="
         + this.getValue()
         + ")";
   }

   public ScoreboardScore() {
   }

   public ScoreboardScore(String var1, byte var2, String var3, int var4) {
      this.itemName = var1;
      this.action = var2;
      this.scoreName = var3;
      this.value = var4;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ScoreboardScore)) {
         return false;
      } else {
         ScoreboardScore var2 = (ScoreboardScore)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getAction() != var2.getAction()) {
            return false;
         } else if (this.getValue() != var2.getValue()) {
            return false;
         } else {
            String var3 = this.getItemName();
            String var4 = var2.getItemName();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getScoreName();
               String var6 = var2.getScoreName();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ScoreboardScore;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getAction();
      var2 = var2 * 59 + this.getValue();
      String var3 = this.getItemName();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getScoreName();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
