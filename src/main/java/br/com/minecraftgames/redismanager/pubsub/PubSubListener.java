package br.com.minecraftgames.redismanager.pubsub;

import br.com.minecraftgames.redismanager.Redis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

/**
 * <h1>Registra o controlador das mensagens do PubSub</h1>
 *
 * @author Ramon, Lucas
 */
public class PubSubListener implements Runnable {

    private Jedis rsc;
    private JedisPubSubHandler jpsh;

    /**
     * Registras os canais e inicia o controlador
     */
    @Override
    public void run() {
        try {
            rsc = Redis.getResource();

            // Cria o conversor de mensagens PubSub para eventos
            jpsh = new JedisPubSubHandler();

            // Registras os canais que o PubSub vai utilizar
            rsc.subscribe(jpsh, "sendtochannel", "sendtoplayer", "kick", "ban", "mute", "unban", "unmute", "changeglobalchatstate",
                    "whitelistedlobby", "tell", "send", "vote", "updatesidebar", "reloadgroup",
                    "reloadam", "reloadexecutors", "reloadconfig", "warnquote");
        } catch (JedisException | ClassCastException ignored) {}
    }

    /**
     * Adciona um ou mais canais no PubSub
     *
     * @param channel Canal(is) a serem adcionados
     */
    public void addChannel(String... channel) {
        jpsh.subscribe(channel);
    }

    /**
     * Remove um ou mais canais do PubSub
     *
     * @param channel Canal(is) a serem removidos
     */
    public void removeChannel(String... channel) {
        jpsh.unsubscribe(channel);
    }

}