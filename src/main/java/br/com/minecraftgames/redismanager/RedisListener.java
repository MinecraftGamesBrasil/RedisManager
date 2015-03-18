package br.com.minecraftgames.redismanager;

import net.md_5.bungee.api.plugin.Listener;

public class RedisListener implements Listener {

    private RedisManager plugin;

    public RedisListener(RedisManager instance) {
        this.plugin = instance;
    }

}