package br.com.minecraftgames.redismanager.processor.events;

import java.util.UUID;

/**
 * <h1>Evento de recebimento do status atual do tell e quote do jogador</h1>
 *
 * @author Ramon, Lucas
 */
public class PlayerTellAndQuoteStatusReceived implements ProcessorEvent {

    private final UUID uuid;
    private final boolean isTellOn;
    private final boolean isQuoteOn;

    /**
     * Construtor do evento
     *
     * @param uuid UUID do jogador
     * @param isTellOn status do tell do jogador
     * @param isQuoteOn status do quote do jogador
     */
    public PlayerTellAndQuoteStatusReceived(UUID uuid, boolean isTellOn, boolean isQuoteOn) {
        this.uuid = uuid;
        this.isTellOn = isTellOn;
        this.isQuoteOn = isQuoteOn;
    }

    /**
     * Retorna o uuid do jogador do evento
     *
     * @return UUID do jogador
     */
    public UUID getUUID() {
        return uuid;
    }

    /**
     * Retorna o status do tell do jogador
     *
     * @return {@code true} para tell ativo e {@code false} para tell desativado
     */
    public boolean isTellOn() {
        return isTellOn;
    }

    /**
     * Retorna o status das citação do jogador
     *
     * @return {@code true} para citações ativas e {@code false} para citações desativadas
     */
    public boolean isQuoteOn() {
        return isQuoteOn;
    }
}