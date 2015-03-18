package br.com.minecraftgames.redismanager;

import br.com.minecraftgames.redismanager.utils.PlayerData;
import br.com.minecraftgames.redismanager.utils.ServerData;
import net.md_5.bungee.api.config.ServerInfo;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.net.InetAddress;
import java.util.Set;
import java.util.UUID;

public class RedisManagerAPI {

    private RedisManager plugin;

    public RedisManagerAPI(RedisManager instance) {
        this.plugin = instance;
    }

    private final static void publish(String channel, String message) {
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.publish(channel, message);
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }
    }

    public final static int getPlayerCount() {
        return RedisConfiguration.globalCount;
    }

    public final static long getLastOnline(UUID uuid) {
        return PlayerData.getLastOnline(uuid);
    }

    public final static ServerInfo getBungeeServerFor(UUID uuid) {
        return PlayerData.getBungeeServerFor(uuid);
    }

    public final static String getInstanceFor(UUID uuid) {
        return PlayerData.getInstanceFor(uuid);
    }

    public final static Set<UUID> getPlayersOnline() {
        return PlayerData.getPlayers();
    }

    public final static Set<UUID> getPlayersOnInstance(String instance) {
        return PlayerData.getPlayersOnInstance(instance);
    }

    public final static boolean isPlayerOnline(UUID uuid) {
        return PlayerData.isPlayerOnline(uuid);
    }

    public final static InetAddress getPlayerIp(UUID uuid) {
        return PlayerData.getIpAddress(uuid);
    }

    public final static String getServerId() {
        return RedisConfiguration.BUNGEE + RedisConfiguration.instanceID;
    }

    public final static int getServerIntId() {
        return RedisConfiguration.instanceID;
    }

    public final static Set<String> getAllServers() {
        return RedisConfiguration.instancesIDs;
    }

    public final static Set<UUID> getTellOff() {
        return RedisConfiguration.tellOff;
    }

    public final static Set<UUID> getQuoteOff() {
        return RedisConfiguration.quoteOff;
    }

    public final static boolean isChatOff() {
        return RedisConfiguration.isChatOff;
    }

    public final static Set<String> getWhitelistedLobbys() {
        return ServerData.getWhitelistedLobbys();
    }

    public final static void sendToChannel(String channelName, String channelTag, String playerTag, String playerName, String messageDeafultColor, String message) {
        publish("sendtochannel", channelName + "%=%" + channelTag + "%=%" + playerTag + "%=%" + playerName + "%=%" + messageDeafultColor + "%=%" + message);
    }

    public final static void sendToPlayer(String player, String message) {
        publish("sendtoplayer", player + "%=%" + message);
    }

    public final static void ban(String player, String by, int time, String reason) {
        publish("ban", player + "%=%" + by + "%=%" + time + "%=%" + reason);
    }

    public final static void kick(String player, String by, String reason) {
        publish("kick", player + "%=%" + by + "%=%" + reason);
    }

    public final static void mute(String player, String by, int time, String reason) {
        publish("mute", player + "%=%" + by + "%=%" + time + "%=%" + reason);
    }

    public final static void unban(String player, String name) {
        publish("unban", player + "%=%" + name);
    }

    public final static void unmute(String player, String name) {
        publish("unmute", player + "%=%" + name);
    }

    public final static void turnChat(String to) {
        publish("turnchat", to);
    }

    public final static void reloadGroups() {
        publish("reloadgroups", "true");
    }

    public final static void reloadConfig() {
        publish("reloadconfig", "true");
    }

    public final static void reloadExecutors() {
        publish("reloadexecutors", "true");
    }

    public final static void reloadAM() {
        publish("reloadam", "");
    }

    public final static void turnTell(String name, String to) {
        publish("turntell", name + "%=%" + to);
    }

    public final static void turnQuote(String name, String to) {
        publish("turnquote", name + "%=%" + to);
    }

    public final static void whitelistedLobby(String lobby, String action) {
        publish("whitelistedlobby", lobby + "%=%" + action);
    }

    public final static void tell(String from, String to, String message) {
        publish("tell", from + "%=%" + to + "%=%" + message);
    }

    public final static void send(String player, String target, boolean message) {
        publish("send", player + "%=%" + target + "%=%" + (message ? "true" : "false"));
    }

    public final static void updateSidebar(String name) {
        publish("updatesidebar", name);
    }

    public final static void warnQuote(String quoted) {
        publish("warnquote", quoted);
    }

}