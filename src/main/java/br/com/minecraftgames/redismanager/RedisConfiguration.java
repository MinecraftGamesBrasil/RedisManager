package br.com.minecraftgames.redismanager;

import br.com.minecraftgames.redismanager.utils.Instances;
import br.com.minecraftgames.redismanager.utils.ServerData;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <h1>Inicia variáveis do Redis</h1>
 *
 * @author Ramon, Lucas
 */
public class RedisConfiguration {

    private RedisManager plugin;

    public RedisConfiguration(RedisManager instance) {
        this.plugin = instance;

        setInstanceID();
        setupInstance();
        loadConfiguration();
    }

    public final static String BUNGEE = "bungee";

    public static int instanceID;
    public static Set<String> instancesIDs;
    public static int globalCount;

    public Set<String> tellOff = new HashSet<String>();
    public Set<String> quoteOff = new HashSet<String>();
    public Set<String> whitelistedLobbys = new HashSet<String>();
    public boolean isChatOff;

    /**
     * Registra o ID da instancia no Redis
     */
    private void setInstanceID() {
        // Verifica existência da pasta do plugin e a cria, caso não exista
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists())
            dataFolder.mkdir();

        // Verifica a existência do arquivo de configuração do plugin
        Configuration config = null;
        File file = new File(dataFolder, "config.yml");
        try {
            if (file.exists())
                config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (config == null) {
            // Retorna o erro e desliga a instância caso o plugin não tenha sido configurado
            plugin.getLogger().info("O ID da instância não foi configurado");
            plugin.getProxy().stop();
        } else {
            // Recupera o valor do ID da instância e assimila a variável
            instanceID = config.getInt("instance-ID");
        }
    }

    /**
     * Carrega configuração armazenada no Redis
     */
    public void setupInstance() {
        // Adciona a instância na lista de instâncias ativas
        JedisPool pool = Redis.getPool();
        Jedis rsc = pool.getResource();
        try {
            rsc.sadd("instances", BUNGEE + instanceID);
        } catch (JedisConnectionException e) {
            pool.returnBrokenResource(rsc);
        } finally {
            pool.returnResource(rsc);
        }

        // Programa a atualização periodica
        Runnable recache = new Runnable() {
            public void run() {
                // Atualiza o tempo do último contato da instância com o Redis
                JedisPool pool = Redis.getPool();
                Jedis rsc = pool.getResource();
                try {
                    rsc.set("instance:" + BUNGEE + instanceID + ":heartbeats", String.valueOf(System.currentTimeMillis()));
                } catch (JedisConnectionException e) {
                    pool.returnBrokenResource(rsc);
                } finally {
                    pool.returnResource(rsc);
                }

                // Verifica quais instâncias ainda estão ativas
                instancesIDs = Instances.getInstancesIDs();

                // Recupera o valor da soma de jogadores online de todas as instâncias
                globalCount = Instances.getGlobalCount();
            }
        };

        // Cria a task de atualização periódica
        plugin.getProxy().getScheduler().schedule(plugin, recache, 0L, 1L, TimeUnit.SECONDS);
    }

    /**
     * Carrega a configuração armazenada no Redis
     */
    public void loadConfiguration() {
        // Lista do UUID(String) dos jogadores com tell desativado
        tellOff = ServerData.getTellOff();

        // Lista do UUID(String) dos jogadores com citações desativadas
        quoteOff = ServerData.getQuoteOff();

        // Lista dos lobbys em manutenção
        whitelistedLobbys = ServerData.getWhitelistedLobbys();

        // Boolean que se refere ao status atual do chat normal (se está ligado ou não)
        isChatOff = ServerData.getChatState();
    }
}