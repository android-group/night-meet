package ru.izebit.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;

/**
 * Created by Artem Konovalov on 8/28/16.
 */

@Configuration
@PropertySource("classpath:database.properties")
public class DataSourceConfiguration {

    @Autowired
    private Environment properties;


    @Bean
    public MongoTemplate mongoTemplate(@Autowired MongoDbFactory mongoDbFactory) {
        return new MongoTemplate(mongoDbFactory);
    }

    @Bean
    public MongoDbFactory mongoDbFactory(@Autowired MongoClient mongoClient) {
        return new SimpleMongoDbFactory(mongoClient, properties.getProperty("mongo.database"));
    }

    @Bean
    public MongoClient mongoClient() throws UnknownHostException {
        Builder builder = MongoClientOptions
                .builder()
                .connectTimeout(properties.getRequiredProperty("mongo.timeout", Integer.class))
                .socketTimeout((int) TimeUnit.SECONDS.toMillis(60))
                .socketKeepAlive(true)
                .connectionsPerHost(8);

        MongoCredential mongoCredential = MongoCredential.createPlainCredential(
                properties.getProperty("mongo.username"),
                properties.getProperty("mongo.database"),
                properties.getProperty("mongo.password").toCharArray());


        ServerAddress serverAddress = new ServerAddress(
                properties.getProperty("mongo.host"),
                properties.getProperty("mongo.port", Integer.class));

        return new MongoClient(serverAddress, singletonList(mongoCredential), builder.build());
    }

}