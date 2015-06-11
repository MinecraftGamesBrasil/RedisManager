package br.com.minecraftgames.redismanager;

import br.com.minecraftgames.redismanager.pubsub.PubSubMessageEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

/**
 * <h1>Listener do PubSub</h1>
 *
 * @author Ramon, Lucas
 */
public class RedisListener implements Listener {

    private RedisManager plugin;

    public RedisListener(RedisManager instance) {
        this.plugin = instance;
    }

    /**
     * PlayerPreLogin event
     *
     * @param event PreLoginEvent
     */
    @EventHandler
    public void onPlayerPreLogin(PreLoginEvent event) {
        // Bloqueia a conexão de jogadores, se necessário
        if(!RedisManager.allowConnections)
            event.setCancelled(true);
    }

    /**
     * Recebimento e execução das mensagens recebidas pelo PubSub
     *
     * @param event Evento da mensagem do PubSub
     */
    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent event) {
        String channel = event.getChannel();
        String message = event.getMessage();
        String[] args = message.split("%=%");

        // Envia uma mensagem a um jogador
        if(channel.equals("message")) {
            String stringUUID = args[0];
            String msg = args[1];
            UUID uuid = UUID.fromString(stringUUID);

            // Verifica se o jogador está online nessa instância
            ProxiedPlayer player;
            if((player = plugin.getProxy().getPlayer(uuid)) != null)
                // Envia a mensagem
                player.sendMessage(RedisManager.convert(msg));
        }

        // Envia um jogador a outro server do BungeeCord
        else if(channel.equals("send")) {
            String stringUUID = args[0];
            String target = args[1];
            UUID uuid = UUID.fromString(stringUUID);

            // Verifica se o jogador está online nessa instância
            ProxiedPlayer player;
            if((player = plugin.getProxy().getPlayer(uuid)) != null) {
                // Verifica se o jogador já está conectado no destino
                if(player.getServer().getInfo().getName().equalsIgnoreCase(target))
                    return;

                // Envia o jogador
                player.connect(plugin.getProxy().getServerInfo(target));
            }
        }
    }

}