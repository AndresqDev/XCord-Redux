package net.md_5.bungee.protocol.packet;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import io.netty.buffer.ByteBuf;
import java.util.LinkedList;
import java.util.List;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class TabCompleteResponse extends DefinedPacket {
   private int transactionId;
   private Suggestions suggestions;
   private List<String> commands;

   public TabCompleteResponse(int var1, Suggestions var2) {
      this.transactionId = var1;
      this.suggestions = var2;
   }

   public TabCompleteResponse(List<String> var1) {
      this.commands = var1;
   }

   @Override
   public void read(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var3 >= 393) {
         this.transactionId = readVarInt(var1);
         int var4 = readVarInt(var1);
         int var5 = readVarInt(var1);
         StringRange var6 = StringRange.between(var4, var4 + var5);
         int var7 = readVarInt(var1, 32500);
         LinkedList var8 = new LinkedList();

         for (int var9 = 0; var9 < var7; var9++) {
            String var10 = readString(var1);
            String var11 = var1.readBoolean() ? readString(var1) : null;
            var8.add(new Suggestion(var6, var10, new LiteralMessage(var11)));
         }

         this.suggestions = new Suggestions(var6, var8);
      }

      if (var3 < 393) {
         this.commands = readStringArray(var1);
      }
   }

   @Override
   public void write(ByteBuf var1, ProtocolConstants.Direction var2, int var3) {
      if (var3 >= 393) {
         writeVarInt(this.transactionId, var1);
         writeVarInt(this.suggestions.getRange().getStart(), var1);
         writeVarInt(this.suggestions.getRange().getLength(), var1);
         writeVarInt(this.suggestions.getList().size(), var1);

         for (Suggestion var5 : this.suggestions.getList()) {
            writeString(var5.getText(), var1);
            var1.writeBoolean(var5.getTooltip() != null && var5.getTooltip().getString() != null);
            if (var5.getTooltip() != null && var5.getTooltip().getString() != null) {
               writeString(var5.getTooltip().getString(), var1);
            }
         }
      }

      if (var3 < 393) {
         writeStringArray(this.commands, var1);
      }
   }

   @Override
   public void handle(AbstractPacketHandler var1) throws Exception {
      var1.handle(this);
   }

   public int getTransactionId() {
      return this.transactionId;
   }

   public Suggestions getSuggestions() {
      return this.suggestions;
   }

   public List<String> getCommands() {
      return this.commands;
   }

   public void setTransactionId(int var1) {
      this.transactionId = var1;
   }

   public void setSuggestions(Suggestions var1) {
      this.suggestions = var1;
   }

   public void setCommands(List<String> var1) {
      this.commands = var1;
   }

   @Override
   public String toString() {
      return "TabCompleteResponse(transactionId="
         + this.getTransactionId()
         + ", suggestions="
         + this.getSuggestions()
         + ", commands="
         + this.getCommands()
         + ")";
   }

   public TabCompleteResponse() {
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof TabCompleteResponse)) {
         return false;
      } else {
         TabCompleteResponse var2 = (TabCompleteResponse)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getTransactionId() != var2.getTransactionId()) {
            return false;
         } else {
            Suggestions var3 = this.getSuggestions();
            Suggestions var4 = var2.getSuggestions();
            if (var3 == null ? var4 == null : var3.equals(var4)) {
               List var5 = this.getCommands();
               List var6 = var2.getCommands();
               return var5 == null ? var6 == null : var5.equals(var6);
            } else {
               return false;
            }
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof TabCompleteResponse;
   }

   @Override
   public int hashCode() {
      byte var1 = 59;
      int var2 = 1;
      var2 = var2 * 59 + this.getTransactionId();
      Suggestions var3 = this.getSuggestions();
      var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      List var4 = this.getCommands();
      return var2 * 59 + (var4 == null ? 43 : var4.hashCode());
   }
}
