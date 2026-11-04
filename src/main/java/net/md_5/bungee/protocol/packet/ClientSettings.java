package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import xcord.Log4jFilter;

public class ClientSettings extends MultiVersionPacketV17 {
   private String locale;
   private byte viewDistance;
   private int chatFlags;
   private boolean chatColours;
   private byte difficulty;
   private byte skinParts;
   private int mainHand;
   private boolean disableTextFiltering;
   private boolean allowServerListing;

   public void v17Read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.locale = Log4jFilter.a(var2, readString(var1));
      this.viewDistance = var1.readByte();
      this.chatFlags = var1.readUnsignedByte();
      this.chatColours = var1.readBoolean();
      this.skinParts = var1.readByte();
      this.difficulty = var1.readByte();
   }

   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.locale = Log4jFilter.a(var2, readString(var1, 16));
      this.viewDistance = var1.readByte();
      this.chatFlags = var3 >= 107 ? DefinedPacket.readVarInt(var1) : var1.readUnsignedByte();
      this.chatColours = var1.readBoolean();
      this.skinParts = var1.readByte();
      if (var3 >= 107) {
         this.mainHand = DefinedPacket.readVarInt(var1);
      }

      if (var3 >= 755) {
         this.disableTextFiltering = var1.readBoolean();
      }

      if (var3 >= 757) {
         this.allowServerListing = var1.readBoolean();
      }
   }

   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.locale, var1);
      var1.writeByte(this.viewDistance);
      if (var3 >= 107) {
         DefinedPacket.writeVarInt(this.chatFlags, var1);
      } else {
         var1.writeByte(this.chatFlags);
      }

      var1.writeBoolean(this.chatColours);
      var1.writeByte(this.skinParts);
      if (var3 >= 107) {
         DefinedPacket.writeVarInt(this.mainHand, var1);
      }

      if (var3 >= 755) {
         var1.writeBoolean(this.disableTextFiltering);
      }

      if (var3 >= 757) {
         var1.writeBoolean(this.allowServerListing);
      }
   }

   public void v17Write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.locale, var1);
      var1.writeByte(this.viewDistance);
      var1.writeByte(this.chatFlags);
      var1.writeBoolean(this.chatColours);
      var1.writeByte(this.skinParts);
      var1.writeByte(this.difficulty);
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getLocale() {
      return this.locale;
   }

   public byte getViewDistance() {
      return this.viewDistance;
   }

   public int getChatFlags() {
      return this.chatFlags;
   }

   public boolean isChatColours() {
      return this.chatColours;
   }

   public byte getDifficulty() {
      return this.difficulty;
   }

   public byte getSkinParts() {
      return this.skinParts;
   }

   public int getMainHand() {
      return this.mainHand;
   }

   public boolean isDisableTextFiltering() {
      return this.disableTextFiltering;
   }

   public boolean isAllowServerListing() {
      return this.allowServerListing;
   }

   public void setLocale(String var1) {
      this.locale = var1;
   }

   public void setViewDistance(byte var1) {
      this.viewDistance = var1;
   }

   public void setChatFlags(int var1) {
      this.chatFlags = var1;
   }

   public void setChatColours(boolean var1) {
      this.chatColours = var1;
   }

   public void setDifficulty(byte var1) {
      this.difficulty = var1;
   }

   public void setSkinParts(byte var1) {
      this.skinParts = var1;
   }

   public void setMainHand(int var1) {
      this.mainHand = var1;
   }

   public void setDisableTextFiltering(boolean var1) {
      this.disableTextFiltering = var1;
   }

   public void setAllowServerListing(boolean var1) {
      this.allowServerListing = var1;
   }

   public String toString() {
      return "ClientSettings(locale="
         + this.getLocale()
         + ", viewDistance="
         + this.getViewDistance()
         + ", chatFlags="
         + this.getChatFlags()
         + ", chatColours="
         + this.isChatColours()
         + ", difficulty="
         + this.getDifficulty()
         + ", skinParts="
         + this.getSkinParts()
         + ", mainHand="
         + this.getMainHand()
         + ", disableTextFiltering="
         + this.isDisableTextFiltering()
         + ", allowServerListing="
         + this.isAllowServerListing()
         + ")";
   }

   public ClientSettings() {
   }

   public ClientSettings(String var1, byte var2, int var3, boolean var4, byte var5, byte var6, int var7, boolean var8, boolean var9) {
      this.locale = var1;
      this.viewDistance = var2;
      this.chatFlags = var3;
      this.chatColours = var4;
      this.difficulty = var5;
      this.skinParts = var6;
      this.mainHand = var7;
      this.disableTextFiltering = var8;
      this.allowServerListing = var9;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ClientSettings)) {
         return false;
      } else {
         ClientSettings var2 = (ClientSettings)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getViewDistance() != var2.getViewDistance()) {
            return false;
         } else if (this.getChatFlags() != var2.getChatFlags()) {
            return false;
         } else if (this.isChatColours() != var2.isChatColours()) {
            return false;
         } else if (this.getDifficulty() != var2.getDifficulty()) {
            return false;
         } else if (this.getSkinParts() != var2.getSkinParts()) {
            return false;
         } else if (this.getMainHand() != var2.getMainHand()) {
            return false;
         } else if (this.isDisableTextFiltering() != var2.isDisableTextFiltering()) {
            return false;
         } else if (this.isAllowServerListing() != var2.isAllowServerListing()) {
            return false;
         } else {
            String var3 = this.getLocale();
            String var4 = var2.getLocale();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ClientSettings;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getViewDistance();
      var2 = var2 * 59 + this.getChatFlags();
      var2 = var2 * 59 + (this.isChatColours() ? 79 : 97);
      var2 = var2 * 59 + this.getDifficulty();
      var2 = var2 * 59 + this.getSkinParts();
      var2 = var2 * 59 + this.getMainHand();
      var2 = var2 * 59 + (this.isDisableTextFiltering() ? 79 : 97);
      var2 = var2 * 59 + (this.isAllowServerListing() ? 79 : 97);
      String var3 = this.getLocale();
      return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
   }
}
