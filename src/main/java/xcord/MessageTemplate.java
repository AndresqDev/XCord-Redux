package xcord;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class MessageTemplate {
    private final String json;
    private final BaseComponent[] components;
    private final String legacyText;

    public MessageTemplate(String string) {
        this.components = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', string));
        this.json = ComponentSerializer.toString(string);
        this.legacyText = TextComponent.toLegacyText(this.components);
    }

    public String getJson() {
        return this.json;
    }

    public BaseComponent[] getComponents() {
        return this.components;
    }

    public String getLegacyText() {
        return this.legacyText;
    }
}

