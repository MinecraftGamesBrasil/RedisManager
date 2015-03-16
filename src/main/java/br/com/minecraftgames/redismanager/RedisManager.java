package br.com.minecraftgames.redismanager;

import net.md_5.bungee.api.plugin.Plugin;

public class RedisManager extends Plugin {

    public void onEnable() {
        Redis.initialize();
        try {
            Redis.getPool().getResource().ping();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDisable() {
        
    }
}