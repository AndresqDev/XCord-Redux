package io.github.waterfallmc.waterfall.exception;

import com.google.common.base.Preconditions;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ProxyCommandException extends ProxyException {
   private final Command command;
   private final CommandSender commandSender;
   private final String[] arguments;

   public ProxyCommandException(String var1, Throwable var2, Command var3, CommandSender var4, String[] var5) {
      super(var1, var2);
      this.commandSender = (CommandSender)Preconditions.checkNotNull(var4, "commandSender");
      this.arguments = (String[])Preconditions.checkNotNull(var5, "arguments");
      this.command = (Command)Preconditions.checkNotNull(var3, "command");
   }

   public ProxyCommandException(Throwable var1, Command var2, CommandSender var3, String[] var4) {
      super(var1);
      this.commandSender = (CommandSender)Preconditions.checkNotNull(var3, "commandSender");
      this.arguments = (String[])Preconditions.checkNotNull(var4, "arguments");
      this.command = (Command)Preconditions.checkNotNull(var2, "command");
   }

   protected ProxyCommandException(String var1, Throwable var2, boolean var3, boolean var4, Command var5, CommandSender var6, String[] var7) {
      super(var1, var2, var3, var4);
      this.commandSender = (CommandSender)Preconditions.checkNotNull(var6, "commandSender");
      this.arguments = (String[])Preconditions.checkNotNull(var7, "arguments");
      this.command = (Command)Preconditions.checkNotNull(var5, "command");
   }

   public Command getCommand() {
      return this.command;
   }

   public CommandSender getCommandSender() {
      return this.commandSender;
   }

   public String[] getArguments() {
      return this.arguments;
   }
}
