package xcord.command;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class SubCommandDispatcher extends Command {
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public SubCommandDispatcher(String name, String permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            this.sendHelp(sender);
        } else {
            String subCommandName = args[0].toLowerCase();
            SubCommand subCommand = this.subCommands.get(subCommandName);
            if (subCommand != null) {
                args = args.length > 1 ? Arrays.copyOfRange(args, 1, args.length) : new String[0];
                if (!sender.hasPermission(subCommand.getPermission())) {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
                } else if (args.length < subCommand.getMinArgs()) {
                    sender.sendMessage(ChatColor.RED + "Wrong argument length: " + ChatColor.GRAY + "/" + subCommandName + " " + subCommand.getUsage());
                } else {
                    try {
                        subCommand.execute(sender, args);
                    }
                    catch (Exception exception) {
                        sender.sendMessage("An error occurred while executing this command. Check your logs/console for more details.");
                        exception.printStackTrace();
                    }
                }
            } else {
                sender.sendMessage("\u00a7cUnknown command: \u00a77" + subCommandName);
            }
        }
    }

    protected void sendHelp(CommandSender sender) {
        sender.sendMessage("\u00a7c" + this.getName() + " Commands>");
        for (Map.Entry<String, SubCommand> entry : this.subCommands.entrySet()) {
            sender.sendMessage(" \u00a74- \u00a78/\u00a77" + this.getName() + " " + entry.getKey() + " " + entry.getValue().getUsage());
        }
    }

    public void registerSubCommand(String name, SubCommand subCommand) {
        this.subCommands.put(name.toLowerCase(), subCommand);
    }
}
