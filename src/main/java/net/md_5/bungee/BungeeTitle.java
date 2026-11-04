package net.md_5.bungee;

import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.packet.ClearTitles;
import net.md_5.bungee.protocol.packet.Subtitle;
import net.md_5.bungee.protocol.packet.TitleTimes;

public class BungeeTitle implements Title {
   private BungeeTitle.TitlePacketHolder<net.md_5.bungee.protocol.packet.Title> title;
   private BungeeTitle.TitlePacketHolder<Subtitle> subtitle;
   private BungeeTitle.TitlePacketHolder<TitleTimes> times;
   private BungeeTitle.TitlePacketHolder<ClearTitles> clear;
   private BungeeTitle.TitlePacketHolder<ClearTitles> reset;

   private static BungeeTitle.TitlePacketHolder<TitleTimes> createAnimationPacket() {
      BungeeTitle.TitlePacketHolder var0 = new BungeeTitle.TitlePacketHolder<>(
         new net.md_5.bungee.protocol.packet.Title(net.md_5.bungee.protocol.packet.Title.Action.TIMES), new TitleTimes()
      );
      var0.oldPacket.setFadeIn(20);
      var0.oldPacket.setStay(60);
      var0.oldPacket.setFadeOut(20);
      ((TitleTimes)var0.newPacket).setFadeIn(20);
      ((TitleTimes)var0.newPacket).setStay(60);
      ((TitleTimes)var0.newPacket).setFadeOut(20);
      return var0;
   }

   @Override
   public Title title(BaseComponent var1) {
      if (this.title == null) {
         net.md_5.bungee.protocol.packet.Title var2 = new net.md_5.bungee.protocol.packet.Title(net.md_5.bungee.protocol.packet.Title.Action.TITLE);
         this.title = new BungeeTitle.TitlePacketHolder<>(var2, var2);
      }

      this.title.oldPacket.setText(ComponentSerializer.toString(var1));
      return this;
   }

   @Override
   public Title title(BaseComponent... var1) {
      if (this.title == null) {
         net.md_5.bungee.protocol.packet.Title var2 = new net.md_5.bungee.protocol.packet.Title(net.md_5.bungee.protocol.packet.Title.Action.TITLE);
         this.title = new BungeeTitle.TitlePacketHolder<>(var2, var2);
      }

      this.title.oldPacket.setText(ComponentSerializer.toString(var1));
      return this;
   }

   @Override
   public Title subTitle(BaseComponent var1) {
      if (this.subtitle == null) {
         this.subtitle = new BungeeTitle.TitlePacketHolder<>(
            new net.md_5.bungee.protocol.packet.Title(net.md_5.bungee.protocol.packet.Title.Action.SUBTITLE), new Subtitle()
         );
      }

      String var2 = ComponentSerializer.toString(var1);
      this.subtitle.oldPacket.setText(var2);
      this.subtitle.newPacket.setText(var2);
      return this;
   }

   @Override
   public Title subTitle(BaseComponent... var1) {
      if (this.subtitle == null) {
         this.subtitle = new BungeeTitle.TitlePacketHolder<>(
            new net.md_5.bungee.protocol.packet.Title(net.md_5.bungee.protocol.packet.Title.Action.SUBTITLE), new Subtitle()
         );
      }

      String var2 = ComponentSerializer.toString(var1);
      this.subtitle.oldPacket.setText(var2);
      this.subtitle.newPacket.setText(var2);
      return this;
   }

   @Override
   public Title fadeIn(int var1) {
      if (this.times == null) {
         this.times = createAnimationPacket();
      }

      this.times.oldPacket.setFadeIn(var1);
      this.times.newPacket.setFadeIn(var1);
      return this;
   }

   @Override
   public Title stay(int var1) {
      if (this.times == null) {
         this.times = createAnimationPacket();
      }

      this.times.oldPacket.setStay(var1);
      this.times.newPacket.setStay(var1);
      return this;
   }

   @Override
   public Title fadeOut(int var1) {
      if (this.times == null) {
         this.times = createAnimationPacket();
      }

      this.times.oldPacket.setFadeOut(var1);
      this.times.newPacket.setFadeOut(var1);
      return this;
   }

   @Override
   public Title clear() {
      if (this.clear == null) {
         this.clear = new BungeeTitle.TitlePacketHolder<>(
            new net.md_5.bungee.protocol.packet.Title(net.md_5.bungee.protocol.packet.Title.Action.CLEAR), new ClearTitles()
         );
      }

      this.title = null;
      return this;
   }

   @Override
   public Title reset() {
      if (this.reset == null) {
         this.reset = new BungeeTitle.TitlePacketHolder<>(
            new net.md_5.bungee.protocol.packet.Title(net.md_5.bungee.protocol.packet.Title.Action.RESET), new ClearTitles(true)
         );
      }

      this.title = null;
      this.subtitle = null;
      this.times = null;
      return this;
   }

   private static void sendPacket(ProxiedPlayer var0, BungeeTitle.TitlePacketHolder var1) {
      if (var1 != null) {
         if (var0.getPendingConnection().getVersion() >= 755) {
            var0.unsafe().sendPacket(var1.newPacket);
         } else {
            var0.unsafe().sendPacket(var1.oldPacket);
         }
      }
   }

   @Override
   public Title send(ProxiedPlayer var1) {
      if (ProtocolConstants.isBeforeOrEq(var1.getPendingConnection().getVersion(), 5)) {
         return this;
      } else {
         sendPacket(var1, this.clear);
         sendPacket(var1, this.reset);
         sendPacket(var1, this.times);
         sendPacket(var1, this.subtitle);
         sendPacket(var1, this.title);
         return this;
      }
   }

   private static class TitlePacketHolder<T extends DefinedPacket> {
      private final net.md_5.bungee.protocol.packet.Title oldPacket;
      private final T newPacket;

      public TitlePacketHolder(net.md_5.bungee.protocol.packet.Title var1, T var2) {
         this.oldPacket = var1;
         this.newPacket = (T)var2;
      }

      public net.md_5.bungee.protocol.packet.Title getOldPacket() {
         return this.oldPacket;
      }

      public T getNewPacket() {
         return this.newPacket;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof BungeeTitle.TitlePacketHolder)) {
            return false;
         } else {
            BungeeTitle.TitlePacketHolder var2 = (BungeeTitle.TitlePacketHolder)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else {
               net.md_5.bungee.protocol.packet.Title var3 = this.getOldPacket();
               net.md_5.bungee.protocol.packet.Title var4 = var2.getOldPacket();
               if (var3 == null ? var4 == null : var3.equals(var4)) {
                  DefinedPacket var5 = this.getNewPacket();
                  DefinedPacket var6 = var2.getNewPacket();
                  return var5 == null ? var6 == null : var5.equals(var6);
               } else {
                  return false;
               }
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof BungeeTitle.TitlePacketHolder;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         net.md_5.bungee.protocol.packet.Title var3 = this.getOldPacket();
         var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
         DefinedPacket var4 = this.getNewPacket();
         return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
      }

      @Override
      public String toString() {
         return "BungeeTitle.TitlePacketHolder(oldPacket=" + this.getOldPacket() + ", newPacket=" + this.getNewPacket() + ")";
      }
   }
}
