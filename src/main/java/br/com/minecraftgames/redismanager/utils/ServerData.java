package br.com.minecraftgames.redismanager.utils;

import br.com.minecraftgames.redismanager.Redis;
import br.com.minecraftgames.redismanager.RedisConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashSet;
import java.util.Set;

/**
 * <h1>Utilidades referente a dados do BungeeCord</h1>
 *
 * @author Ramon, Lucas
 */
public class ServerData {

    /**
     * Lista de lobbys em manutenção
     *
     * @return Set de String com os lobbys que estão em manutenção
     */
    public static Set<String> getWhitelistedLobbys() {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            return new HashSet<String>(rsc.smembers("config:whitelisted-lobbys"));
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return null;
    }

    /**
     * Adciona um lobby a whitelist
     *
     * @param lobby Lobby
     */
    public static void addLobbyToWhitelist(String lobby) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.sadd("config:whitelisted-lobbys", lobby);
            RedisConfiguration.whitelistedLobbys.add(lobby);
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    /**
     * Retira um lobby da whitelist
     *
     * @param lobby Lobby
     */
    public static void removeLobbyFromWhitelist(String lobby) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.srem("config:whitelisted-lobbys", lobby);
            RedisConfiguration.whitelistedLobbys.remove(lobby);
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }
}