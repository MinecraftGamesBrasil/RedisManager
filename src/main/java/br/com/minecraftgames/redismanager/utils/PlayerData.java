package br.com.minecraftgames.redismanager.utils;

import br.com.minecraftgames.redismanager.Redis;
import br.com.minecraftgames.redismanager.RedisConfiguration;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerData {

    /**
     * Limpa os dados atuais do jogador, armazenados no Redis.
     *
     * @param uuid UUID do jogador
     */
    public static void cleanUpPlayer(UUID uuid) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.srem("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnline", uuid.toString());
            if(rsc.hexists("player:" + uuid.toString(), "online") && Long.parseLong(rsc.hget("player:" + uuid.toString(), "online")) == 0L)
                rsc.hset("player:" + uuid.toString(), "online", rsc.get("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":heartbeats"));
            rsc.hdel("player:" + uuid.toString(), "instance");
            rsc.hdel("player:" + uuid.toString(), "bungee-server");
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    public static Set<UUID> getPlayers() {
        Set<UUID> players = new HashSet<UUID>();
        for(String instance : RedisConfiguration.instancesIDs)
            players.addAll(getPlayersOnInstance(instance));
        return players;
    }

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

    public static boolean isPlayerOnline(UUID uuid) {
        return getPlayers().contains(uuid);
    }

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

    public static long getLastOnline(UUID uuid) {
        long time = -1L;
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

    public static InetAddress getIpAddress(UUID uuid) {
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