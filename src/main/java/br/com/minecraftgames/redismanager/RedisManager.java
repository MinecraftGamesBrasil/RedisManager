package br.com.minecraftgames.redismanager;

import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public RedisManagerAPI api;
    public ExecutorService service;
    public PubSubListener psl;

    public void onEnable() {
        plugin = this;

        // Inicia conexão com o Redis
        Redis.initialize();

        // Inicia todos as variáveis relativas ao Redis
        configuration = new RedisConfiguration(this);

        // Inicia a API
        api = new RedisManagerAPI(this);

        // Inicia o PubSubListener
        psl = new PubSubListener();

        // Inicia o ExecutorService em outro thread
        getProxy().getScheduler().runAsync(this, new Runnable() {
            @Override
            public void run() {
                service = Executors.newFixedThreadPool(16);
            }
        });

        // Registra o canal do plugin no Bungee
        getProxy().registerChannel("RedisManager");
    }

    public void onDisable() {

    }
}