package net.md_5.bungee.api;

import io.github.waterfallmc.waterfall.utils.UUIDUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.Util;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ServerPing {
   private ServerPing.Protocol version;
   private ServerPing.Players players;
   private BaseComponent description;
   private Favicon favicon;
   private final ServerPing.ModInfo modinfo = new ServerPing.ModInfo();

   @Deprecated
   public ServerPing(ServerPing.Protocol var1, ServerPing.Players var2, String var3, String var4) {
      this(var1, var2, new TextComponent(TextComponent.fromLegacyText(var3)), var4 == null ? null : Favicon.create(var4));
   }

   @Deprecated
   public ServerPing(ServerPing.Protocol var1, ServerPing.Players var2, String var3, Favicon var4) {
      this(var1, var2, new TextComponent(TextComponent.fromLegacyText(var3)), var4);
   }

   @Deprecated
   public String getFavicon() {
      return this.getFaviconObject() == null ? null : this.getFaviconObject().getEncoded();
   }

   public Favicon getFaviconObject() {
      return this.favicon;
   }

   @Deprecated
   public void setFavicon(String var1) {
      this.setFavicon(var1 == null ? null : Favicon.create(var1));
   }

   public void setFavicon(Favicon var1) {
      this.favicon = var1;
   }

   @Deprecated
   public void setDescription(String var1) {
      this.description = new TextComponent(TextComponent.fromLegacyText(var1));
   }

   @Deprecated
   public String getDescription() {
      return BaseComponent.toLegacyText(this.description);
   }

   public void setDescriptionComponent(BaseComponent var1) {
      this.description = var1;
   }

   public BaseComponent getDescriptionComponent() {
      return this.description;
   }

   public ServerPing.Protocol getVersion() {
      return this.version;
   }

   public ServerPing.Players getPlayers() {
      return this.players;
   }

   public ServerPing.ModInfo getModinfo() {
      return this.modinfo;
   }

   public void setVersion(ServerPing.Protocol var1) {
      this.version = var1;
   }

   public void setPlayers(ServerPing.Players var1) {
      this.players = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ServerPing)) {
         return false;
      } else {
         ServerPing var2 = (ServerPing)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            ServerPing.Protocol var3 = this.getVersion();
            ServerPing.Protocol var4 = var2.getVersion();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               ServerPing.Players var5 = this.getPlayers();
               ServerPing.Players var6 = var2.getPlayers();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  String var7 = this.getDescription();
                  String var8 = var2.getDescription();
                  if (var7 == null ? var8 == null : var7.equals(var8)) {
                     String var9 = this.getFavicon();
                     String var10 = var2.getFavicon();
                     if (var9 == null ? var10 == null : var9.equals(var10)) {
                        ServerPing.ModInfo var11 = this.getModinfo();
                        ServerPing.ModInfo var12 = var2.getModinfo();
                        return var11 == null ? var12 == null : var11.equals(var12);
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
      return var1 instanceof ServerPing;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      ServerPing.Protocol var3 = this.getVersion();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      ServerPing.Players var4 = this.getPlayers();
      var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      String var5 = this.getDescription();
      var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
      String var6 = this.getFavicon();
      var2 = var2 * 59 + (var6 == null ? 43 : var6.hashCode());
      ServerPing.ModInfo var7 = this.getModinfo();
      return var2 * 59 + (var7 == null ? 43 : var7.hashCode());
   }

   @Override
   public String toString() {
      return "ServerPing(version="
         + this.getVersion()
         + ", players="
         + this.getPlayers()
         + ", description="
         + this.getDescription()
         + ", modinfo="
         + this.getModinfo()
         + ")";
   }

   public ServerPing() {
   }

   public ServerPing(ServerPing.Protocol var1, ServerPing.Players var2, BaseComponent var3, Favicon var4) {
      this.version = var1;
      this.players = var2;
      this.description = var3;
      this.favicon = var4;
   }

   public static class ModInfo {
      private String type = "FML";
      private List<ServerPing.ModItem> modList = new ArrayList<>();

      public String getType() {
         return this.type;
      }

      public List<ServerPing.ModItem> getModList() {
         return this.modList;
      }

      public void setType(String var1) {
         this.type = var1;
      }

      public void setModList(List<ServerPing.ModItem> var1) {
         this.modList = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ServerPing.ModInfo)) {
            return false;
         } else {
            ServerPing.ModInfo var2 = (ServerPing.ModInfo)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else {
               String var3 = this.getType();
               String var4 = var2.getType();
               if (var3 == null ? var4 == null : var3.equals(var4)) {
                  List var5 = this.getModList();
                  List var6 = var2.getModList();
                  return var5 == null ? var6 == null : var5.equals(var6);
               } else {
                  return false;
               }
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof ServerPing.ModInfo;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         String var3 = this.getType();
         var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
         List var4 = this.getModList();
         return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      }

      @Override
      public String toString() {
         return "ServerPing.ModInfo(type=" + this.getType() + ", modList=" + this.getModList() + ")";
      }
   }

   public static class ModItem {
      private String modid;
      private String version;

      public String getModid() {
         return this.modid;
      }

      public String getVersion() {
         return this.version;
      }

      public void setModid(String var1) {
         this.modid = var1;
      }

      public void setVersion(String var1) {
         this.version = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ServerPing.ModItem)) {
            return false;
         } else {
            ServerPing.ModItem var2 = (ServerPing.ModItem)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else {
               String var3 = this.getModid();
               String var4 = var2.getModid();
               if (var3 == null ? var4 == null : var3.equals(var4)) {
                  String var5 = this.getVersion();
                  String var6 = var2.getVersion();
                  return var5 == null ? var6 == null : var5.equals(var6);
               } else {
                  return false;
               }
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof ServerPing.ModItem;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         String var3 = this.getModid();
         var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
         String var4 = this.getVersion();
         return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      }

      @Override
      public String toString() {
         return "ServerPing.ModItem(modid=" + this.getModid() + ", version=" + this.getVersion() + ")";
      }

      public ModItem(String var1, String var2) {
         this.modid = var1;
         this.version = var2;
      }
   }

   public static class PlayerInfo {
      private String name;
      private UUID uniqueId;
      private static final UUID md5UUID = Util.getUUID("af74a02d19cb445bb07f6866a861f783");

      public PlayerInfo(String var1, String var2) {
         this.setName(var1);
         this.setId(var2);
      }

      public void setId(String var1) {
         try {
            this.uniqueId = Util.getUUID(var1);
         } catch (Exception var3) {
            this.uniqueId = md5UUID;
         }
      }

      public String getId() {
         return UUIDUtils.undash(this.uniqueId.toString());
      }

      public String getName() {
         return this.name;
      }

      public UUID getUniqueId() {
         return this.uniqueId;
      }

      public void setName(String var1) {
         this.name = var1;
      }

      public void setUniqueId(UUID var1) {
         this.uniqueId = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ServerPing.PlayerInfo)) {
            return false;
         } else {
            ServerPing.PlayerInfo var2 = (ServerPing.PlayerInfo)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else {
               String var3 = this.getName();
               String var4 = var2.getName();
               if (var3 == null ? var4 == null : var3.equals(var4)) {
                  UUID var5 = this.getUniqueId();
                  UUID var6 = var2.getUniqueId();
                  return var5 == null ? var6 == null : var5.equals(var6);
               } else {
                  return false;
               }
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof ServerPing.PlayerInfo;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         String var3 = this.getName();
         var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
         UUID var4 = this.getUniqueId();
         return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      }

      @Override
      public String toString() {
         return "ServerPing.PlayerInfo(name=" + this.getName() + ", uniqueId=" + this.getUniqueId() + ")";
      }

      public PlayerInfo(String var1, UUID var2) {
         this.name = var1;
         this.uniqueId = var2;
      }
   }

   public static class Players {
      private int max;
      private int online;
      private ServerPing.PlayerInfo[] sample;

      public int getMax() {
         return this.max;
      }

      public int getOnline() {
         return this.online;
      }

      public ServerPing.PlayerInfo[] getSample() {
         return this.sample;
      }

      public void setMax(int var1) {
         this.max = var1;
      }

      public void setOnline(int var1) {
         this.online = var1;
      }

      public void setSample(ServerPing.PlayerInfo[] var1) {
         this.sample = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ServerPing.Players)) {
            return false;
         } else {
            ServerPing.Players var2 = (ServerPing.Players)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else if (this.getMax() != var2.getMax()) {
               return false;
            } else {
               return this.getOnline() != var2.getOnline() ? false : Arrays.deepEquals(this.getSample(), var2.getSample());
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof ServerPing.Players;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         var2 = var2 * 59 + this.getMax();
         var2 = var2 * 59 + this.getOnline();
         return var2 * 59 + Arrays.deepHashCode(this.getSample());
      }

      @Override
      public String toString() {
         return "ServerPing.Players(max=" + this.getMax() + ", online=" + this.getOnline() + ", sample=" + Arrays.deepToString(this.getSample()) + ")";
      }

      public Players(int var1, int var2, ServerPing.PlayerInfo[] var3) {
         this.max = var1;
         this.online = var2;
         this.sample = var3;
      }
   }

   public static class Protocol {
      private String name;
      private int protocol;

      public String getName() {
         return this.name;
      }

      public int getProtocol() {
         return this.protocol;
      }

      public void setName(String var1) {
         this.name = var1;
      }

      public void setProtocol(int var1) {
         this.protocol = var1;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ServerPing.Protocol)) {
            return false;
         } else {
            ServerPing.Protocol var2 = (ServerPing.Protocol)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else if (this.getProtocol() != var2.getProtocol()) {
               return false;
            } else {
               String var3 = this.getName();
               String var4 = var2.getName();
               return var3 == null ? var4 == null : var3.equals(var4);
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof ServerPing.Protocol;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         var2 = var2 * 59 + this.getProtocol();
         String var3 = this.getName();
         return var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      }

      @Override
      public String toString() {
         return "ServerPing.Protocol(name=" + this.getName() + ", protocol=" + this.getProtocol() + ")";
      }

      public Protocol(String var1, int var2) {
         this.name = var1;
         this.protocol = var2;
      }
   }
}
