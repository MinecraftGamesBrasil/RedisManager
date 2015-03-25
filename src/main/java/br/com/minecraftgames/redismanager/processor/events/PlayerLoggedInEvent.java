package br.com.minecraftgames.redismanager.processor.events;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * <h1>Evento de LogIn</h1>
 *
 * @author Ramon, Lucas
 */
public class PlayerLoggedInEvent implements ProcessorEvent {

    private final ProxiedPlayer player;

    /**
     * Construtor do evento
     *
     * @param player ProxiedPlayer do jogador
     */
    public PlayerLoggedInEvent(ProxiedPlayer player) {
        this.player = player;
    }

    /**
     * Retorna o jogador do evento
     *
     * @return ProxiedPlayer do jogador
     */
    public ProxiedPlayer getPlayer() {
        return player;
    }
}