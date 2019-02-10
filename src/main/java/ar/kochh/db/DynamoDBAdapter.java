package ar.kochh.db;

import ar.kochh.model.Transaction;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class DynamoDBAdapter {
    private Logger LOG = LogManager.getLogger(DynamoDBAdapter.class);
    private final AmazonDynamoDB client;

    @Inject
    public DynamoDBAdapter(AmazonDynamoDB client) {
        this.client = client;
    }

    public List<Transaction> getTransactions(String accountId) throws Exception {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        Map<String, AttributeValue> vals = new HashMap<>();
        vals.put(":val1", new AttributeValue().withS(accountId));
        DynamoDBQueryExpression<Transaction> queryExpression = new DynamoDBQueryExpression<Transaction>()
                .withKeyConditionExpression("account_id = :val1 ")
                .withExpressionAttributeValues(vals);
        return mapper.query(Transaction.class, queryExpression);
    }

    public void putTransaction(Transaction transaction) throws  Exception {
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        mapper.save(transaction);
    }
}

