package ar.kochh;

import java.util.Collections;
import java.util.Map;

public class Response {

	private final String message;
	private final Map<String, Object> input;

	public Response(String message, Map<String, Object> input) {
		this.message = message;
		this.input = input;
	}

	public String getMessage() {
		return this.message;
	}

	public Map<String, Object> getInput() {
		return this.input;
	}

	public static ApiGatewayResponse createFailure(String failMsg, int statusCode, Map<String, Object> input) {
		return ApiGatewayResponse.builder()
				.setStatusCode(statusCode)
				.setObjectBody(new Response(failMsg, input))
				.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
				.build();
	}

}
