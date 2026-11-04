package net.md_5.bungee.protocol.packet;

import io.github.waterfallmc.travertine.protocol.MultiVersionPacketV17;
import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ProtocolConstants;

public class TabCompleteRequest extends MultiVersionPacketV17 {
   private int transactionId;
   private String cursor;
   private boolean assumeCommand;
   private boolean hasPositon;
   private long position;

   public TabCompleteRequest(int var1, String var2) {
      this.transactionId = var1;
      this.cursor = var2;
   }

   public TabCompleteRequest(String var1, boolean var2, boolean var3, long var4) {
      this.cursor = var1;
      this.assumeCommand = var2;
      this.hasPositon = var3;
      this.position = var4;
   }

   public void v17Read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.cursor = readString(var1);
   }

   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var3 >= 393) {
         this.transactionId = readVarInt(var1);
      }

      this.cursor = readString(var1, 32500);
      if (var3 < 393) {
         if (var3 >= 107) {
            this.assumeCommand = var1.readBoolean();
         }

         if (this.hasPositon = var1.readBoolean()) {
            this.position = var1.readLong();
         }
      }
   }

   public void v17Write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.cursor, var1);
   }

   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var3 >= 393) {
         writeVarInt(this.transactionId, var1);
      }

      writeString(this.cursor, var1);
      if (var3 < 393) {
         if (var3 >= 107) {
            var1.writeBoolean(this.assumeCommand);
         }

         var1.writeBoolean(this.hasPositon);
         if (this.hasPositon) {
            var1.writeLong(this.position);
         }
      }
   }

   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public String getCursor() {
      return this.cursor;
   }

   public boolean isAssumeCommand() {
      return this.assumeCommand;
   }

   public boolean isHasPositon() {
      return this.hasPositon;
   }

   public long getPosition() {
      return this.position;
   }

   public void setTransactionId(int var1) {
      this.transactionId = var1;
   }

   public void setCursor(String var1) {
      this.cursor = var1;
   }

   public void setAssumeCommand(boolean var1) {
      this.assumeCommand = var1;
   }

   public void setHasPositon(boolean var1) {
      this.hasPositon = var1;
   }

   public void setPosition(long var1) {
      this.position = var1;
   }

   public String toString() {
      return "TabCompleteRequest(transactionId="
         + this.getTransactionId()
         + ", cursor="
         + this.getCursor()
         + ", assumeCommand="
         + this.isAssumeCommand()
         + ", hasPositon="
         + this.isHasPositon()
         + ", position="
         + this.getPosition()
         + ")";
   }

   public TabCompleteRequest() {
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TabCompleteRequest)) {
         return false;
      } else {
         TabCompleteRequest var2 = (TabCompleteRequest)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getTransactionId() != var2.getTransactionId()) {
            return false;
         } else if (this.isAssumeCommand() != var2.isAssumeCommand()) {
            return false;
         } else if (this.isHasPositon() != var2.isHasPositon()) {
            return false;
         } else if (this.getPosition() != var2.getPosition()) {
            return false;
         } else {
            String var3 = this.getCursor();
            String var4 = var2.getCursor();
            return var3 == null ? var4 == null : var3.equals(var4);
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof TabCompleteRequest;
   }

   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getTransactionId();
      var2 = var2 * 59 + (this.isAssumeCommand() ? 79 : 97);
      var2 = var2 * 59 + (this.isHasPositon() ? 79 : 97);
      long var3 = this.getPosition();
      var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
      String var5 = this.getCursor();
      return var2 * 59 + (var5 == null ? 43 : var5.hashCode());
   }
}
