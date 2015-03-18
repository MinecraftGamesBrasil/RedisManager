package br.com.minecraftgames.redismanager.pubsub;

import net.md_5.bungee.api.plugin.Event;

public class PubSubMessageEvent extends Event {

    private final String channel;
    private final String message;

    public PubSubMessageEvent(String channel, String message) {
        this.channel = channel;
        this.message = message;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return "PubSubMessage (channel: " + channel + ", message: " + message + ")";
    }
}