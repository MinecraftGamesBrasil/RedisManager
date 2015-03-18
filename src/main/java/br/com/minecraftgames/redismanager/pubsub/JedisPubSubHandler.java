package br.com.minecraftgames.redismanager.pubsub;

import br.com.minecraftgames.redismanager.RedisManager;
import net.md_5.bungee.api.ProxyServer;
import redis.clients.jedis.JedisPubSub;

public class JedisPubSubHandler extends JedisPubSub {

    @Override
    public void onMessage(final String s, final String s2) {
        if (s2.trim().length() == 0)
            return;
        RedisManager.service.submit(new Runnable() {
            @Override
            public void run() {
                ProxyServer.getInstance().getPluginManager().callEvent(new PubSubMessageEvent(s, s2));
            }
        });
    }

    @Override
    public void onPMessage(String s, String s2, String s3) {}

    @Override
    public void onSubscribe(String s, int i) {}

    @Override
    public void onUnsubscribe(String s, int i) {}

    @Override
    public void onPUnsubscribe(String s, int i) {}

    @Override
    public void onPSubscribe(String s, int i) {}
}