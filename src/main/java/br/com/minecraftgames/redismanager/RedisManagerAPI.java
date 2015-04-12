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
     * Retorna jogadores com o tell desativado
     *
     * @return Set de UUID dos jogadores
     */
    public final static Set<UUID> getTellOff() {
        return RedisConfiguration.tellOff;
    }

    /**
     * Retorna jogadores com o citações desativadas
     *
     * @return Set de UUID dos jogadores
     */
    public final static Set<UUID> getQuoteOff() {
        return RedisConfiguration.quoteOff;
    }

    /**
     * Verifica o status atual do chat normal
     *
     * @return {@code true} para ativo e {@code false} para desativado
     */
    public final static boolean isChatOff() {
        return RedisConfiguration.isChatOff;
    }

    /**
     * Retorna os lobbys que estão em manutenção
     *
     * @return Set de String no formato: {@code lobbyX}, onde {@code X} representa o número do lobby
     */
    public final static Set<String> getWhitelistedLobbys() {
        return ServerData.getWhitelistedLobbys();
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
     * @param time Tempo de duração do ban
     * @param reason Motivo do ban
     */
    public final static void ban(UUID uuid, String punisher, int time, String reason) {
        publish("ban", uuid.toString() + "%=%" + punisher + "%=%" + time + "%=%" + reason);
    }

    /**
     * Comunica o kick de um jogador à todas as instâncias
     *
     * @param uuid UUID do jogador
     * @param punisher Nome do staffer que kickou
     * @param reason Motivo do kick
     */
    public final static void kick(UUID uuid, String punisher, String reason) {
        publish("kick", uuid.toString() + "%=%" + punisher + "%=%" + reason);
    }

    /**
     * Comunica o mute de um jogador à todas as instâncias
     *
     * @param uuid UUID do jogador
     * @param punisher Nome do staffer que mutou
     * @param time Tempo do mute
     * @param reason Motivo do mute
     */
    public final static void mute(UUID uuid, String punisher, int time, String reason) {
        publish("mute", uuid.toString() + "%=%" + punisher + "%=%" + time + "%=%" + reason);
    }

    /**
     * Comunica o unban de um jogador à todas as instâncias
     *
     * @param uuid UUID do jogador
     * @param staffer Nome do staffer que deu o unban
     */
    public final static void unban(UUID uuid, String staffer) {
        publish("unban", uuid.toString() + "%=%" + staffer);
    }

    /**
     * Comunica o unmute de um jogador à todas as instâncias
     *
     * @param uuid UUID do jogador
     * @param staffer Nome do staffer que deu o unmute
     */
    public final static void unmute(UUID uuid, String staffer) {
        publish("unmute", uuid.toString() + "%=%" + staffer);
    }

    /**
     * Altera o status do chat normal
     *
     * @param to Novo status: {@code on} para ativar e {@code off} para desativar
     */
    public final static void turnChat(String to) {
        publish("turnchat", to);
    }

    /**
     * Avisa à todas as instâncias para recarregarem o cache de um setor
     *
     * @param sector Setor a ser recarregado
     */
    public final static void reload(String sector) {
        publish("reload", sector);
    }

    /**
     * Altera o status do tell de um jogador
     *
     * @param uuid UUID do jogador
     * @param to Novo status: {@code on} para ativar e {@code off} para desativar
     */
    public final static void turnTell(UUID uuid, String to) {
        publish("turntell", uuid.toString() + "%=%" + to);
    }

    /**
     * Altera o status das citações de um jogador
     *
     * @param uuid UUID do jogador
     * @param to Novo status: {@code on} para ativar e {@code off} para desativar
     */
    public final static void turnQuote(UUID uuid, String to) {
        publish("turnquote", uuid.toString() + "%=%" + to);
    }

    /**
     * Altera o status de bloqueio de algum lobby
     *
     * @param lobby Lobby a ser alterado, no formato: {@code lobbyX}, onde {@code X} representa o número do lobby
     * @param to Novo status: {@code add} adciona ou {@code del} deleta um lobby a lista de lobbys bloqueados
     */
    public final static void whitelistedLobby(String lobby, String to) {
        publish("whitelistedlobby", lobby + "%=%" + to);
    }

    /**
     * Envia uma mensagem privada para um jogador
     *
     * @param from UUID do jogador que está enviando a mensagem privada
     * @param to Nome do jogador que irá receber a mensagem
     * @param message Mensagem a ser enviada
     */
    public final static void tell(UUID from, String to, String message) {
        publish("tell", from.toString() + "%=%" + to + "%=%" + message);
    }

    /**
     * Envia um ou mais jogadores para um servidor específico do BungeeCord
     *
     * @param uuids Set de UUID dos jogadores
     * @param target servidor destino do BungeeCord
     * @param message {@code true} envia a mensagem ao jogador, enquanto {@code false} realiza a operação silenciosamente
     */
    public final static void send(Set<UUID> uuids, String target, boolean message) {
        for(UUID uuid : uuids)
            publish("send", uuid.toString() + "%=%" + target + "%=%" + String.valueOf(message));
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