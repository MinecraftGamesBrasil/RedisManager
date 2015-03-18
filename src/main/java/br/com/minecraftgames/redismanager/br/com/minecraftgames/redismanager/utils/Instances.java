package br.com.minecraftgames.redismanager.br.com.minecraftgames.redismanager.utils;

import br.com.minecraftgames.redismanager.Redis;
import br.com.minecraftgames.redismanager.RedisConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Set;

/**
 * <h1>Utilidades referente a instâncias</h1>
 *
 * @version 0.0.0
 */
public class Instances {

    /**
     * <h1>Instâncias registradas no Redis</h1>
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
     * <h1>Quantidade de jogadores online em todas as instâncias</h1>
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
}