package Leees.Bungee.Queue.Bungee;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import org.bukkit.entity.Player;

/**
 * Events
 */
public class Events implements Listener {

    List<UUID> regular = new ArrayList<>();
    List<UUID> priority = new ArrayList<>();
    ServerInfo queue = ProxyServer.getInstance().getServerInfo(Lang.QUEUESERVER);
    public static boolean mainonline = false;
    public static boolean queueonline = false;
    public static boolean authonline = false;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreLogin(PreLoginEvent ple) {
        if (!ple.getConnection().getName().matches(Lang.REGEX)) {
            ple.setCancelReason(new TextComponent(ChatColor.GOLD + "[LBQ] Invalid username please use: " + Lang.REGEX));
            ple.setCancelled(true);
        }
    }

    public static void CheckIfMainServerIsOnline() {
        try {
            Socket s = new Socket(ProxyServer.getInstance().getServerInfo(Lang.MAINSERVER).getAddress().getAddress(), ProxyServer.getInstance().getServerInfo(Lang.MAINSERVER).getAddress().getPort());
            // ONLINE
            s.close();
            mainonline = true;
        } catch (Throwable t) {
            mainonline = false;
            System.out.println(ChatColor.translateAlternateColorCodes('&', "&6[LBQ] Your main server is offline please check your config"));
        }
    }

    public static void CheckIfQueueServerIsOnline() {
        try {
            Socket s = new Socket(ProxyServer.getInstance().getServerInfo(Lang.QUEUESERVER).getAddress().getAddress(), ProxyServer.getInstance().getServerInfo(Lang.QUEUESERVER).getAddress().getPort());
            // ONLINE
            s.close();
            queueonline = true;
        } catch (Throwable t) {
            queueonline = false;
            System.out.println(ChatColor.translateAlternateColorCodes('&', "&6[LBQ] Your queue server is offline please check your config"));
        }
    }

    public static void CheckIfAuthServerIsOnline() {
        if (Lang.ENABLEAUTHSERVER.contains("true")) {
            try {
                Socket s = new Socket(ProxyServer.getInstance().getServerInfo(Lang.AUTHSERVER).getAddress().getAddress(), ProxyServer.getInstance().getServerInfo(Lang.AUTHSERVER).getAddress().getPort());
                // ONLINE
                s.close();
                authonline = true;
            } catch (Throwable t) {
                authonline = false;
                System.out.println(ChatColor.translateAlternateColorCodes('&', "&6[LBQ] Your auth server is offline please check your config"));
            }
        } else {
            authonline = true;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PostLoginEvent event) {
        if (!Lang.ENABLEAUTHSERVER.contains("true")) {
            if (mainonline && queueonline) {
                if (!Lang.ALWAYSQUEUE.contains("true")) {
                    if (ProxyServer.getInstance().getOnlineCount() <= Lang.MAINSERVERSLOTS)
                        return;
                    if (event.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                        // Send the priority player to the priority queue
                        priority.add(event.getPlayer().getUniqueId());
                        return;
                    }
                    if (!event.getPlayer().hasPermission(Lang.QUEUEBYPASSPERMISSION) && !event.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                        // Send the player to the regular queue
                        regular.add(event.getPlayer().getUniqueId());
                        return;
                    }
                } else {
                    if (event.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                        // Send the priority player to the priority queue
                        priority.add(event.getPlayer().getUniqueId());
                    }
                    if (!event.getPlayer().hasPermission(Lang.QUEUEBYPASSPERMISSION) && !event.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                        // Send the player to the regular queue
                        regular.add(event.getPlayer().getUniqueId());
                    }
                }
            } else {
                event.getPlayer().disconnect(Lang.SERVERDOWNKICKMESSAGE.replace("&", "§"));
            }
        } else {
            if (mainonline && queueonline && authonline) {
                if (!Lang.ALWAYSQUEUE.contains("true")) {
                    if (ProxyServer.getInstance().getOnlineCount() <= Lang.MAINSERVERSLOTS)
                        return;
                    if (event.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                        // Send the priority player to the priority queue
                        priority.add(event.getPlayer().getUniqueId());
                        return;
                    }
                    if (!event.getPlayer().hasPermission(Lang.QUEUEBYPASSPERMISSION) && !event.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                        // Send the player to the regular queue
                        regular.add(event.getPlayer().getUniqueId());
                        return;
                    }
                } else {
                    if (event.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                        // Send the priority player to the priority queue
                        priority.add(event.getPlayer().getUniqueId());
                    }
                    if (!event.getPlayer().hasPermission(Lang.QUEUEBYPASSPERMISSION) && !event.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                        // Send the player to the regular queue
                        regular.add(event.getPlayer().getUniqueId());
                    }
                }
            } else {
                if (Lang.CONNECTTOQUEUEWHENDOWN.contains("true")) {
                    if (event.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                        priority.add(event.getPlayer().getUniqueId());
                    } else {
                        regular.add(event.getPlayer().getUniqueId());
                    }
                } else {
                    event.getPlayer().disconnect(Lang.SERVERDOWNKICKMESSAGE.replace("&", "§"));
                }

            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSend(ServerConnectEvent e) {
        ProxiedPlayer player = e.getPlayer();
        if (player.hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
            if (!priority.contains(player.getUniqueId()))
                return;
            priority.remove(player.getUniqueId());
            // Send the player to the queue and send a message.
            String originalTarget = Lang.AFTERQUEUE;
            e.setTarget(queue);
            player.setTabHeader(
                    new ComponentBuilder(Lang.HEADERPRIORITY.replace("&", "§").replace("<position>", "None").replace("<wait>", "None")).create(),
                    new ComponentBuilder(Lang.FOOTERPRIORITY.replace("&", "§").replace("<position>", "None").replace("<wait>", "None")).create());
            player.sendMessage(ChatColor.GOLD + Lang.SERVERISFULLMESSAGE.replace("&", "§"));
            // Store the data concerning the player's destination
            LeeesBungeeQueue.getInstance().getPriorityqueue().put(player.getUniqueId(), originalTarget);
        } else if (!e.getPlayer().hasPermission(Lang.QUEUEBYPASSPERMISSION) && !e.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
            if (!regular.contains(player.getUniqueId()))
                return;
            regular.remove(player.getUniqueId());
            // Send the player to the queue and send a message.
            String originalTarget = Lang.AFTERQUEUE;
            e.setTarget(queue);
            player.setTabHeader(
                    new ComponentBuilder(Lang.HEADER.replace("&", "§").replace("<position>", "None").replace("<wait>", "None")).create(),
                    new ComponentBuilder(Lang.FOOTER.replace("&", "§").replace("<position>", "None").replace("<wait>", "None")).create());
            player.sendMessage(ChatColor.GOLD + Lang.SERVERISFULLMESSAGE.replace("&", "§"));
            // Store the data concerning the player's destination
            LeeesBungeeQueue.getInstance().getRegularqueue().put(player.getUniqueId(), originalTarget);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisconnect(PlayerDisconnectEvent e) {
        if (e.getPlayer().getServer().getInfo().getName().equalsIgnoreCase(Lang.QUEUESERVER)) {
            if (!e.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                e.getPlayer().setReconnectServer(ProxyServer.getInstance().getServerInfo(LeeesBungeeQueue.getInstance().getRegularqueue().get(e.getPlayer().getUniqueId())));
                LeeesBungeeQueue.getInstance().getRegularqueue().remove(e.getPlayer().getUniqueId());
                regular.remove(e.getPlayer().getUniqueId());
            } else {
                e.getPlayer().setReconnectServer(ProxyServer.getInstance().getServerInfo(LeeesBungeeQueue.getInstance().getPriorityqueue().get(e.getPlayer().getUniqueId())));
                LeeesBungeeQueue.getInstance().getPriorityqueue().remove(e.getPlayer().getUniqueId());
                priority.remove(e.getPlayer().getUniqueId());
            }
        }
    }

    public static void moveQueue() {
        //checks if priority queue is empty if it is a non priority user always gets in
        //if it has people in it then it gives a chance for either a priority or non
        //priority user to get in when someone logs off the main server
        //gets a random number then if the number is less then or equal to the odds set in
        //this bungeeconfig.yml it will add a priority player if its anything above the odds then
        //a non priority player gets added to the main server
        if (Events.authonline && Events.mainonline && Events.queueonline) {
            if (!LeeesBungeeQueue.getInstance().getPriorityqueue().isEmpty()) {
                if (Lang.MAINSERVERSLOTS <= ProxyServer.getInstance().getOnlineCount() - LeeesBungeeQueue.getInstance().getRegularqueue().size() - LeeesBungeeQueue.getInstance().getPriorityqueue().size())
                    return;
                if (LeeesBungeeQueue.getInstance().getPriorityqueue().isEmpty())
                    return;
                Entry<UUID, String> entry2 = LeeesBungeeQueue.getInstance().getPriorityqueue().entrySet().iterator().next();
                ProxiedPlayer player2 = ProxyServer.getInstance().getPlayer(entry2.getKey());
                player2.connect(ProxyServer.getInstance().getServerInfo(entry2.getValue()));
                player2.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(Lang.JOININGMAINSERVER.replace("&", "§").replace("<server>", entry2.getValue())));
                LeeesBungeeQueue.getInstance().getPriorityqueue().remove(entry2.getKey());
            } else {
                if (Lang.MAINSERVERSLOTS <= ProxyServer.getInstance().getOnlineCount() - LeeesBungeeQueue.getInstance().getRegularqueue().size() - LeeesBungeeQueue.getInstance().getPriorityqueue().size())
                    return;
                if (LeeesBungeeQueue.getInstance().getRegularqueue().isEmpty())
                    return;
                Entry<UUID, String> entry = LeeesBungeeQueue.getInstance().getRegularqueue().entrySet().iterator().next();
                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(entry.getKey());
                player.connect(ProxyServer.getInstance().getServerInfo(entry.getValue()));
                player.sendMessage(ChatMessageType.CHAT, TextComponent.fromLegacyText(Lang.JOININGMAINSERVER.replace("&", "§").replace("<server>", entry.getValue())));
                LeeesBungeeQueue.getInstance().getRegularqueue().remove(entry.getKey());
            }
        }
        }
    //}

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onkick(ServerKickEvent event) {
        if (Lang.ENABLEKICKMESSAGE.equals("true")) {
            event.setKickReason(Lang.KICKMESSAGE.replace("&", "§"));
        }
    }
}
