package br.com.minecraftgames.redismanager;

import br.com.minecraftgames.redismanager.pubsub.PubSubMessageEvent;
import br.com.minecraftgames.redismanager.utils.ServerData;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class RedisListener implements Listener {

    private RedisManager plugin;

    public RedisListener(RedisManager instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent event) {
        String channel = event.getChannel();
        String message = event.getMessage();
        String[] args = message.split("%=%");
        if(channel.equals("whitelistlobby")) {
            String lobby = args[0];
            String action = args[1].toLowerCase();
            if(action.equals("add"))
                ServerData.addLobbyToWhitelist(lobby);
            else
                ServerData.removeLobbyFromWhitelist(lobby);
        } else if(channel.equals("turnchat")) {
            String action = args[0].toLowerCase();
            ServerData.setChatState(action);
        } else if(channel.equals("turntell")) {
            String name = args[0];
            String action = args[1].toLowerCase();
            if(action.equals("off"))
                ServerData.addTellOff(name);
            else
                ServerData.removeTellOff(name);
        } else if(channel.equals("turnquote")) {
            String name = args[0];
            String action = args[1].toLowerCase();
            if(action.equals("off"))
                ServerData.addQuoteOff(name);
            else
                ServerData.removeQuoteOff(name);
        } else if(channel.equals("message")) {
            String name = args[0];
            String msg = args[1];
            if(plugin.getProxy().getPlayer(name) != null) {
                ProxiedPlayer player = plugin.getProxy().getPlayer(name);
                player.sendMessage(RedisManager.convert(msg));
            }
        } else if(channel.equals("send")) {
            String name = args[0];
            String target = args[1];
            if(plugin.getProxy().getPlayer(name) != null) {
                ProxiedPlayer player = plugin.getProxy().getPlayer(name);
                if(player.getServer().getInfo().getName().equalsIgnoreCase(target))
                    return;
                player.connect(plugin.getProxy().getServerInfo(target));
                if(args.length >= 3) {
                    if(args[2].equalsIgnoreCase("true")) {
                        player.sendMessage(RedisManager.convert("&aVocê foi teleportado para o servidor " + target));
                    }
                }
            }
        }
    }

}