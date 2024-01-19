package com.project.template.logger.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

@NonNullApi
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.data.database}")
    private String dataBase;

    @Override
    protected String getDatabaseName() {
        return dataBase;
    }

    @Override
    public MongoClient mongoClient() {
        return MongoClients.create();
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
