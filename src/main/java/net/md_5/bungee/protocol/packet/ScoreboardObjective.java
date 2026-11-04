package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import java.util.Locale;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ScoreboardObjective extends MultiVersionPacketV17 {
   private String name;
   private String value;
   private ScoreboardObjective.HealthDisplay type;
   private byte action;

   public void v17Read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.name = readString(var1);
      this.value = readString(var1);
      this.action = var1.readByte();
   }

   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.name = readString(var1);
      this.action = var1.readByte();
      if (this.action == 0 || this.action == 2) {
         this.value = readString(var1);
         if (var3 >= 393) {
            this.type = ScoreboardObjective.HealthDisplay.values()[readVarInt(var1)];
         } else {
            this.type = ScoreboardObjective.HealthDisplay.fromString(readString(var1));
         }
      }
   }

   public void v17Write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.name, var1);
      writeString(this.value, var1);
      var1.writeByte(this.action);
   }

   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.name, var1);
      var1.writeByte(this.action);
      if (this.action == 0 || this.action == 2) {
         writeString(this.value, var1);
         if (var3 >= 393) {
            writeVarInt(this.type.ordinal(), var1);
         } else {
            writeString(this.type.toString(), var1);
         }
      }
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public ScoreboardObjective.HealthDisplay getType() {
      return this.type;
   }

   public byte getAction() {
      return this.action;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public void setValue(String var1) {
      this.value = var1;
   }

   public void setType(ScoreboardObjective.HealthDisplay var1) {
      this.type = var1;
   }

   public void setAction(byte var1) {
      this.action = var1;
   }

   public String toString() {
      return "ScoreboardObjective(name=" + this.getName() + ", value=" + this.getValue() + ", type=" + this.getType() + ", action=" + this.getAction() + ")";
   }

   public ScoreboardObjective() {
   }

   public ScoreboardObjective(String var1, String var2, ScoreboardObjective.HealthDisplay var3, byte var4) {
      this.name = var1;
      this.value = var2;
      this.type = var3;
      this.action = var4;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ScoreboardObjective)) {
         return false;
      } else {
         ScoreboardObjective var2 = (ScoreboardObjective)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getAction() != var2.getAction()) {
            return false;
         } else {
            String var3 = this.getName();
            String var4 = var2.getName();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getValue();
               String var6 = var2.getValue();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  ScoreboardObjective.HealthDisplay var7 = this.getType();
                  ScoreboardObjective.HealthDisplay var8 = var2.getType();
                  return var7 == null ? var8 == null : var7.equals(var8);
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
      return var1 instanceof ScoreboardObjective;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getAction();
      String var3 = this.getName();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getValue();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      ScoreboardObjective.HealthDisplay var5 = this.getType();
      return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
   }

   public static enum HealthDisplay {
      INTEGER,
      HEARTS;

      @Override
      public String toString() {
         return super.toString().toLowerCase(Locale.ROOT);
      }

      public static ScoreboardObjective.HealthDisplay fromString(String var0) {
         return valueOf(var0.toUpperCase(Locale.ROOT));
      }
   }
}
