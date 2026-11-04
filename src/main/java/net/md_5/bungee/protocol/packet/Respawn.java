package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Location;
import net.md_5.bungee.protocol.ProtocolConstants;
import se.llbit.nbt.Tag;

public class Respawn extends DefinedPacket {
   private Object dimension;
   private String worldName;
   private long seed;
   private short difficulty;
   private short gameMode;
   private short previousGameMode;
   private String levelType;
   private boolean debug;
   private boolean flat;
   private boolean copyMeta;
   private Location deathLocation;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var3 >= 735) {
         if (var3 >= 751 && var3 < 759) {
            this.dimension = readTag(var1);
         } else {
            this.dimension = readString(var1);
         }

         this.worldName = readString(var1);
      } else {
         this.dimension = var1.readInt();
      }

      if (var3 >= 573) {
         this.seed = var1.readLong();
      }

      if (var3 < 477) {
         this.difficulty = var1.readUnsignedByte();
      }

      this.gameMode = var1.readUnsignedByte();
      if (var3 >= 735) {
         this.previousGameMode = var1.readUnsignedByte();
         this.debug = var1.readBoolean();
         this.flat = var1.readBoolean();
         this.copyMeta = var1.readBoolean();
      } else {
         this.levelType = readString(var1);
      }

      if (var3 >= 759 && var1.readBoolean()) {
         this.deathLocation = new Location(readString(var1), var1.readLong());
      }
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var3 >= 735) {
         if (var3 >= 751 && var3 < 759) {
            writeTag((Tag)this.dimension, var1);
         } else {
            writeString((String)this.dimension, var1);
         }

         writeString(this.worldName, var1);
      } else {
         var1.writeInt((Integer)this.dimension);
      }

      if (var3 >= 573) {
         var1.writeLong(this.seed);
      }

      if (var3 < 477) {
         var1.writeByte(this.difficulty);
      }

      var1.writeByte(this.gameMode);
      if (var3 >= 735) {
         var1.writeByte(this.previousGameMode);
         var1.writeBoolean(this.debug);
         var1.writeBoolean(this.flat);
         var1.writeBoolean(this.copyMeta);
      } else {
         writeString(this.levelType, var1);
      }

      if (var3 >= 759) {
         if (this.deathLocation != null) {
            var1.writeBoolean(true);
            writeString(this.deathLocation.getDimension(), var1);
            var1.writeLong(this.deathLocation.getPos());
         } else {
            var1.writeBoolean(false);
         }
      }
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public Object getDimension() {
      return this.dimension;
   }

   public String getWorldName() {
      return this.worldName;
   }

   public long getSeed() {
      return this.seed;
   }

   public short getDifficulty() {
      return this.difficulty;
   }

   public short getGameMode() {
      return this.gameMode;
   }

   public short getPreviousGameMode() {
      return this.previousGameMode;
   }

   public String getLevelType() {
      return this.levelType;
   }

   public boolean isDebug() {
      return this.debug;
   }

   public boolean isFlat() {
      return this.flat;
   }

   public boolean isCopyMeta() {
      return this.copyMeta;
   }

   public Location getDeathLocation() {
      return this.deathLocation;
   }

   public void setDimension(Object var1) {
      this.dimension = var1;
   }

   public void setWorldName(String var1) {
      this.worldName = var1;
   }

   public void setSeed(long var1) {
      this.seed = var1;
   }

   public void setDifficulty(short var1) {
      this.difficulty = var1;
   }

   public void setGameMode(short var1) {
      this.gameMode = var1;
   }

   public void setPreviousGameMode(short var1) {
      this.previousGameMode = var1;
   }

   public void setLevelType(String var1) {
      this.levelType = var1;
   }

   public void setDebug(boolean var1) {
      this.debug = var1;
   }

   public void setFlat(boolean var1) {
      this.flat = var1;
   }

   public void setCopyMeta(boolean var1) {
      this.copyMeta = var1;
   }

   public void setDeathLocation(Location var1) {
      this.deathLocation = var1;
   }

   @Override
   public String toString() {
      return "Respawn(dimension="
         + this.getDimension()
         + ", worldName="
         + this.getWorldName()
         + ", seed="
         + this.getSeed()
         + ", difficulty="
         + this.getDifficulty()
         + ", gameMode="
         + this.getGameMode()
         + ", previousGameMode="
         + this.getPreviousGameMode()
         + ", levelType="
         + this.getLevelType()
         + ", debug="
         + this.isDebug()
         + ", flat="
         + this.isFlat()
         + ", copyMeta="
         + this.isCopyMeta()
         + ", deathLocation="
         + this.getDeathLocation()
         + ")";
   }

   public Respawn() {
   }

   public Respawn(
      Object var1, String var2, long var3, short var5, short var6, short var7, String var8, boolean var9, boolean var10, boolean var11, Location var12
   ) {
      this.dimension = var1;
      this.worldName = var2;
      this.seed = var3;
      this.difficulty = var5;
      this.gameMode = var6;
      this.previousGameMode = var7;
      this.levelType = var8;
      this.debug = var9;
      this.flat = var10;
      this.copyMeta = var11;
      this.deathLocation = var12;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Respawn)) {
         return false;
      } else {
         Respawn var2 = (Respawn)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getSeed() != var2.getSeed()) {
            return false;
         } else if (this.getDifficulty() != var2.getDifficulty()) {
            return false;
         } else if (this.getGameMode() != var2.getGameMode()) {
            return false;
         } else if (this.getPreviousGameMode() != var2.getPreviousGameMode()) {
            return false;
         } else if (this.isDebug() != var2.isDebug()) {
            return false;
         } else if (this.isFlat() != var2.isFlat()) {
            return false;
         } else if (this.isCopyMeta() != var2.isCopyMeta()) {
            return false;
         } else {
            Object var3 = this.getDimension();
            Object var4 = var2.getDimension();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getWorldName();
               String var6 = var2.getWorldName();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  String var7 = this.getLevelType();
                  String var8 = var2.getLevelType();
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     Location var9 = this.getDeathLocation();
                     Location var10 = var2.getDeathLocation();
                     return var9 == null ? var10 == null : var9.equals(var10);
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
      return var1 instanceof Respawn;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      long var3 = this.getSeed();
      var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
      var2 = var2 * 59 + this.getDifficulty();
      var2 = var2 * 59 + this.getGameMode();
      var2 = var2 * 59 + this.getPreviousGameMode();
      var2 = var2 * 59 + (this.isDebug() ? 79 : 97);
      var2 = var2 * 59 + (this.isFlat() ? 79 : 97);
      var2 = var2 * 59 + (this.isCopyMeta() ? 79 : 97);
      Object var5 = this.getDimension();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      String var6 = this.getWorldName();
      var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      String var7 = this.getLevelType();
      var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
      Location var8 = this.getDeathLocation();
      return var2 * 59 + (var8 == null ? 43 : var8.hashCode());
   }
}
