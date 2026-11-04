package net.md_5.bungee;

import net.md_5.bungee.api.SkinConfiguration;

public class PlayerSkinConfiguration implements SkinConfiguration {
   static final SkinConfiguration SKIN_SHOW_ALL = new PlayerSkinConfiguration((byte)127);
   private final byte bitmask;

   @Override
   public boolean hasCape() {
      return (this.bitmask >> 0 & 1) == 1;
   }

   @Override
   public boolean hasJacket() {
      return (this.bitmask >> 1 & 1) == 1;
   }

   @Override
   public boolean hasLeftSleeve() {
      return (this.bitmask >> 2 & 1) == 1;
   }

   @Override
   public boolean hasRightSleeve() {
      return (this.bitmask >> 3 & 1) == 1;
   }

   @Override
   public boolean hasLeftPants() {
      return (this.bitmask >> 4 & 1) == 1;
   }

   @Override
   public boolean hasRightPants() {
      return (this.bitmask >> 5 & 1) == 1;
   }

   @Override
   public boolean hasHat() {
      return (this.bitmask >> 6 & 1) == 1;
   }

   public PlayerSkinConfiguration(byte var1) {
      this.bitmask = var1;
   }
}
