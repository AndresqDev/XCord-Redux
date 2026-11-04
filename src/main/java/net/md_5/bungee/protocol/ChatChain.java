package net.md_5.bungee.protocol;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ChatChain extends DefinedPacket {
   private List<ChatChain.ChainLink> seen;
   private List<ChatChain.ChainLink> received;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.seen = readLinks(var1);
      if (var1.readBoolean()) {
         this.received = readLinks(var1);
      }
   }

   private static List<ChatChain.ChainLink> readLinks(ByteBuf var0) {
      int var1 = readVarInt(var0);
      Preconditions.checkArgument(var1 <= 5, "Too many entries");
      LinkedList var2 = new LinkedList();

      for (int var3 = 0; var3 < var1; var3++) {
         var2.add(new ChatChain.ChainLink(readUUID(var0), readArray(var0)));
      }

      return var2;
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeLinks(this.seen, var1);
      if (this.received != null) {
         var1.writeBoolean(true);
         writeLinks(this.received, var1);
      } else {
         var1.writeBoolean(false);
      }
   }

   private static void writeLinks(List<ChatChain.ChainLink> var0, ByteBuf var1) {
      writeVarInt(var0.size(), var1);

      for (ChatChain.ChainLink var3 : var0) {
         writeUUID(var3.sender, var1);
         writeArray(var3.signature, var1);
      }
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      throw new UnsupportedOperationException("Not supported.");
   }

   public List<ChatChain.ChainLink> getSeen() {
      return this.seen;
   }

   public List<ChatChain.ChainLink> getReceived() {
      return this.received;
   }

   public void setSeen(List<ChatChain.ChainLink> var1) {
      this.seen = var1;
   }

   public void setReceived(List<ChatChain.ChainLink> var1) {
      this.received = var1;
   }

   @Override
   public String toString() {
      return "ChatChain(seen=" + this.getSeen() + ", received=" + this.getReceived() + ")";
   }

   public ChatChain() {
   }

   public ChatChain(List<ChatChain.ChainLink> var1, List<ChatChain.ChainLink> var2) {
      this.seen = var1;
      this.received = var2;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ChatChain)) {
         return false;
      } else {
         ChatChain var2 = (ChatChain)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            List var3 = this.getSeen();
            List var4 = var2.getSeen();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               List var5 = this.getReceived();
               List var6 = var2.getReceived();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ChatChain;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      List var3 = this.getSeen();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      List var4 = this.getReceived();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }

   public static class ChainLink {
      private final UUID sender;
      private final byte[] signature;

      public ChainLink(UUID var1, byte[] var2) {
         this.sender = var1;
         this.signature = var2;
      }

      public UUID getSender() {
         return this.sender;
      }

      public byte[] getSignature() {
         return this.signature;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof ChatChain.ChainLink)) {
            return false;
         } else {
            ChatChain.ChainLink var2 = (ChatChain.ChainLink)var1;
            if (!var2.canEqual(this)) {
               return false;
            } else {
               UUID var3 = this.getSender();
               UUID var4 = var2.getSender();
               return (var3 == null ? var4 == null : var3.equals(var4)) ? Arrays.equals(this.getSignature(), var2.getSignature()) : false;
            }
         }
      }

      protected boolean canEqual(Object var1) {
         return var1 instanceof ChatChain.ChainLink;
      }

      @Override
      public int hashCode() {
         byte var1 = 59;
         int var2 = 1;
         UUID var3 = this.getSender();
         var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
         return var2 * 59 + Arrays.hashCode(this.getSignature());
      }

      @Override
      public String toString() {
         return "ChatChain.ChainLink(sender=" + this.getSender() + ", signature=" + Arrays.toString(this.getSignature()) + ")";
      }
   }
}
