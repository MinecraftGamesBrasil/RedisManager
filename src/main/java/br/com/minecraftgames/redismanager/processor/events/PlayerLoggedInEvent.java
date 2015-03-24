package br.com.minecraftgames.redismanager.processor.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PlayerLoggedInEvent implements ProcessorEvent {

    private final ProxiedPlayer player;

    public PlayerLoggedInEvent(ProxiedPlayer player) {
        this.player = player;
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }
}