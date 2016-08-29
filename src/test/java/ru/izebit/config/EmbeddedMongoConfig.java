package ru.izebit.config;

import com.github.fakemongo.Fongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import static ru.izebit.config.BaseConfiguration.Profile.TEST;


@Configuration
@PropertySource("classpath:application.properties")
public class EmbeddedMongoConfig {

    @Bean
    @Profile(TEST)
    public MongoClient mongoClient() {
        Fongo fongo = new Fongo("InMemoryMongo");
        return fongo.getMongo();
    }
}
