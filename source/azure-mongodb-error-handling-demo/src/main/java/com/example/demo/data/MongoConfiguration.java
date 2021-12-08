package com.example.demo.data;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongoCmdOptions;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.io.IOException;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
@EnableMongoRepositories
@EnableReactiveMongoRepositories
public class MongoConfiguration  implements IMongoCmdOptions {

    private static final String MONGO_DB_URL = "localhost"; //MongoProperties.DEFAULT_URI;
    private static final String MONGO_DB_NAME = "darren";
    private static final int port = MongoProperties.DEFAULT_PORT;
    private MongodExecutable mongo;

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() throws IOException {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        IMongodConfig mongodConfig = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .net(new Net(MONGO_DB_URL, port, Network.localhostIsIPv6()))
                .build();
        mongo = starter.prepare(mongodConfig);
        mongo.start();
        MongoClient client = MongoClients.create(String.format("mongodb://%s:%d", MONGO_DB_URL, port));
        return new ReactiveMongoTemplate(client, MONGO_DB_NAME);
    }

    @Override
    public Integer syncDelay() {
        return null;
    }

    @Override
    public String storageEngine() {
        return null;
    }

    @Override
    public boolean isVerbose() {
        return false;
    }

    @Override
    public boolean useNoPrealloc() {
        return false;
    }

    @Override
    public boolean useSmallFiles() {
        return false;
    }

    @Override
    public boolean useNoJournal() {
        return false;
    }

    @Override
    public boolean enableTextSearch() {
        return false;
    }

    @Override
    public boolean auth() {
        return false;
    }

    @Override
    public boolean master() {
        return false;
    }
}
