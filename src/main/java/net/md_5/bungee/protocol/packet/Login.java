package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.HashSet;
import java.util.Set;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Location;
import net.md_5.bungee.protocol.ProtocolConstants;
import se.llbit.nbt.Tag;

public class Login extends DefinedPacket {
   private int entityId;
   private boolean hardcore;
   private short gameMode;
   private short previousGameMode;
   private Set<String> worldNames;
   private Tag dimensions;
   private Object dimension;
   private String worldName;
   private long seed;
   private short difficulty;
   private int maxPlayers;
   private String levelType;
   private int viewDistance;
   private int simulationDistance;
   private boolean reducedDebugInfo;
   private boolean normalRespawn;
   private boolean debug;
   private boolean flat;
   private Location deathLocation;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.entityId = var1.readInt();
      if (var3 >= 751) {
         this.hardcore = var1.readBoolean();
      }

      this.gameMode = var1.readUnsignedByte();
      if (var3 >= 735) {
         this.previousGameMode = var1.readUnsignedByte();
         this.worldNames = new HashSet<>();
         int var4 = readVarInt(var1);

         for (int var5 = 0; var5 < var4; var5++) {
            this.worldNames.add(readString(var1));
         }

         this.dimensions = readTag(var1);
      }

      if (var3 >= 735) {
         if (var3 >= 751 && var3 < 759) {
            this.dimension = readTag(var1);
         } else {
            this.dimension = readString(var1);
         }

         this.worldName = readString(var1);
      } else if (var3 > 107) {
         this.dimension = var1.readInt();
      } else {
         this.dimension = Integer.valueOf(var1.readByte());
      }

      if (var3 >= 573) {
         this.seed = var1.readLong();
      }

      if (var3 < 477) {
         this.difficulty = var1.readUnsignedByte();
      }

      if (var3 >= 751) {
         this.maxPlayers = readVarInt(var1);
      } else {
         this.maxPlayers = var1.readUnsignedByte();
      }

      if (var3 < 735) {
         this.levelType = readString(var1);
      }

      if (var3 >= 477) {
         this.viewDistance = readVarInt(var1);
      }

      if (var3 >= 757) {
         this.simulationDistance = readVarInt(var1);
      }

      if (var3 >= 29) {
         this.reducedDebugInfo = var1.readBoolean();
      }

      if (var3 >= 573) {
         this.normalRespawn = var1.readBoolean();
      }

      if (var3 >= 735) {
         this.debug = var1.readBoolean();
         this.flat = var1.readBoolean();
      }

      if (var3 >= 759 && var1.readBoolean()) {
         this.deathLocation = new Location(readString(var1), var1.readLong());
      }
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      var1.writeInt(this.entityId);
      if (var3 >= 751) {
         var1.writeBoolean(this.hardcore);
      }

      var1.writeByte(this.gameMode);
      if (var3 >= 735) {
         var1.writeByte(this.previousGameMode);
         writeVarInt(this.worldNames.size(), var1);

         for (String var5 : this.worldNames) {
            writeString(var5, var1);
         }

         writeTag(this.dimensions, var1);
      }

      if (var3 >= 735) {
         if (var3 >= 751 && var3 < 759) {
            writeTag((Tag)this.dimension, var1);
         } else {
            writeString((String)this.dimension, var1);
         }

         writeString(this.worldName, var1);
      } else if (var3 > 107) {
         var1.writeInt((Integer)this.dimension);
      } else {
         var1.writeByte((Integer)this.dimension);
      }

      if (var3 >= 573) {
         var1.writeLong(this.seed);
      }

      if (var3 < 477) {
         var1.writeByte(this.difficulty);
      }

      if (var3 >= 751) {
         writeVarInt(this.maxPlayers, var1);
      } else {
         var1.writeByte(this.maxPlayers);
      }

      if (var3 < 735) {
         writeString(this.levelType, var1);
      }

      if (var3 >= 477) {
         writeVarInt(this.viewDistance, var1);
      }

      if (var3 >= 757) {
         writeVarInt(this.simulationDistance, var1);
      }

      if (var3 >= 29) {
         var1.writeBoolean(this.reducedDebugInfo);
      }

      if (var3 >= 573) {
         var1.writeBoolean(this.normalRespawn);
      }

      if (var3 >= 735) {
         var1.writeBoolean(this.debug);
         var1.writeBoolean(this.flat);
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

   public int getEntityId() {
      return this.entityId;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public short getGameMode() {
      return this.gameMode;
   }

   public short getPreviousGameMode() {
      return this.previousGameMode;
   }

   public Set<String> getWorldNames() {
      return this.worldNames;
   }

   public Tag getDimensions() {
      return this.dimensions;
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

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public String getLevelType() {
      return this.levelType;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public int getSimulationDistance() {
      return this.simulationDistance;
   }

   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   public boolean isNormalRespawn() {
      return this.normalRespawn;
   }

   public boolean isDebug() {
      return this.debug;
   }

   public boolean isFlat() {
      return this.flat;
   }

   public Location getDeathLocation() {
      return this.deathLocation;
   }

   public void setEntityId(int var1) {
      this.entityId = var1;
   }

   public void setHardcore(boolean var1) {
      this.hardcore = var1;
   }

   public void setGameMode(short var1) {
      this.gameMode = var1;
   }

   public void setPreviousGameMode(short var1) {
      this.previousGameMode = var1;
   }

   public void setWorldNames(Set<String> var1) {
      this.worldNames = var1;
   }

   public void setDimensions(Tag var1) {
      this.dimensions = var1;
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

   public void setMaxPlayers(int var1) {
      this.maxPlayers = var1;
   }

   public void setLevelType(String var1) {
      this.levelType = var1;
   }

   public void setViewDistance(int var1) {
      this.viewDistance = var1;
   }

   public void setSimulationDistance(int var1) {
      this.simulationDistance = var1;
   }

   public void setReducedDebugInfo(boolean var1) {
      this.reducedDebugInfo = var1;
   }

   public void setNormalRespawn(boolean var1) {
      this.normalRespawn = var1;
   }

   public void setDebug(boolean var1) {
      this.debug = var1;
   }

   public void setFlat(boolean var1) {
      this.flat = var1;
   }

   public void setDeathLocation(Location var1) {
      this.deathLocation = var1;
   }

   @Override
   public String toString() {
      return "Login(entityId="
         + this.getEntityId()
         + ", hardcore="
         + this.isHardcore()
         + ", gameMode="
         + this.getGameMode()
         + ", previousGameMode="
         + this.getPreviousGameMode()
         + ", worldNames="
         + this.getWorldNames()
         + ", dimensions="
         + this.getDimensions()
         + ", dimension="
         + this.getDimension()
         + ", worldName="
         + this.getWorldName()
         + ", seed="
         + this.getSeed()
         + ", difficulty="
         + this.getDifficulty()
         + ", maxPlayers="
         + this.getMaxPlayers()
         + ", levelType="
         + this.getLevelType()
         + ", viewDistance="
         + this.getViewDistance()
         + ", simulationDistance="
         + this.getSimulationDistance()
         + ", reducedDebugInfo="
         + this.isReducedDebugInfo()
         + ", normalRespawn="
         + this.isNormalRespawn()
         + ", debug="
         + this.isDebug()
         + ", flat="
         + this.isFlat()
         + ", deathLocation="
         + this.getDeathLocation()
         + ")";
   }

   public Login() {
   }

   public Login(
      int var1,
      boolean var2,
      short var3,
      short var4,
      Set<String> var5,
      Tag var6,
      Object var7,
      String var8,
      long var9,
      short var11,
      int var12,
      String var13,
      int var14,
      int var15,
      boolean var16,
      boolean var17,
      boolean var18,
      boolean var19,
      Location var20
   ) {
      this.entityId = var1;
      this.hardcore = var2;
      this.gameMode = var3;
      this.previousGameMode = var4;
      this.worldNames = var5;
      this.dimensions = var6;
      this.dimension = var7;
      this.worldName = var8;
      this.seed = var9;
      this.difficulty = var11;
      this.maxPlayers = var12;
      this.levelType = var13;
      this.viewDistance = var14;
      this.simulationDistance = var15;
      this.reducedDebugInfo = var16;
      this.normalRespawn = var17;
      this.debug = var18;
      this.flat = var19;
      this.deathLocation = var20;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Login)) {
         return false;
      } else {
         Login var2 = (Login)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getEntityId() != var2.getEntityId()) {
            return false;
         } else if (this.isHardcore() != var2.isHardcore()) {
            return false;
         } else if (this.getGameMode() != var2.getGameMode()) {
            return false;
         } else if (this.getPreviousGameMode() != var2.getPreviousGameMode()) {
            return false;
         } else if (this.getSeed() != var2.getSeed()) {
            return false;
         } else if (this.getDifficulty() != var2.getDifficulty()) {
            return false;
         } else if (this.getMaxPlayers() != var2.getMaxPlayers()) {
            return false;
         } else if (this.getViewDistance() != var2.getViewDistance()) {
            return false;
         } else if (this.getSimulationDistance() != var2.getSimulationDistance()) {
            return false;
         } else if (this.isReducedDebugInfo() != var2.isReducedDebugInfo()) {
            return false;
         } else if (this.isNormalRespawn() != var2.isNormalRespawn()) {
            return false;
         } else if (this.isDebug() != var2.isDebug()) {
            return false;
         } else if (this.isFlat() != var2.isFlat()) {
            return false;
         } else {
            Set var3 = this.getWorldNames();
            Set var4 = var2.getWorldNames();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               Tag var5 = this.getDimensions();
               Tag var6 = var2.getDimensions();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  Object var7 = this.getDimension();
                  Object var8 = var2.getDimension();
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     String var9 = this.getWorldName();
                     String var10 = var2.getWorldName();
                     if (var9 == null ? var10 == null : var9.equals(var10)) {
                        String var11 = this.getLevelType();
                        String var12 = var2.getLevelType();
                        if (var11 == null ? var12 == null : var11.equals(var12)) {
                           Location var13 = this.getDeathLocation();
                           Location var14 = var2.getDeathLocation();
                           return var13 == null ? var14 == null : var13.equals(var14);
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
      return var1 instanceof Login;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getEntityId();
      var2 = var2 * 59 + (this.isHardcore() ? 79 : 97);
      var2 = var2 * 59 + this.getGameMode();
      var2 = var2 * 59 + this.getPreviousGameMode();
      long var3 = this.getSeed();
      var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
      var2 = var2 * 59 + this.getDifficulty();
      var2 = var2 * 59 + this.getMaxPlayers();
      var2 = var2 * 59 + this.getViewDistance();
      var2 = var2 * 59 + this.getSimulationDistance();
      var2 = var2 * 59 + (this.isReducedDebugInfo() ? 79 : 97);
      var2 = var2 * 59 + (this.isNormalRespawn() ? 79 : 97);
      var2 = var2 * 59 + (this.isDebug() ? 79 : 97);
      var2 = var2 * 59 + (this.isFlat() ? 79 : 97);
      Set var5 = this.getWorldNames();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      Tag var6 = this.getDimensions();
      var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      Object var7 = this.getDimension();
      var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
      String var8 = this.getWorldName();
      var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
      String var9 = this.getLevelType();
      var2 = var2 * 59 + (var9 == null ? 43 : var9.hashCode());
      Location var10 = this.getDeathLocation();
      return var2 * 59 + (var10 == null ? 43 : var10.hashCode());
   }
}
