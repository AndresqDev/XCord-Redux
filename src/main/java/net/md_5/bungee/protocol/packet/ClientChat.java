package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ChatChain;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ClientChat extends DefinedPacket {
   private String message;
   private long timestamp;
   private long salt;
   private byte[] signature;
   private boolean signedPreview;
   private ChatChain chain;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.message = readString(var1, 256);
      this.timestamp = var1.readLong();
      this.salt = var1.readLong();
      this.signature = readArray(var1);
      this.signedPreview = var1.readBoolean();
      if (var3 >= 760) {
         this.chain = new ChatChain();
         this.chain.read(var1, var2, var3);
      }
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.message, var1);
      var1.writeLong(this.timestamp);
      var1.writeLong(this.salt);
      writeArray(this.signature, var1);
      var1.writeBoolean(this.signedPreview);
      if (var3 >= 760) {
         this.chain.write(var1, var2, var3);
      }
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getMessage() {
      return this.message;
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   public long getSalt() {
      return this.salt;
   }

   public byte[] getSignature() {
      return this.signature;
   }

   public boolean isSignedPreview() {
      return this.signedPreview;
   }

   public ChatChain getChain() {
      return this.chain;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   public void setTimestamp(long var1) {
      this.timestamp = var1;
   }

   public void setSalt(long var1) {
      this.salt = var1;
   }

   public void setSignature(byte[] var1) {
      this.signature = var1;
   }

   public void setSignedPreview(boolean var1) {
      this.signedPreview = var1;
   }

   public void setChain(ChatChain var1) {
      this.chain = var1;
   }

   @Override
   public String toString() {
      return "ClientChat(message="
         + this.getMessage()
         + ", timestamp="
         + this.getTimestamp()
         + ", salt="
         + this.getSalt()
         + ", signature="
         + Arrays.toString(this.getSignature())
         + ", signedPreview="
         + this.isSignedPreview()
         + ", chain="
         + this.getChain()
         + ")";
   }

   public ClientChat() {
   }

   public ClientChat(String var1, long var2, long var4, byte[] var6, boolean var7, ChatChain var8) {
      this.message = var1;
      this.timestamp = var2;
      this.salt = var4;
      this.signature = var6;
      this.signedPreview = var7;
      this.chain = var8;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ClientChat)) {
         return false;
      } else {
         ClientChat var2 = (ClientChat)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getTimestamp() != var2.getTimestamp()) {
            return false;
         } else if (this.getSalt() != var2.getSalt()) {
            return false;
         } else if (this.isSignedPreview() != var2.isSignedPreview()) {
            return false;
         } else {
            String var3 = this.getMessage();
            String var4 = var2.getMessage();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               if (!Arrays.equals(this.getSignature(), var2.getSignature())) {
                  return false;
               } else {
                  ChatChain var5 = this.getChain();
                  ChatChain var6 = var2.getChain();
                  return var5 == null ? var6 == null : var5.equals(var6);
               }
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof ClientChat;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      long var3 = this.getTimestamp();
      var2 = var2 * 59 + (int)(var3 >>> 32 ^ var3);
      long var5 = this.getSalt();
      var2 = var2 * 59 + (int)(var5 >>> 32 ^ var5);
      var2 = var2 * 59 + (this.isSignedPreview() ? 79 : 97);
      String var7 = this.getMessage();
      var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
      var2 = var2 * 59 + Arrays.hashCode(this.getSignature());
      ChatChain var8 = this.getChain();
      return var2 * 59 + (var8 == null ? 43 : var8.hashCode());
   }
}
