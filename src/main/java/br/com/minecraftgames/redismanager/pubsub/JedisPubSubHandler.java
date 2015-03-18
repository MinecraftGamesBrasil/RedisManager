package br.com.minecraftgames.redismanager.pubsub;

import br.com.minecraftgames.redismanager.RedisManager;
import net.md_5.bungee.api.ProxyServer;
import redis.clients.jedis.JedisPubSub;

/**
 * <h1>Converte mensagens do PubSub em eventos</h1>
 *
 * @author Ramon, Lucas
 */
public class JedisPubSubHandler extends JedisPubSub {

    /**
     * Recebe a mensagem do PubSub
     *
     * @param channel Canal da mensagem
     * @param message Mensagem recebida
     */
    @Override
    public void onMessage(final String channel, final String message) {
        // Cancela o processamento da função caso a mensagem esteja vazia
        if (message.trim().length() == 0)
            return;

        // Envia o evento, cria a partir da mensagem do PubSub, para o BungeeCord
        RedisManager.service.submit(new Runnable() {
            @Override
            public void run() {
                ProxyServer.getInstance().getPluginManager().callEvent(new PubSubMessageEvent(channel, message));
            }
        });
    }

    // IGNORED
    @Override
    public void onPMessage(String s, String s2, String s3) {}

    // IGNORED
    @Override
    public void onSubscribe(String s, int i) {}

    // IGNORED
    @Override
    public void onUnsubscribe(String s, int i) {}

    // IGNORED
    @Override
    public void onPUnsubscribe(String s, int i) {}

    // IGNORED
    @Override
    public void onPSubscribe(String s, int i) {}
}