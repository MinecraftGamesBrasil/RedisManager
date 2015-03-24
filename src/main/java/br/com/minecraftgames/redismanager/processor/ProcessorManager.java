package br.com.minecraftgames.redismanager.processor;

import br.com.minecraftgames.redismanager.Redis;
import br.com.minecraftgames.redismanager.RedisConfiguration;
import br.com.minecraftgames.redismanager.RedisManager;
import br.com.minecraftgames.redismanager.processor.events.PlayerChangedServerEvent;
import br.com.minecraftgames.redismanager.processor.events.PlayerLoggedInEvent;
import br.com.minecraftgames.redismanager.processor.events.PlayerLoggedOffEvent;
import br.com.minecraftgames.redismanager.processor.events.ProcessorEvent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.net.InetAddress;
import java.util.UUID;

public class ProcessorManager {

    private RedisManager plugin;

    public ProcessorManager(RedisManager instance) {
        this.plugin = instance;
    }

    public void process(ProcessorEvent processorEvent) {
        Runnable processableEvent = null;
        if(processorEvent instanceof PlayerLoggedInEvent) {
            final PlayerLoggedInEvent event = (PlayerLoggedInEvent) processorEvent;
            processableEvent = new Runnable() {
                public void run() {
                    ProxiedPlayer player = event.getPlayer();
                    processLogIn(player);
                }
            };
        } else if(processorEvent instanceof PlayerLoggedOffEvent) {
            final PlayerLoggedOffEvent event = (PlayerLoggedOffEvent) processorEvent;
            processableEvent = new Runnable() {
                public void run() {
                    ProxiedPlayer player = event.getPlayer();
                    processLogOff(player);
                }
            };
        } else if(processorEvent instanceof PlayerChangedServerEvent) {
            final PlayerChangedServerEvent event = (PlayerChangedServerEvent) processorEvent;
            processableEvent = new Runnable() {
                public void run() {
                    ProxiedPlayer player = event.getPlayer();
                    ServerInfo server = event.getNewServer();
                    processChangeServer(player, server);
                }
            };
        }
        plugin.getExecutorService().submit(processableEvent);
    }

    private void processLogIn(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        InetAddress address = player.getAddress().getAddress();
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.sadd("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnline", uuid.toString());
            rsc.hset("player:" + uuid.toString(), "name", name);
            rsc.hset("player:" + uuid.toString(), "instance", RedisConfiguration.BUNGEE + RedisConfiguration.instanceID);
            rsc.hset("player:" + uuid.toString(), "online", "0");
            rsc.hset("player:" + uuid.toString(), "ip", address.getHostAddress());
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    private void processLogOff(ProxiedPlayer player) {
        UUID uuid = player.getUniqueId();
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.srem("instance:" + RedisConfiguration.BUNGEE + RedisConfiguration.instanceID + ":usersOnline", uuid.toString());
            rsc.hset("player:" + uuid.toString(), "online", String.valueOf(System.currentTimeMillis()));
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    private void processChangeServer(ProxiedPlayer player, ServerInfo server) {
        UUID uuid = player.getUniqueId();
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.hset("player:" + uuid.toString(), "bungee-server", server.getName());
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }
}