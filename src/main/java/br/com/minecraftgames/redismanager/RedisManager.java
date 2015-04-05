package br.com.minecraftgames.redismanager;

import br.com.minecraftgames.redismanager.processor.ProcessorListener;
import br.com.minecraftgames.redismanager.processor.ProcessorManager;
import br.com.minecraftgames.redismanager.pubsub.PubSubListener;
import br.com.minecraftgames.redismanager.utils.Instances;
import br.com.minecraftgames.redismanager.utils.PlayerData;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
//Teste
    public static RedisManager plugin;

    public static RedisConfiguration configuration;
    public static ProcessorManager processor;
    public static RedisManagerAPI api;
    public static ExecutorService service;
    public static PubSubListener psl;

    public static boolean allowConnections;

    /**
     * Funções realizadas ao iniciar o servidor
     */
    public void onEnable() {
        plugin = this;

        // Inicia conexão com o Redis
        Redis.initialize();

        // Limpa os dados atuais da instância, armazenados no Redis
        Instances.cleanUpInstance();

        // Inicia todos as variáveis relativas ao Redis
        configuration = new RedisConfiguration(this);

        // Inicia o processador de dados para o Redis
        processor = new ProcessorManager(this);

        // Inicia a API
        api = new RedisManagerAPI(this);

        // Inicia o PubSubListener
        psl = new PubSubListener();

        // Inicia o ExecutorService em outro thread
        getProxy().getScheduler().runAsync(this, new Runnable() {
            @Override
            public void run() {
                service = Executors.newFixedThreadPool(16);
                service.submit(psl);
            }
        });

        // Registra os Listeners
        getProxy().getPluginManager().registerListener(this, new RedisListener(this));
        getProxy().getPluginManager().registerListener(this, new ProcessorListener());

        // Libera a instância para receber conexões de jogadores
        allowConnections = true;
    }

    /**
     * Funções realizadas quando o servidor fechar
     */
    public void onDisable() {
        // Bloqueia a conexão de jogadores
        allowConnections = false;

        // Removendo os dados dos jogadores online
        for(ProxiedPlayer player : getProxy().getPlayers()) {
            PlayerData.gracefulLogout(player);
        }

        // Remove o listener
        getProxy().getPluginManager().unregisterListeners(this);

        // Desliga todas as tasks
        getProxy().getScheduler().cancel(this);

        // Finaliza o ExecutorService
        service.shutdownNow();

        // Desliga a conexão com Redis
        Redis.getPool().destroy();
    }

    /**
     * Converte uma mensagem(String) normal no formato de mensagens do BungeeCord(ChatComponent)
     *
     * @param message Mensagem(String)
     * @return Mensagem(ChatComponent)
     */
    public static BaseComponent[] convert(String message) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message.replaceAll("(&)\\1{1,}", "$1")));
    }

    /**
     * Retorna o ExecutorService, sobrepondo o default do bungee
     *
     * @return ExecutorService
     */
    public ExecutorService getService() {
        return service;
    }
}