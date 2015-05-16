package br.com.minecraftgames.redismanager.processor;

import br.com.minecraftgames.redismanager.Redis;
import br.com.minecraftgames.redismanager.RedisConfiguration;
import br.com.minecraftgames.redismanager.RedisManager;
import br.com.minecraftgames.redismanager.processor.events.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.net.InetAddress;
import java.util.UUID;

/**
 * <h1>Processa a entrada de dados no Redis</h1>
 *
 * @author Ramon, Lucas
 */
public class ProcessorManager {

    private RedisManager plugin;

    /**
     * Construtor do processador
     *
     * @param instance Instância da main class do plugin
     */
    public ProcessorManager(RedisManager instance) {
        this.plugin = instance;
    }

    /**
     * Processa um evento
     *
     * @param processorEvent Evento
     */
    public void process(ProcessorEvent processorEvent) {
        Runnable processableEvent = null;

        // Caso o evento seja do tipo LogIn
        if(processorEvent instanceof PlayerLoggedInEvent) {
            final PlayerLoggedInEvent event = (PlayerLoggedInEvent) processorEvent;
            processableEvent = new Runnable() {
                public void run() {
                    ProxiedPlayer player = event.getPlayer();
                    processLogIn(player);
                }
            };
        }

        // Caso o evento seja do tipo LogOff
        else if(processorEvent instanceof PlayerLoggedOffEvent) {
            final PlayerLoggedOffEvent event = (PlayerLoggedOffEvent) processorEvent;
            processableEvent = new Runnable() {
                public void run() {
                    ProxiedPlayer player = event.getPlayer();
                    processLogOff(player);
                }
            };
        }

        // Caso o evento seja do tipo ChangeServer
        else if(processorEvent instanceof PlayerChangedServerEvent) {
            final PlayerChangedServerEvent event = (PlayerChangedServerEvent) processorEvent;
            processableEvent = new Runnable() {
                public void run() {
                    ProxiedPlayer player = event.getPlayer();
                    ServerInfo server = event.getNewServer();
                    processChangeServer(player, server);
                }
            };
        }

        // Caso o evento seja do tipo TellAndQuoteReceived
        else if(processorEvent instanceof PlayerTellAndQuoteStatusReceived) {
            final PlayerTellAndQuoteStatusReceived event = (PlayerTellAndQuoteStatusReceived) processorEvent;
            processableEvent = new Runnable() {
                public void run() {
                    UUID uuid = event.getUUID();
                    boolean isTellOn = event.isTellOn();
                    boolean isQuoteOn = event.isQuoteOn();
                    processSavePlayerTellAndQuote(uuid, isTellOn, isQuoteOn);
                }
            };
        }

        // Passa a ação para o ExecutorService
        plugin.getService().submit(processableEvent);
    }

    /**
     * Processa o evento de LogIn
     *
     * @param player ProxiedPlayer do jogador que entrou
     */
    private void processLogIn(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        InetAddress address = player.getAddress().getAddress();
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            // Adiciona o jogador a lista de jogadores online (UUID)
            rsc.sadd("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnlineUUID", uuid.toString());
            // Adiciona o jogador a lista de jogadores online (Name)
            rsc.hset("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnlineName", name, uuid.toString());

            // Altera dados do jogador no Redis
            rsc.hset("player:" + uuid.toString(), "name", name);
            rsc.hset("player:" + uuid.toString(), "instance", RedisConfiguration.BUNGEE + RedisConfiguration.instanceID);
            rsc.hset("player:" + uuid.toString(), "online", "0");
            rsc.hset("player:" + uuid.toString(), "ip", address.getHostAddress());
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    /**
     * Processa o evento de LogOff
     *
     * @param player ProxiedPlayer do jogador que saiu
     */
    private void processLogOff(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            // Remove o jogador da lista de jogadores online (UUID)
            rsc.srem("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnlineUUID", uuid.toString());
            // Remove o jogador da lista de jogadores online (Name)
            rsc.hdel("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnlineName", name);

            // Salva o momento do último login do jogador
            rsc.hset("player:" + uuid.toString(), "online", String.valueOf(System.currentTimeMillis()));
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    /**
     * Processa o evento de ChangeServer
     *
     * @param player ProxiedPlayer do jogador que trocou de server
     * @param server Servidor, do BungeeCord, destino do jogador
     */
    private void processChangeServer(ProxiedPlayer player, ServerInfo server) {
        UUID uuid = player.getUniqueId();
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.hset("player:" + uuid.toString(), "bungee-server", server.getName());
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    /**
     * Processa o evento de PlayerTellAndQuoteReceived
     *
     * @param uuid UUID do jogador
     * @param isTellOn status do tell do jogador
     * @param isQuoteOn status do quote do jogador
     */
    private void processSavePlayerTellAndQuote(UUID uuid, boolean isTellOn, boolean isQuoteOn) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.hset("player:" + uuid.toString(), "tell-status", isTellOn ? "on" : "off");
            rsc.hset("player:" + uuid.toString(), "quote-status", isQuoteOn ? "on" : "off");
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }
}