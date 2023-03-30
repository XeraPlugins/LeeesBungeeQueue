# LeeesBungeeQueue

LeeesBungeeQueue is a 2b2t like queue plugin for Bungeecord
very easy to configure just make sure you have these things on bungeecord
or waterfall set properly below 

NOTE: the plugin has no velocity support but works perfectly with snap on velocity i myself use velocity for my server and can testify this will work with snap perfectly fine
https://forums.papermc.io/threads/snap-run-bungeecord-plugins-on-velocity.31/

NOTE: if you need a plugin to make a queue server you can place this same jar file in any 1.12.2 paper / spigot server and itll act as a plugin to prevent player movement, set gamemode, set world and exact specific coordinates you want all the players to be stuck at do note that the max version that part of the plugin supports is 1.12.2 so make sure your queue server is 1.12.2 and you can add 1.13 + support with via version

NOTE: it is recommended to use Waterfall and not bone stock bungeecord with this plugin (waterfall forks work fine) if you have an issue with a specific waterfall fork please let me know and ill see whats going on otherwise please use regular waterfall https://papermc.io/downloads#Waterfall

NOTE: if your cracked heres the link to the modified version of AuthMeBungee addon: https://www.8b8t.me/files/AuthMeBungee-LBQ.jar

If you would like to donate to keep me motivated to keep this project up to date you can donate at: https://donate.8b8t.me or my paypal email is nate2@xera.ca

```

Waterfall / Bungeecord config.yml ->

please note NEVER use the plugin
with main or auth in priorities
make sure the only server in priorities is the queue server
EXAMPLE:
 priorities:
 - queue

also make sure this is set to true
force_default_server: true

at the bottom of your bungeecord / waterfall config
add the queue main and auth(only if your server is offline mode / cracked)
like this example below:
servers:
  8b8t:
    motd: '&18b&98t &6Main Server'
    address: localhost:8401
    restricted: true
  queue:
    motd: '&18b&98t &6Queue Server'
    address: localhost:8402
    restricted: true
  auth:
    motd: '&18b&98t &6Auth Server'
    address: localhost:8403
    restricted: true




LeeesBungeeQueue config.yml ->

#Antibot works by checking
#if player has permission
#antivpn.bypass use this plugin:
#to setup a antibot verification website:
#https://github.com/Leeewith3Es/LeeesWhitelistPlugin
ANTIBOT: "true" #Disable this if you have your own antibot
ANTIBOTMESSAGE: "&6[&18builders&98tools&6]\n\nTo play 8b8t verify your username at &chttps://antibot.8b8t.me\n&6Para jugar a 8b8t verifica tu nombre de usuario en &chttps://antibot.8b8t.me"

How to configure LBQ's config properly
make sure you set the names of the servers
in lbq to the exact name of the ones you set
in the bungeecord config

QUEUESERVER: "queue"
MAINSERVER: "8b8t"
#set this to false if your a premium server
ENABLEAUTHSERVER: "true"
AUTHSERVER: "auth"
#server to send the players to after queue
AFTERQUEUE: "auth"

```
need extra help ? watch the youtube video: https://youtu.be/mJN5vZjhqwI
