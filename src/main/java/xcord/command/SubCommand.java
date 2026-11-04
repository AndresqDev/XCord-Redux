package xcord.command;

import net.md_5.bungee.api.CommandSender;

public abstract class SubCommand {
    protected int minArgs = 0;
    protected String usage = "";
    protected String permission = "xcord.genericcommand";

    public abstract void execute(CommandSender sender, String[] args) throws IllegalArgumentException;

    public int getMinArgs() {
        return this.minArgs;
    }

    public String getUsage() {
        return this.usage;
    }

    public String getPermission() {
        return this.permission;
    }
}
