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
 * @author Ramon, Lucas
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
                c += rsc.scard("instance:" + server + ":usersOnlineUUID");
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
            // Limpa os registros dos jogadores conectados a instancia
            for(String stringUUID : rsc.smembers("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnlineUUID")) {
                UUID uuid = UUID.fromString(stringUUID);
                PlayerData.cleanUpPlayer(uuid);
            }

            // Garante que os Nicks sejam excluídos da lista de jogadores online por Nick
            for(String name : rsc.smembers("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnlineName"))
                rsc.srem("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnlineName", name);

            // Reseta o tempo do contato da instância com o Redis
            rsc.set("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":heartbeats", "0");
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

}