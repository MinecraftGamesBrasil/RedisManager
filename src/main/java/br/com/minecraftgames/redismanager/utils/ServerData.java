package br.com.minecraftgames.redismanager.utils;

import br.com.minecraftgames.redismanager.Redis;
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
}