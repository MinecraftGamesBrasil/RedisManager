package br.com.minecraftgames.redismanager.processor;

import br.com.minecraftgames.redismanager.RedisManager;
import br.com.minecraftgames.redismanager.processor.events.PlayerChangedServerEvent;
import br.com.minecraftgames.redismanager.processor.events.PlayerLoggedInEvent;
import br.com.minecraftgames.redismanager.processor.events.PlayerLoggedOffEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ProcessorListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPostConnect(PostLoginEvent event) {
        RedisManager.processor.process(new PlayerLoggedInEvent(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        RedisManager.processor.process(new PlayerLoggedOffEvent(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerChange(ServerConnectedEvent event) {
        RedisManager.processor.process(new PlayerChangedServerEvent(event.getPlayer(), event.getServer().getInfo()));
    }
}