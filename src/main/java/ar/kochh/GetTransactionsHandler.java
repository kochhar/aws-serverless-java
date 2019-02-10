package ar.kochh;

import ar.kochh.db.DynamoDBAdapter;
import ar.kochh.db.DynamoDBModule;
import ar.kochh.model.Transaction;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetTransactionsHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(GetTransactionsHandler.class);
	private final Injector dbInjector = Guice.createInjector(new DynamoDBModule());


	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);
		List<Transaction> txs;

		Map<String, String> pathParameters = (Map<String, String>)input.get("pathParameters");
		String account_id = pathParameters.get("account_id");
		try {
			LOG.info("Getting transactions for " + account_id);
			txs = dbInjector.getInstance(DynamoDBAdapter.class).getTransactions(account_id);
		} catch (Exception e) {
			LOG.error(e, e);
			return Response.createFailure("Could not read transaction for account: " + account_id,
					500, input);
		}
		return ApiGatewayResponse.builder()
				.setStatusCode(200)
				.setObjectBody(txs)
				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
				.build();
	}
}
