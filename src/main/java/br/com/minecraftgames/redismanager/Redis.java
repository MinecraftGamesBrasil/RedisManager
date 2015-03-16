package br.com.minecraftgames.redismanager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class Redis {

	private static JedisPool pool;

	private static String server = "172.16.0.1";
	private static int port = 6379;
	private static String password = "HoLz";

	public static void initialize() {
		pool = createConnection();
	}

	private static JedisPool createConnection() {
		JedisPool pool = new JedisPool(new JedisPoolConfig(), server, port, 2000, password);
		Jedis rsc = null;
		try {
			rsc = (Jedis) pool.getResource();
			rsc.exists(String.valueOf(System.currentTimeMillis()));
		} catch(JedisConnectionException e) {
			e.printStackTrace();
		} finally {
			if(rsc != null && pool != null)
				pool.returnResource(rsc);
		}
		return pool;
	}

	public static Jedis getResource() {
		Jedis rsc = null;
		rsc = (Jedis) pool.getResource();
		return rsc;
	}

	public static JedisPool getPool() {
		return pool;
	}
}