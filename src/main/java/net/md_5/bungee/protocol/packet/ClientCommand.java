package net.md_5.bungee.protocol.packet;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ChatChain;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ClientCommand extends DefinedPacket {
   private String command;
   private long timestamp;
   private long salt;
   private Map<String, byte[]> signatures;
   private boolean signedPreview;
   private ChatChain chain;

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      this.command = readString(var1);
      this.timestamp = var1.readLong();
      this.salt = var1.readLong();
      int var4 = readVarInt(var1);
      Preconditions.checkArgument(var4 <= 8, "Too many signatures");
      this.signatures = new HashMap<>(var4);

      for (int var5 = 0; var5 < var4; var5++) {
         this.signatures.put(readString(var1, 16), readArray(var1));
      }

      this.signedPreview = var1.readBoolean();
      if (var3 >= 760) {
         this.chain = new ChatChain();
         this.chain.read(var1, var2, var3);
      }
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      writeString(this.command, var1);
      var1.writeLong(this.timestamp);
      var1.writeLong(this.salt);
      writeVarInt(this.signatures.size(), var1);

      for (Entry var5 : this.signatures.entrySet()) {
         writeString((String)var5.getKey(), var1);
         writeArray((byte[])var5.getValue(), var1);
      }

      var1.writeBoolean(this.signedPreview);
      if (var3 >= 760) {
         this.chain.write(var1, var2, var3);
      }
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public String getCommand() {
      return this.command;
   }

   public long getTimestamp() {
      return this.timestamp;
   }

   public long getSalt() {
      return this.salt;
   }

   public Map<String, byte[]> getSignatures() {
      return this.signatures;
   }

   public boolean isSignedPreview() {
      return this.signedPreview;
   }

   public ChatChain getChain() {
      return this.chain;
   }

   public void setCommand(String var1) {
      this.command = var1;
   }

   public void setTimestamp(long var1) {
      this.timestamp = var1;
   }

   public void setSalt(long var1) {
      this.salt = var1;
   }

   public void setSignatures(Map<String, byte[]> var1) {
      this.signatures = var1;
   }

   public void setSignedPreview(boolean var1) {
      this.signedPreview = var1;
   }

   public void setChain(ChatChain var1) {
      this.chain = var1;
   }

   @Override
   public String toString() {
      return "ClientCommand(command="
         + this.getCommand()
         + ", timestamp="
         + this.getTimestamp()
         + ", salt="
         + this.getSalt()
         + ", signatures="
         + this.getSignatures()
         + ", signedPreview="
         + this.isSignedPreview()
         + ", chain="
         + this.getChain()
         + ")";
   }

   public ClientCommand() {
   }

   public ClientCommand(String var1, long var2, long var4, Map<String, byte[]> var6, boolean var7, ChatChain var8) {
      this.command = var1;
      this.timestamp = var2;
      this.salt = var4;
      this.signatures = var6;
      this.signedPreview = var7;
      this.chain = var8;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof ClientCommand)) {
         return false;
      } else {
         ClientCommand var2 = (ClientCommand)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getTimestamp() != var2.getTimestamp()) {
            return false;
         } else if (this.getSalt() != var2.getSalt()) {
            return false;
         } else if (this.isSignedPreview() != var2.isSignedPreview()) {
            return false;
         } else {
            String var3 = this.getCommand();
            String var4 = var2.getCommand();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               Map var5 = this.getSignatures();
               Map var6 = var2.getSignatures();
               if (var5 == null ? var6 == null : var5.equals(var6)) {
                  ChatChain var7 = this.getChain();
                  ChatChain var8 = var2.getChain();
                  return var7 == null ? var8 == null : var7.equals(var8);
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
      return var1 instanceof ClientCommand;
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
      String var7 = this.getCommand();
      var2 = var2 * 59 + (var7 == null ? 43 : var7.hashCode());
      Map var8 = this.getSignatures();
      var2 = var2 * 59 + (var8 == null ? 43 : var8.hashCode());
      ChatChain var9 = this.getChain();
      return var2 * 59 + (var9 == null ? 43 : var9.hashCode());
   }
}
