package ar.kochh;

import ar.kochh.db.DynamoDBAdapter;
import ar.kochh.db.DynamoDBModule;
import ar.kochh.model.Transaction;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class PostTransactionHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(PostTransactionHandler.class);
	private final Injector dbInjector = Guice.createInjector(new DynamoDBModule());


	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);
		try {

			Map<String, String> pathParameters = (Map<String, String>)input.get("pathParameters");
			String account_id = pathParameters.get("account_id");

			JsonNode jsonBody = new ObjectMapper().readTree((String)input.get("body"));
			LOG.info((String)input.get("body"));
			LOG.info(String.join(", ", new Iterable<String>() {
				@Override
				public Iterator<String> iterator() {
					return jsonBody.fieldNames();
				}
			}));
			String transaction_id = jsonBody.get("transaction_id").asText();
			float amount = (float)jsonBody.get("amount").asDouble();

			// Create the transaction
			Transaction transaction = new Transaction();
			transaction.setAccount_id(account_id);
			transaction.setTransaction_date(new Date(System.currentTimeMillis()));
			transaction.setTransaction_id(transaction_id);
			transaction.setAmount(amount);

			dbInjector.getInstance(DynamoDBAdapter.class).putTransaction(transaction);
		} catch  (JsonProcessingException e) {
			LOG.error(e, e);
			return Response.createFailure("Failure reading JSON request", 400, input);
		} catch (IOException e) {
			LOG.error(e, e);
			return Response.createFailure("Failure reading request", 500, input);
		} catch (Exception e) {
			LOG.error(e, e);
			return Response.createFailure("Failure putting x-action", 500, input);
		}

		Response responseBody = new Response("Transaction added successfully", input);
		return ApiGatewayResponse.builder()
				.setStatusCode(200)
				.setObjectBody(responseBody)
				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
				.build();
	}


}
