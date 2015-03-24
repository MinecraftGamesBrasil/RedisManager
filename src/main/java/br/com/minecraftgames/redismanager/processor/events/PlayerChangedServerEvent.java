package br.com.minecraftgames.redismanager.processor.events;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerChangedServerEvent implements ProcessorEvent {

    private final ProxiedPlayer player;
    private final ServerInfo newServer;

    public PlayerChangedServerEvent(ProxiedPlayer player, ServerInfo newServer) {
        this.player = player;
        this.newServer = newServer;
    }

    public ServerInfo getNewServer() {
        return newServer;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }
}