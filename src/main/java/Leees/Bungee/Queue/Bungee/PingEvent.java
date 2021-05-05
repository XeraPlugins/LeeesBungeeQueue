package Leees.Bungee.Queue.Bungee;

import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class PingEvent implements Listener {
    ServerPing.Protocol protocol;
    @EventHandler(priority = EventPriority.HIGH)
    public void onPing(ProxyPingEvent event) {
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

            int total;

            total = LeeesBungeeQueue.priorityqueue.size() + LeeesBungeeQueue.regularqueue.size();

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
