package br.com.minecraftgames.redismanager.pubsub;

import net.md_5.bungee.api.plugin.Event;

/**
 * <h1>Evento das mensagens do PubSub</h1>
 *
 * @author Ramon, Lucas
 */
public class PubSubMessageEvent extends Event {

    private final String channel;
    private final String message;

    /**
     * Construtor da classe
     *
     * @param channel Canal da mensagem
     * @param message Mensagem
     */
    public PubSubMessageEvent(String channel, String message) {
        this.channel = channel;
        this.message = message;
    }

    /**
     * Retorna o canal da mensagem
     *
     * @return Canal da mensagem
     */
    public String getChannel() {
        return channel;
    }

    /**
     * Retorna a mensagem
     *
     * @return Mensagem
     */
    public String getMessage() {
        return message;
    }

    /**
     * Converte o evento no formato de uma string
     *
     * @return PubSubMessage (channel: "{@code channel}", message: "{@code message}")
     */
    public String toString() {
        return "PubSubMessage (channel: " + channel + ", message: " + message + ")";
    }
}