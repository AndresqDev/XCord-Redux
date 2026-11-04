package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Title extends DefinedPacket {
   private Title.Action action;
   private String text;
   private int fadeIn;
   private int stay;
   private int fadeOut;

   public Title(Title.Action var1) {
      this.action = var1;
   }

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var3 >= 755) {
         this.text = readString(var1);
      } else {
         int var4 = readVarInt(var1);
         if (var3 <= 210 && var4 >= 2) {
            var4++;
         }

         this.action = Title.Action.values()[var4];
         switch (this.action) {
            case TITLE:
            case SUBTITLE:
            case ACTIONBAR:
               this.text = readString(var1);
               break;
            case TIMES:
               this.fadeIn = var1.readInt();
               this.stay = var1.readInt();
               this.fadeOut = var1.readInt();
         }
      }
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var3 >= 755) {
         writeString(this.text, var1);
      } else {
         int var4 = this.action.ordinal();
         if (var3 <= 210 && var4 >= 2) {
            var4--;
         }

         writeVarInt(var4, var1);
         switch (this.action) {
            case TITLE:
            case SUBTITLE:
            case ACTIONBAR:
               writeString(this.text, var1);
               break;
            case TIMES:
               var1.writeInt(this.fadeIn);
               var1.writeInt(this.stay);
               var1.writeInt(this.fadeOut);
         }
      }
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public Title.Action getAction() {
      return this.action;
   }

   public String getText() {
      return this.text;
   }

   public int getFadeIn() {
      return this.fadeIn;
   }

   public int getStay() {
      return this.stay;
   }

   public int getFadeOut() {
      return this.fadeOut;
   }

   public void setAction(Title.Action var1) {
      this.action = var1;
   }

   public void setText(String var1) {
      this.text = var1;
   }

   public void setFadeIn(int var1) {
      this.fadeIn = var1;
   }

   public void setStay(int var1) {
      this.stay = var1;
   }

   public void setFadeOut(int var1) {
      this.fadeOut = var1;
   }

   @Override
   public String toString() {
      return "Title(action="
         + this.getAction()
         + ", text="
         + this.getText()
         + ", fadeIn="
         + this.getFadeIn()
         + ", stay="
         + this.getStay()
         + ", fadeOut="
         + this.getFadeOut()
         + ")";
   }

   public Title() {
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Title)) {
         return false;
      } else {
         Title var2 = (Title)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getFadeIn() != var2.getFadeIn()) {
            return false;
         } else if (this.getStay() != var2.getStay()) {
            return false;
         } else if (this.getFadeOut() != var2.getFadeOut()) {
            return false;
         } else {
            Title.Action var3 = this.getAction();
            Title.Action var4 = var2.getAction();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               String var5 = this.getText();
               String var6 = var2.getText();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Title;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getFadeIn();
      var2 = var2 * 59 + this.getStay();
      var2 = var2 * 59 + this.getFadeOut();
      Title.Action var3 = this.getAction();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getText();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }

   public static enum Action {
      TITLE,
      SUBTITLE,
      ACTIONBAR,
      TIMES,
      CLEAR,
      RESET;
   }
}
