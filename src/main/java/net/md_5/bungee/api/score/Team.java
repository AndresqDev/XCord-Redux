package net.md_5.bungee.api.score;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;

public class Team {
   @NonNull
   private final String name;
   private String displayName;
   private String prefix;
   private String suffix;
   private byte friendlyFire;
   private String nameTagVisibility;
   private String collisionRule;
   private int color;
   private Set<String> players = new HashSet<>();

   public Collection<String> getPlayers() {
      return Collections.unmodifiableSet(this.players);
   }

   public void addPlayer(String var1) {
      this.players.add(var1);
   }

   public void removePlayer(String var1) {
      this.players.remove(var1);
   }

   public Team(@NonNull String var1) {
      if (var1 == null) {
         throw new NullPointerException("name is marked non-null but is null");
      } else {
         this.name = var1;
      }
   }

   @NonNull
   public String getName() {
      return this.name;
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

   public byte getFriendlyFire() {
      return this.friendlyFire;
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

   public void setDisplayName(String var1) {
      this.displayName = var1;
   }

   public void setPrefix(String var1) {
      this.prefix = var1;
   }

   public void setSuffix(String var1) {
      this.suffix = var1;
   }

   public void setFriendlyFire(byte var1) {
      this.friendlyFire = var1;
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

   public void setPlayers(Set<String> var1) {
      this.players = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Team)) {
         return false;
      } else {
         Team var2 = (Team)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getFriendlyFire() != var2.getFriendlyFire()) {
            return false;
         } else if (this.getColor() != var2.getColor()) {
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
                           if (var13 == null ? var14 == null : var13.equals(var14)) {
                              Collection var15 = this.getPlayers();
                              Collection var16 = var2.getPlayers();
                              return var15 == null ? var16 == null : var15.equals(var16);
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
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Team;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getFriendlyFire();
      var2 = var2 * 59 + this.getColor();
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
      Collection var9 = this.getPlayers();
      return var2 * 59 + (var9 == null ? 43 : var9.hashCode());
   }

   @Override
   public String toString() {
      return "Team(name="
         + this.getName()
         + ", displayName="
         + this.getDisplayName()
         + ", prefix="
         + this.getPrefix()
         + ", suffix="
         + this.getSuffix()
         + ", friendlyFire="
         + this.getFriendlyFire()
         + ", nameTagVisibility="
         + this.getNameTagVisibility()
         + ", collisionRule="
         + this.getCollisionRule()
         + ", color="
         + this.getColor()
         + ", players="
         + this.getPlayers()
         + ")";
   }
}
