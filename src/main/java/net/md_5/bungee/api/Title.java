package net.md_5.bungee.api;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface Title {
   Title title(BaseComponent var1);

   Title title(BaseComponent... var1);

   Title subTitle(BaseComponent var1);

   Title subTitle(BaseComponent... var1);

   Title fadeIn(int var1);

   Title stay(int var1);

   Title fadeOut(int var1);

   Title clear();

   Title reset();

   Title send(ProxiedPlayer var1);
}
