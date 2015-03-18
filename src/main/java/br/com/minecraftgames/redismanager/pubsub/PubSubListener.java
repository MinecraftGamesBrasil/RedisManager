package br.com.minecraftgames.redismanager.pubsub;

import br.com.minecraftgames.redismanager.Redis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class PubSubListener implements Runnable {

    private Jedis rsc;
    private JedisPubSubHandler jpsh;

    @Override
    public void run() {
        try {
            rsc = Redis.getResource();
            jpsh = new JedisPubSubHandler();
            rsc.subscribe(jpsh, "sendtochannel", "sendtoplayer", "kick", "ban", "mute", "unban", "unmute", "turnchat",
                    "turntell", "turnquote", "whitelistedlobby", "tell", "send", "vote", "updatesidebar", "reloadgroup",
                    "reloadam", "reloadexecutors", "reloadconfig", "warnquote");
        } catch (JedisException | ClassCastException ignored) {}
    }

    public void addChannel(String... channel) {
        jpsh.subscribe(channel);
    }

    public void removeChannel(String... channel) {
        jpsh.unsubscribe(channel);
    }

}