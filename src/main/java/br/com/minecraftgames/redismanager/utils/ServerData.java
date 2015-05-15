package br.com.minecraftgames.redismanager.utils;

import br.com.minecraftgames.redismanager.Redis;
import br.com.minecraftgames.redismanager.RedisConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * <h1>Utilidades referente a dados do BungeeCord</h1>
 *
 * @author Ramon, Lucas
 */
public class ServerData {

    /**
     * Lista de jogadores com tell desativado
     *
     * @return Set de UUID com o UUID dos jogadores com tell desativado
     */
    public static Set<UUID> getTellOff() {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            Set<UUID> uuids = new HashSet<UUID>();
            for(String stringUUID : rsc.smembers("config:tell-off")) {
                UUID uuid = UUID.fromString(stringUUID);
                uuids.add(uuid);
            }
            return uuids;
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return null;
    }

    /**
     * Adciona um jogador a lista de tell desativado
     *
     * @param uuid UUID do jogador
     */
    public static void addTellOff(UUID uuid) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.sadd("config:tell-off", uuid.toString());
            RedisConfiguration.tellOff.add(uuid);
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    /**
     * Remove um jogador da lista de tell desativado
     *
     * @param uuid UUID do jogador
     */
    public static void removeTellOff(UUID uuid) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.srem("config:tell-off", uuid.toString());
            RedisConfiguration.tellOff.remove(uuid.toString());
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    /**
     * Lista de jogadores com citações desativadas
     *
     * @return Set de String com o UUID dos jogadores com citações desativadas
     */
    public static Set<UUID> getQuoteOff() {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            Set<UUID> uuids = new HashSet<UUID>();
            for(String stringUUID : rsc.smembers("config:quote-off")) {
                UUID uuid = UUID.fromString(stringUUID);
                uuids.add(uuid);
            }
            return uuids;
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return null;
    }

    /**
     * Adciona um jogador a lista de citações desativadas
     *
     * @param uuid UUID do jogador
     */
    public static void addQuoteOff(UUID uuid) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.sadd("config:quote-off", uuid.toString());
            RedisConfiguration.quoteOff.add(uuid);
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    /**
     * Remove um jogador da lista de citações desativadas
     *
     * @param uuid UUID do jogador
     */
    public static void removeQuoteOff(UUID uuid) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.srem("config:quote-off", uuid.toString());
            RedisConfiguration.quoteOff.remove(uuid);
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

    /**
     * Status atual do chat normal
     *
     * @return {@code true} para o chat habilitado ou {@code false} para desabilitado
     */
    public static boolean getGlobalChatState() {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            return !rsc.exists("config:global-chat-state");
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
        return false;
    }

    /**
     * Altera o status do chat global
     *
     * @param state Novo status
     */
    public static void setGlobalChatState(String state) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            if(state.equalsIgnoreCase("off")) {
                rsc.set("config:global-chat-off", "true");
                RedisConfiguration.isGlobalChatOff = true;
            } else {
                rsc.del("config:global-chat-off");
                RedisConfiguration.isGlobalChatOff = false;
            }
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }
}