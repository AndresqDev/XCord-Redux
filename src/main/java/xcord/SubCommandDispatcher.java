package xcord;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import xcord.SubCommand;

public class SubCommandDispatcher
extends Command {
    private final Map<String, SubCommand> subCommands = new HashMap<String, SubCommand>();

    public SubCommandDispatcher(String string, String string2, String ... stringArray) {
        super(string, string2, stringArray);
    }

    @Override
    public void execute(CommandSender commandSender, String[] stringArray) {
        if (stringArray.length < 1) {
            this.sendHelp(commandSender);
        } else {
            String string = stringArray[0].toLowerCase();
            SubCommand subCommand = this.subCommands.get(string);
            if (subCommand != null) {
                stringArray = stringArray.length > 1 ? Arrays.copyOfRange(stringArray, 1, stringArray.length) : new String[]{};
                if (!commandSender.hasPermission(subCommand.getPermission())) {
                    commandSender.sendMessage(ChatColor.RED + "You do not have permission to execute this command!");
                } else if (stringArray.length < subCommand.getMinArgs()) {
                    commandSender.sendMessage(ChatColor.RED + "Wrong argument length: " + ChatColor.GRAY + "/" + string + " " + subCommand.getUsage());
                } else {
                    try {
                        subCommand.execute(commandSender, stringArray);
                    }
                    catch (Exception exception) {
                        commandSender.sendMessage("An error occurred while executing this command. Check your logs/console for more details.");
                        exception.printStackTrace();
                    }
                }
            } else {
                commandSender.sendMessage("\u00a7cUnknown command: \u00a77" + string);
            }
        }
    }

    protected void sendHelp(CommandSender commandSender) {
        commandSender.sendMessage("\u00a7c" + this.getName() + " Commands>");
        for (Map.Entry<String, SubCommand> entry : this.subCommands.entrySet()) {
            commandSender.sendMessage(" \u00a74- \u00a78/\u00a77" + this.getName() + " " + entry.getKey() + " " + entry.getValue().getUsage());
        }
    }

    public void registerSubCommand(String string, SubCommand subCommand) {
        this.subCommands.put(string.toLowerCase(), subCommand);
    }
}

