package ar.kochh.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DynamoDBModule extends AbstractModule {
    private static final Logger LOG = LogManager.getLogger(DynamoDBModule.class);

    @Override
    protected void configure() {
    }

    @Provides
    @Inject
    public AmazonDynamoDB getDynamoDBClient(@Named("region") String region) {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder
                .standard()
                .withRegion(region)
                .build();
        LOG.info("Created DynamoDB client");
        return client;
    }

    @Provides @Named("region")
    public String getRegion() {
        return "ap-south-1";
    }
}
