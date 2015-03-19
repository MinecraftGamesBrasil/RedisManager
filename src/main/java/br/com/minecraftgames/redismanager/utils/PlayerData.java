package br.com.minecraftgames.redismanager.utils;

import br.com.minecraftgames.redismanager.Redis;
import br.com.minecraftgames.redismanager.RedisConfiguration;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * <h1>Utilidades referente a dados de jogadores</h1>
 *
 * @author Ramon, Lucas
 */
public class PlayerData {

    /**
     * Faz o safe-logout do jogador
     *
     * @param player Objeto do jogador
     */
    public static void gracefulLogout(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            // Remove o jogador da lista de jogadores onlines na instância
            rsc.srem("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnline", uuid.toString());

            // Iguala o tempo da última vez que o jogador esteve online ao último contato da instância com o Redis, caso o jogador não tenha sido limpo antes
            rsc.hset("player:" + uuid.toString(), "online", String.valueOf(System.currentTimeMillis()));

            // Remove a instância que o jogador esteve conectado de seus dados
            rsc.hdel("player:" + uuid.toString(), "instance");

            // Remove o server do BungeeCord que o jogador esteve conectado do seus dados
            rsc.hdel("player:" + uuid.toString(), "bungee-server");
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    /**
     * Limpa os dados atuais do jogador, armazenados no Redis.
     *
     * @param uuid UUID do jogador
     */
    public static void cleanUpPlayer(UUID uuid) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            // Remove o jogador da lista de jogadores onlines na instância
            rsc.srem("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnline", uuid.toString());

            // Iguala o tempo da última vez que o jogador esteve online ao último contato da instância com o Redis, caso o jogador não tenha sido limpo antes
            if(rsc.hexists("player:" + uuid.toString(), "online") && Long.parseLong(rsc.hget("player:" + uuid.toString(), "online")) == 0L)
                rsc.hset("player:" + uuid.toString(), "online", rsc.get("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":heartbeats"));

            // Remove a instância que o jogador esteve conectado de seus dados
            rsc.hdel("player:" + uuid.toString(), "instance");

            // Remove o server do BungeeCord que o jogador esteve conectado do seus dados
            rsc.hdel("player:" + uuid.toString(), "bungee-server");
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    /**
     * Lista de jogadores conectados em todas as instâncias
     *
     * @return Set de UUID dos jogadores
     */
    public static Set<UUID> getPlayers() {
        Set<UUID> players = new HashSet<UUID>();
        for(String instance : RedisConfiguration.instancesIDs)
            players.addAll(getPlayersOnInstance(instance));
        return players;
    }

    /**
     * Lista de jogadores conectados em determniada instância
     *
     * @param instance Instância
     * @return Set de UUID dos jogadores na instância
     */
    public static Set<UUID> getPlayersOnInstance(String instance) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            Set<UUID> players = new HashSet<UUID>();
            for(String stringUUID : rsc.smembers("instance:" + instance + ":usersOnline")) {
                UUID uuid = UUID.fromString(stringUUID);
                players.add(uuid);
            }
            return players;
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return null;
    }

    /**
     * Se o jogador está online ou não
     *
     * @param uuid UUID do jogador
     * @return {@code true} para online e {@code false} para offlne
     */
    public static boolean isPlayerOnline(UUID uuid) {
        return getPlayers().contains(uuid);
    }

    /**
     * Server do BungeeCord que o jogador se encontra
     *
     * @param uuid UUID do jogador
     * @return ServerInfo do server do BungeeCord
     */
    public static ServerInfo getBungeeServerFor(UUID uuid) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            ServerInfo server = null;
            if(rsc.hexists("player:" + uuid.toString(), "bungee-server"))
                server = ProxyServer.getInstance().getServerInfo(rsc.hget("player:" + uuid.toString(), "bungee-server"));
            return server;
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return null;
    }

    /**
     * Instância em que o jogador está conectado
     *
     * @param uuid UUID do jogador
     * @return Instância
     */
    public static String getInstanceFor(UUID uuid) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            String proxy = null;
            if(rsc.hexists("player:" + uuid.toString(), "instance"))
                proxy = rsc.hget("player:" + uuid.toString(), "instance");
            return proxy;
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return null;
    }

    /**
     * Tempo(MS) do último logout do jogador
     *
     * @param uuid UUID do jogador
     * @return Tempo do logout
     */
    public static long getLastOnline(UUID uuid) {
        long time = -1L;

        // Se o jogador estiver online retornar 0
        if(isPlayerOnline(uuid))
            return 0L;

        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            if(rsc.hexists("player:" + uuid.toString(), "online"))
                time = Long.valueOf(rsc.hget("player:" + uuid.toString(), "online"));
            else
                rsc.hset("player:" + uuid.toString(), "online", String.valueOf(System.currentTimeMillis()));
            return time;
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return 0L;
    }

    /**
     * Último IP do jogador
     *
     * @param uuid UUID do jogador
     * @return Último IP(InetAddress) do jogador
     */
    public static InetAddress getIpAddress(UUID uuid) {
        // Se o jogador estiver online pegar o IP diretamente do BungeeCord
        if(isPlayerOnline(uuid))
            return ProxyServer.getInstance().getPlayer(uuid).getAddress().getAddress();

        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            InetAddress ia = null;
            if(rsc.hexists("player:" + uuid.toString(), "ip"))
                ia = InetAddress.getByName(rsc.hget("player:" + uuid.toString(), "ip"));
            return ia;
        } catch (UnknownHostException ignored) {} catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return null;
    }
}