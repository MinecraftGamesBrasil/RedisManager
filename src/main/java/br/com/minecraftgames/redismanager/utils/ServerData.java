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
     * Lista de jogadores com tell desativado
     *
     * @return Set de String com o UUID(String) dos jogadores com tell desativado
     */
    public static Set<String> getTellOff() {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            return new HashSet<String>(rsc.smembers("config:tell-off"));
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return null;
    }

    public static void addTellOff(String name) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.sadd("config:tell-off", name);
            RedisConfiguration.tellOff.add(name);
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    public static void removeTellOff(String name) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.srem("config:tell-off", name);
            RedisConfiguration.tellOff.remove(name);
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    /**
     * Lista de jogadores com citações desativadas
     *
     * @return Set de String com o UUID(String) dos jogadores com citações desativadas
     */
    public static Set<String> getQuoteOff() {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            return new HashSet<String>(rsc.smembers("config:quote-off"));
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return null;
    }

    public static void addQuoteOff(String name) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.sadd("config:quote-off", name);
            RedisConfiguration.quoteOff.add(name);
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    public static void removeQuoteOff(String name) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.srem("config:quote-off", name);
            RedisConfiguration.quoteOff.remove(name);
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

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

    /**
     * Status atual do chat normal
     *
     * @return {@code true} para o chat habilitado ou {@code false} para desabilitado
     */
    public static boolean getChatState() {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            return !rsc.exists("config:chat-state");
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return false;
    }

    public static void setChatState(String state) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            if(state.equalsIgnoreCase("off")) {
                rsc.set("config:chat-off", "true");
                RedisConfiguration.isChatOff = true;
            } else {
                rsc.del("config:chat-off");
                RedisConfiguration.isChatOff = false;
            }
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }
}