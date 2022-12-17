package Leees.Bungee.Queue.Bungee;

import java.net.Socket;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import static Leees.Bungee.Queue.Bungee.Lang.AFTERQUEUE;

/**
 * Events
 */
public class Events implements Listener {

    public List<UUID> regular = new ArrayList<>();
    public List<UUID> priority = new ArrayList<>();
    ServerInfo queue = ProxyServer.getInstance().getServerInfo(Lang.QUEUESERVER);
    public static boolean mainonline = false;
    public static boolean queueonline = false;
    public static boolean authonline = false;
    private net.md_5.bungee.api.plugin.Plugin plugin;


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
        if (Lang.ANTIBOT.contains("true")) {
            if (event.getPlayer().hasPermission("antivpn.bypass")) {
                if (!Lang.ENABLEAUTHSERVER.contains("true")) {
                    if (mainonline && queueonline) {
                        if (!Lang.ALWAYSQUEUE.contains("true")) {
                            if (ProxyServer.getInstance().getOnlineCount() <= Lang.MAINSERVERSLOTS)
                                return;
                            addplayertoqueue(event.getPlayer().getUniqueId(), AFTERQUEUE, event.getPlayer());
                        } else {
                            addplayertoqueue(event.getPlayer().getUniqueId(), AFTERQUEUE, event.getPlayer());
                        }
                    } else {
                        event.getPlayer().disconnect(Lang.SERVERDOWNKICKMESSAGE.replace("&", "§"));
                    }
                } else {
                    if (mainonline && queueonline && authonline) {
                        if (!Lang.ALWAYSQUEUE.contains("true")) {
                            addplayertoqueue(event.getPlayer().getUniqueId(), AFTERQUEUE, event.getPlayer());
                        } else {
                            addplayertoqueue(event.getPlayer().getUniqueId(), AFTERQUEUE, event.getPlayer());
                        }
                    } else {
                        if (Lang.CONNECTTOQUEUEWHENDOWN.contains("true")) {
                            addplayertoqueue(event.getPlayer().getUniqueId(), AFTERQUEUE, event.getPlayer());
                        } else {
                            event.getPlayer().disconnect(Lang.SERVERDOWNKICKMESSAGE.replace("&", "§"));
                        }
                    }
                }
            } else {
                event.getPlayer().disconnect(Lang.ANTIBOTMESSAGE.replace("&", "§"));
            }
        } else if(Lang.ANTIBOT.contains("false")) {
            if (!Lang.ENABLEAUTHSERVER.contains("true")) {
                if (mainonline && queueonline) {
                    if (!Lang.ALWAYSQUEUE.contains("true")) {
                        if (ProxyServer.getInstance().getOnlineCount() <= Lang.MAINSERVERSLOTS)
                            return;
                        addplayertoqueue(event.getPlayer().getUniqueId(), AFTERQUEUE, event.getPlayer());
                    } else {
                        addplayertoqueue(event.getPlayer().getUniqueId(), AFTERQUEUE, event.getPlayer());
                    }
                } else {
                    event.getPlayer().disconnect(Lang.SERVERDOWNKICKMESSAGE.replace("&", "§"));
                }
            } else {
                if (mainonline && queueonline && authonline) {
                    if (!Lang.ALWAYSQUEUE.contains("true")) {
                        addplayertoqueue(event.getPlayer().getUniqueId(), AFTERQUEUE, event.getPlayer());
                    } else {
                        addplayertoqueue(event.getPlayer().getUniqueId(), AFTERQUEUE, event.getPlayer());
                    }
                } else {
                    if (Lang.CONNECTTOQUEUEWHENDOWN.contains("true")) {
                        addplayertoqueue(event.getPlayer().getUniqueId(), AFTERQUEUE, event.getPlayer());
                    } else {
                        event.getPlayer().disconnect(Lang.SERVERDOWNKICKMESSAGE.replace("&", "§"));
                    }
                }
            }
        }
    }
    public void addplayertoqueue(UUID playeruuid, String originaltarget, ProxiedPlayer playername) {
        if (playername.hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
            playername.sendMessage(Lang.SERVERISFULLMESSAGE.replace('&', '§'));
            playername.setTabHeader(
                    new ComponentBuilder(Lang.HEADERPRIORITY.replace("&", "§").replace("<position>", "None").replace("<wait>", "None")).create(),
                    new ComponentBuilder(Lang.FOOTERPRIORITY.replace("&", "§").replace("<position>", "None").replace("<wait>", "None")).create());
                    LeeesBungeeQueue.getInstance().getPriorityqueue().put(playeruuid, originaltarget);
        } else {
            playername.sendMessage(Lang.SERVERISFULLMESSAGE.replace('&', '§'));
            playername.setTabHeader(
                    new ComponentBuilder(Lang.HEADER.replace("&", "§").replace("<position>", "None").replace("<wait>", "None")).create(),
                    new ComponentBuilder(Lang.FOOTER.replace("&", "§").replace("<position>", "None").replace("<wait>", "None")).create());
            LeeesBungeeQueue.getInstance().getRegularqueue().put(playeruuid, originaltarget);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void ontick(ServerConnectedEvent event) {
        if (Lang.ANTIBOT.contains("true")) {
            if (event.getPlayer().hasPermission("antivpn.bypass")) {
                if (Lang.ALWAYSQUEUE.contains("true")) {
                    if (event.getServer().getInfo().getName().equals(Lang.QUEUESERVER)) {
                        if (event.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                            if (!LeeesBungeeQueue.getInstance().getPriorityqueue().containsKey(event.getPlayer().getUniqueId())) {
                                priority.add(event.getPlayer().getUniqueId());
                            }
                        } else {
                            if (!LeeesBungeeQueue.getInstance().getRegularqueue().containsKey(event.getPlayer().getUniqueId())) {
                                regular.add(event.getPlayer().getUniqueId());
                            }
                        }
                    }
                }
            }
        } else if(Lang.ANTIBOT.contains("false")) {
            if (Lang.ALWAYSQUEUE.contains("true")) {
                if (event.getServer().getInfo().getName().equals(Lang.QUEUESERVER)) {
                    if (event.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                        if (!LeeesBungeeQueue.getInstance().getPriorityqueue().containsKey(event.getPlayer().getUniqueId())) {
                            priority.add(event.getPlayer().getUniqueId());
                        }
                    } else {
                        if (!LeeesBungeeQueue.getInstance().getRegularqueue().containsKey(event.getPlayer().getUniqueId())) {
                            regular.add(event.getPlayer().getUniqueId());
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisconnect(PlayerDisconnectEvent e) {
        if (Lang.ANTIBOT.contains("true")) {
            if (e.getPlayer().hasPermission("antivpn.bypass")) {
                if (e.getPlayer().getServer().getInfo().getName().equalsIgnoreCase(Lang.QUEUESERVER)) {
                    if (!e.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                        LeeesBungeeQueue.getInstance().getRegularqueue().remove(e.getPlayer().getUniqueId());
                        regular.remove(e.getPlayer().getUniqueId());
                    } else {
                        LeeesBungeeQueue.getInstance().getPriorityqueue().remove(e.getPlayer().getUniqueId());
                        priority.remove(e.getPlayer().getUniqueId());
                    }
                }
            }
        } else if(Lang.ANTIBOT.contains("false")) {
            if (e.getPlayer().hasPermission("antivpn.bypass")) {
                if (e.getPlayer().getServer().getInfo().getName().equalsIgnoreCase(Lang.QUEUESERVER)) {
                    if (!e.getPlayer().hasPermission(Lang.QUEUEPRIORITYPERMISSION)) {
                        LeeesBungeeQueue.getInstance().getRegularqueue().remove(e.getPlayer().getUniqueId());
                        regular.remove(e.getPlayer().getUniqueId());
                    } else {
                        LeeesBungeeQueue.getInstance().getPriorityqueue().remove(e.getPlayer().getUniqueId());
                        priority.remove(e.getPlayer().getUniqueId());
                    }
                }
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
        try {
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
                        } else if (!LeeesBungeeQueue.getInstance().getRegularqueue().isEmpty()) {
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
        } catch (Throwable throwable) {
            LeeesBungeeQueue.getInstance().getRegularqueue().clear();
            LeeesBungeeQueue.getInstance().getPriorityqueue().clear();
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
