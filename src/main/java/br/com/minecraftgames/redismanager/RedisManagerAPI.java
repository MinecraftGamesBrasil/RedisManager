package br.com.minecraftgames.redismanager;

import br.com.minecraftgames.redismanager.processor.events.PlayerTellAndQuoteStatusReceived;
import br.com.minecraftgames.redismanager.utils.PlayerData;
import net.md_5.bungee.api.config.ServerInfo;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.net.InetAddress;
import java.util.*;

/**
 * <h1>API de funções</h1>
 * API com todas as funções que serão usadas por outro plugin
 *
 * @author Ramon, Lucas
 */
public class RedisManagerAPI {

    private RedisManager plugin;

    /**
     * Construtor da API
     *
     * @param instance Instância da main class do plugin
     */
    public RedisManagerAPI(RedisManager instance) {
        this.plugin = instance;
    }

    /**
     * Publica alguma mensagem no PubSub do redis
     * Uso interno da classe
     *
     * @param channel Canal da publicação
     * @param message Mensagem a ser publicada
     */
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

    /**
     * Soma de jogadores online em todas as instâncias
     *
     * @return Int com o número de jogadores
     */
    public final static int getPlayerCount() {
        return RedisConfiguration.globalCount;
    }

    /**
     * Retorna um set contendo os jogadores que estão conectados em todas as instâncias
     *
     * @return Set de String com nick dos jogadores
     */
    public final static Set<String> getPlayersOnlineByName() {
        return PlayerData.getPlayersByName();
    }

    /**
     * Retorna um set contendo os jogadores que estão conectados em todas as instâncias
     *
     * @return Set de UUID dos jogadores
     */
    public final static Set<UUID> getPlayersOnline() {
        return PlayerData.getPlayers();
    }

    /**
     * Retorna um set contendo os jogadores contectados em determinada instância
     *
     * @param instance Instância
     * @return Set de String com o nick dos jogadores da instância
     */
    public final static Set<String> getPlayersOnInstanceByName(String instance) {
        return PlayerData.getPlayersOnInstanceByName(instance);
    }

    /**
     * Retorna um set contendo os jogadores contectados em determinada instância
     *
     * @param instance Instância
     * @return Set de UUID dos jogadores da instância
     */
    public final static Set<UUID> getPlayersOnInstance(String instance) {
        return PlayerData.getPlayersOnInstance(instance);
    }

    /**
     * Verifica se um jogador está online em alguma instância
     *
     * @param uuid UUID do jogador
     * @return {@code true} para online e {@code false} para offline
     */
    public final static boolean isPlayerOnline(UUID uuid) {
        return PlayerData.isPlayerOnline(uuid);
    }

    /**
     * Verifica se um jogador está online em alguma instância
     *
     * @param name Nick do jogador
     * @return {@code true} para online e {@code false} para offline
     */
    public final static boolean isPlayerOnline(String name) {
        return PlayerData.isPlayerOnline(name);
    }

    /**
     * Retorna o UUID de um jogador
     *
     * @param name Nick do jogador
     * @return UUID do jogador
     */
    public final static UUID getPlayerUUID(String name) {
        return PlayerData.getPlayerUUID(name);
    }

    /**
     * Retorna o IP de um jogador que esteja em qualquer instância
     *
     * @param uuid UUID do jogador
     * @return InetAddress do jogador
     */
    public final static InetAddress getPlayerIp(UUID uuid) {
        return PlayerData.getIpAddress(uuid);
    }

    /**
     * Retorna o server do BungeeCord em que o jogador, de qualquer instância, está conectado
     *
     * @param uuid UUID do jogador
     * @return ServerInfo do server do BungeeCord
     */
    public final static ServerInfo getBungeeServerFor(UUID uuid) {
        return PlayerData.getBungeeServerFor(uuid);
    }

    /**
     * Retorna a instância do BungeeCord em que o jogador está conectado
     *
     * @param uuid UUID do jogador
     * @return String no formato: {@code bungeeX}, onde {@code X} representa o número da instância
     */
    public final static String getInstanceFor(UUID uuid) {
        return PlayerData.getInstanceFor(uuid);
    }

    /**
     * Retorna o tempo em MS(long) do último logout do jogador
     *
     * @param uuid UUID do jogador
     * @return {@code 0} caso o jogador esteja online, {@code -1} caso o jogador nunca tenha entrado ou o MS do último logout
     */
    public final static long getLastOnline(UUID uuid) {
        return PlayerData.getLastOnline(uuid);
    }

    /**
     * Retorna o nick do jogador
     *
     * @param uuid UUID do jogador
     * @return String com o nick do jogador.
     */
    public final static String getPlayerName(UUID uuid) {
        return PlayerData.getPlayerName(uuid);
    }

    /**
     * Instância que está rodando
     *
     * @return String no formato: {@code bungeeX}, onde {@code X} representa o número da instância
     */
    public final static String getInstance() {
        return RedisConfiguration.BUNGEE + RedisConfiguration.instanceID;
    }

    /**
     * ID da instância que está rodando
     *
     * @return int com o ID da instância
     */
    public final static int getInstanceID() {
        return RedisConfiguration.instanceID;
    }

    /**
     * Instâncias ativas
     *
     * @return Set de String no formato: {@code bungeeX}, onde {@code X} representa o número da instância
     */
    public final static Set<String> getAllServers() {
        return RedisConfiguration.instancesIDs;
    }

    /**
     * Salva no Redis o status do tell e citações do jogador
     *
     * @param uuid UUID do jogador
     * @param isTellOn status do tell do jogador
     * @param isQuoteOn status das citações do jogador
     */
    public final static void saveTellAndQuoteStatus(UUID uuid, boolean isTellOn, boolean isQuoteOn) {
        RedisManager.processor.process(new PlayerTellAndQuoteStatusReceived(uuid, isTellOn, isQuoteOn));
    }

    /**
     * Retorna se o tell do jogador está ativo
     *
     * @return {@code true} para tell ativo e {@code false} para tell desativado
     */
    public final static boolean isTellOn(UUID uuid) {
        return PlayerData.isTellOn(uuid);
    }

    /**
     * Retorna se as citações do jogador está ativo
     *
     * @return S{@code true} para citações ativas e {@code false} para citações desativadas
     */
    public final static boolean isQuoteOn(UUID uuid) {
        return PlayerData.isQuoteOn(uuid);
    }

    /**
     * Envia uma mensagem em um canal
     *
     * @param channelName Nome do canal onde a mensagem será enviada
     * @param channelTag {@code true} para mostrar tag do canal e {@code false} para escondê-la
     * @param playerTag Tag do jogador. {@code null} esconde a tag do jogador
     * @param playerName Nome do jogador. {@code null} esconde o nome do jogador
     * @param messageDeafultColor Cor padrão da mensagem. {@code null} utilizará a cor padrão
     * @param message Mensagem a ser enviada
     */
    public final static void sendToChannel(String channelName, boolean channelTag, String playerTag, String playerName, String messageDeafultColor, String message) {
        publish("sendtochannel", channelName + "%=%" + String.valueOf(channelTag) + "%=%" + playerTag + "%=%" + playerName + "%=%" + messageDeafultColor + "%=%" + message);
    }

    /**
     * Envia uma mensagem direcionada a um jogador
     *
     * @param uuid UUID do jogador
     * @param message Mensagem a ser enviada
     */
    public final static void sendToPlayer(UUID uuid, String message) {
        publish("sendtoplayer", uuid.toString() + "%=%" + message);
    }

    /**
     * Comunica o ban de um jogador à todas as instâncias
     *
     * @param uuid UUID do jogador
     * @param punisher Nome do staffer que baniu
     * @param motive Motivo do ban
     * @param duration Tempo de duração do ban
     */
    public final static void transmitBan(UUID uuid, String punisher, int motive, int duration) {
        publish("ban", uuid.toString() + "%=%" + punisher + "%=%" + motive + "%=%" + duration);
    }

    /**
     * Comunica o mute de um jogador à todas as instâncias
     *
     * @param uuid UUID do jogador
     * @param punisher Nome do staffer que mutou
     * @param motive Motivo do mute
     * @param duration Tempo de duração do mute
     */
    public final static void transmitMute(UUID uuid, String punisher, int motive, int duration) {
        publish("mute", uuid.toString() + "%=%" + punisher + "%=%" + motive + "%=%" + duration);
    }

    /**
     * Comunica o unmute de um jogador à todas as instâncias
     *
     * @param uuid UUID do jogador
     * @param staffer Nome do staffer que deu o unmute
     */
    public final static void transmitUnmute(UUID uuid, String staffer) {
        publish("unmute", uuid.toString() + "%=%" + staffer);
    }

    /**
     * Altera o status do chat normal
     *
     * @param on Novo status: {@code true} para ativar e {@code false} para desativar
     */
    public final static void setGlobalChatOn(boolean on) {
        publish("changeglobalchatstate", String.valueOf(on));
    }

    /**
     * Altera se um lobby está ou não na blacklist
     *
     * @param blacklist Map<Integer, Boolean> contendo o número do lobby com a informação de adcionar ou retirar
     */
    public final static void blacklistLobby(int lobby, boolean blacklist) {
        publish("blacklistlobby", String.valueOf(lobby) + "%=%" + String.valueOf(blacklist ? 1 : 0));
    }

    /**
     * Avisa à todas as instâncias para recarregarem o cache de um ou mais setores
     *
     * @param sectors Setor(es) a ser(em) recarregado(s)
     */
    public final static void reload(HashSet<String> sectors) {
        for(String sector : sectors)
            publish("reload", sector);
    }

    /**
     * Envia uma mensagem privada para um jogador
     *
     * @param from UUID do jogador que está enviando a mensagem privada
     * @param to UUID do jogador que irá receber a mensagem
     * @param message Mensagem a ser enviada
     */
    public final static void tell(UUID from, UUID to, String message) {
        publish("tell", from == null ? null : from.toString() + "%=%" + to + "%=%" + message);
    }

    /**
     * Envia um ou mais jogadores para um servidor específico do BungeeCord
     *
     * @param uuids Set de UUID dos jogadores
     * @param target servidor destino do BungeeCord
     */
    public final static void send(Set<UUID> uuids, String target) {
        for(UUID uuid : uuids)
            publish("send", uuid.toString() + "%=%" + target);
    }

    /**
     * Atualiza a sidebar de um jogador específico
     *
     * @param uuid UUID do jogador
     */
    public final static void updateSidebar(UUID uuid) {
        publish("updatesidebar", uuid.toString());
    }

    /**
     * Envia a notificação sonora de um quote à um jogador
     *
     * @param uuid UUID do jogador
     */
    public final static void warnQuote(UUID uuid) {
        publish("warnquote", uuid.toString());
    }

}