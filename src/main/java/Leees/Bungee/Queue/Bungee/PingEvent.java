package Leees.Bungee.Queue.Bungee;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import Leees.Bungee.Queue.Bungee.Lang;
import net.md_5.bungee.event.EventPriority;

import java.util.Random;

public class PingEvent implements Listener {
    ServerPing.Protocol protocol;
    private final Random random = new Random();
    @EventHandler(priority = EventPriority.HIGH)
    public void onPing(ProxyPingEvent event) {
        int total;

        total = LeeesBungeeQueue.priorityqueue.size() + LeeesBungeeQueue.regularqueue.size();
      if (Lang.ENABLEMOTD.contains("true")) {
          if (!Lang.ENABLERANDOMMOTD.contains("true")) {
              ServerPing serverPing = event.getResponse();
              serverPing.setDescription(Lang.MOTD.replaceAll("&", "§").replaceAll("%priosize%", "" +
                      LeeesBungeeQueue.priorityqueue.size()).replaceAll("%regsize%", "" + LeeesBungeeQueue.regularqueue.size())
                      .replaceAll("%maxplayers%", "" + Lang.MAINSERVERSLOTS).replaceAll("%total%", "" + total));
              event.setResponse(serverPing);
          } else {
              ServerPing serverPing = event.getResponse();
              serverPing.setDescriptionComponent(new TextComponent(Lang.RANDOMMOTDS.get(random.nextInt(Lang.RANDOMMOTDS.size())).replaceAll("&", "§").replaceAll("%priosize%", "" +
                      LeeesBungeeQueue.priorityqueue.size()).replaceAll("%regsize%", "" + LeeesBungeeQueue.regularqueue.size())
                      .replaceAll("%maxplayers%", "" + Lang.MAINSERVERSLOTS).replaceAll("%total%", "" + total)));
              event.setResponse(serverPing);
          }
        }
        if (Lang.SERVERPINGINFOENABLE.equals("true")) {
            if (!Lang.CUSTOMPROTOCOL.contains("false")) {
                ServerPing.Protocol provided = event.getResponse().getVersion();


                provided.setName(Lang.CUSTOMPROTOCOL.replaceAll("&", "§"));

                protocol = provided;
            } else {
                protocol = event.getResponse().getVersion();
            }
            ServerPing.PlayerInfo[] info = {};
            int i = 0;

            for (String str : Lang.SERVERPINGINFO) {
                info = addInfo(info, new ServerPing.PlayerInfo(str.replaceAll("&", "§").replaceAll("%priority%", "" + LeeesBungeeQueue.priorityqueue.size()).replaceAll("%regular%", "" + LeeesBungeeQueue.regularqueue.size()).replaceAll("%maxplayers%", String.valueOf(Lang.MAINSERVERSLOTS)).replaceAll("%totalinqueue%", String.valueOf(total)), String.valueOf(i)));
                i++;
            }
            ServerPing.Players players;

            players = new ServerPing.Players(Lang.QUEUESERVERSLOTS, LeeesBungeeQueue.getInstance().getProxy().getOnlineCount(), info);

            ServerPing ping = new ServerPing(protocol, players, event.getResponse().getDescriptionComponent(), event.getResponse().getFaviconObject());
            event.setResponse(ping);
        }
    }

    public static ServerPing.PlayerInfo[] addInfo(ServerPing.PlayerInfo[] arr, ServerPing.PlayerInfo info) {
        int i;

        ServerPing.PlayerInfo[] newarr = new ServerPing.PlayerInfo[arr.length + 1];

        for (i = 0; i < arr.length; i++)
            newarr[i] = arr[i];

        newarr[arr.length] = info;

        return newarr;
    }
}
