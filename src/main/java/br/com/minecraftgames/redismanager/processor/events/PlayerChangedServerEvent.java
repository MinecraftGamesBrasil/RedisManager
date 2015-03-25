package br.com.minecraftgames.redismanager.processor.events;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * <h1>Evento de ChangeServer</h1>
 *
 * @author Ramon, Lucas
 */
public class PlayerChangedServerEvent implements ProcessorEvent {

    private final ProxiedPlayer player;
    private final ServerInfo newServer;

    /**
     * Construtor do evento
     *
     * @param player ProxiedPlayer do jogador
     * @param newServer Servidor, do BungeeCord, destino
     */
    public PlayerChangedServerEvent(ProxiedPlayer player, ServerInfo newServer) {
        this.player = player;
        this.newServer = newServer;
    }

    /**
     * Retorna o servidor destino do jogador
     *
     * @return Servidor, do BungeeCord, destino
     */
    public ServerInfo getNewServer() {
        return newServer;
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