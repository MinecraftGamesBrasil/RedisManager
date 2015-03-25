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

/**
 *  <h1>Trata dos eventos que precisam ser processados</h1>
 *
 *  @author Ramon, Lucas
 */
public class ProcessorListener implements Listener {

    /**
     * Evento disparado depois do login do jogador
     *
     * @param event Evento
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPostConnect(PostLoginEvent event) {
        RedisManager.processor.process(new PlayerLoggedInEvent(event.getPlayer()));
    }

    /**
     * Evento disparado após o logout do jogador
     *
     * @param event Evento
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        RedisManager.processor.process(new PlayerLoggedOffEvent(event.getPlayer()));
    }

    /**
     * Evento disparado após a troca de server
     *
     * @param event Evento
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerChange(ServerConnectedEvent event) {
        RedisManager.processor.process(new PlayerChangedServerEvent(event.getPlayer(), event.getServer().getInfo()));
    }
}