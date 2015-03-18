package br.com.minecraftgames.redismanager.utils;

import br.com.minecraftgames.redismanager.Redis;
import br.com.minecraftgames.redismanager.RedisConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Set;
import java.util.UUID;

/**
 * <h1>Utilidades referente a instâncias</h1>
 *
 * @version 0.0.0
 */
public class Instances {

    /**
     * Instâncias registradas no Redis
     *
     * @return Um Set de String com os IDs das instâncias
     */
    public static Set<String> getInstancesIDs() {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            return rsc.smembers("instances");
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return null;
    }

    /**
     * Quantidade de jogadores online em todas as instâncias
     *
     * @return Soma da quantidade de jogadores online em cada instância
     */
    public static int getGlobalCount() {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            int c = 0;
            for(String server : RedisConfiguration.instancesIDs)
                c += rsc.scard("instance:" + server + ":usersOnline");
            return c;
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return 0;
    }

    /**
     * Limpa os dados atuais da instância, armazenados no Redis
     */
    public static void cleanUpInstance() {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            for(String stringUUID : rsc.smembers("server:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnline")) {
                UUID uuid = UUID.fromString(stringUUID);
                cleanUpPlayer(uuid);
            }
            rsc.set("server:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":heartbeats", "0");
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    /**
     * Limpa os dados atuais do jogador, armazenados no Redis.
     *
     * @param UUID do jogador
     */
    public static void cleanUpPlayer(UUID uuid) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.srem("server:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnline", uuid.toString());
            if(rsc.hexists("player:" + uuid.toString(), "online") && Long.parseLong(rsc.hget("player:" + uuid.toString(), "online")) == 0L)
                rsc.hset("player:" + uuid.toString(), "online", rsc.get("server:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":heartbeats"));
            rsc.hdel("player:" + uuid.toString(), "proxy");
            rsc.hdel("player:" + uuid.toString(), "server");
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }
}