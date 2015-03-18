package br.com.minecraftgames.redismanager;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * <h1>Controla todas as funções relativas ao Redis</h1>
 * O RedisManager funciona em conjunto com o ProxyManager
 * e juntos, controlam a instância do BungeeCord do MinecraftGames
 *
 * @author Ramon, Lucas
 */
public class RedisManager extends Plugin {

    public static RedisManager plugin;

    public RedisConfiguration configuration;

    public void onEnable() {
        plugin = this;

        // Inicia conexão com o Redis
        Redis.initialize();

        // Registra o canal do plugin no Bungee
        getProxy().registerChannel("RedisManager");

        // Inicia todos as variáveis relativas ao Redis
        configuration = new RedisConfiguration(this);
    }

    public void onDisable() {

    }
}