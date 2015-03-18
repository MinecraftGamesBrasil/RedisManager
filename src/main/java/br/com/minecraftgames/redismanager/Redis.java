package br.com.minecraftgames.redismanager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * <h1>Atalhos para funções do Redis</h1>
 *
 * @author Ramon, Lucas
 */
public class Redis {

    private static JedisPool pool;

    // Dados do banco de dados
    private static String server = "172.16.0.11";
    private static int port = 6379;

    /**
     * <h1>Cria a conexão com o Redis</h1>
     */
    public static void initialize() {
        // JedisPool settings
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(8);
        config.setJmxEnabled(false);
        JedisPool jedisPool = new JedisPool(config, server, port, 0);

        Jedis rsc = null;
        try {
            rsc = jedisPool.getResource();
            // Tests the connection
            rsc.ping();
        } catch (JedisConnectionException e) {
            if (rsc != null)
                jedisPool.returnBrokenResource(rsc);
            jedisPool.destroy();
            jedisPool = null;
            rsc = null;
            throw e;
        } finally {
            if (rsc != null && jedisPool != null)
                jedisPool.returnResource(rsc);
        }
        pool = jedisPool;
    }

    /**
     * <h1>Retorna o Resource do Redis</h1>
     *
     * @return Jedis Resource
     */
    public static Jedis getResource() {
        return pool.getResource();
    }

    /**
     * <h1>Retorna a JedisPool</h1>
     *
     * @return JedisPool
     */
    public static JedisPool getPool() {
        return pool;
    }
}