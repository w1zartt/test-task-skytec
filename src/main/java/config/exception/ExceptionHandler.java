package config.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import config.rest.Constants;

import java.io.IOException;
import java.io.OutputStream;

public class ExceptionHandler {
    private final ObjectMapper objectMapper;

    public ExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void handle(Throwable throwable, HttpExchange exchange) {
        try {
            throwable.printStackTrace();
            exchange.getResponseHeaders().set(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
            ErrorResponse response = getErrorResponse(throwable, exchange);
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(objectMapper.writeValueAsBytes(response));
            responseBody.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ErrorResponse getErrorResponse(Throwable throwable, HttpExchange exchange) throws IOException {
        var responseBuilder = ErrorResponse.builder();
        if (throwable instanceof ResourceNotFoundException) {
            ResourceNotFoundException exc = (ResourceNotFoundException) throwable;
            responseBuilder.message(exc.getMessage()).code(exc.getCode());
            exchange.sendResponseHeaders(exc.getCode(), 0);
        } else if (throwable instanceof MethodNotAllowedException) {
            MethodNotAllowedException exc = (MethodNotAllowedException) throwable;
            responseBuilder.message(exc.getMessage()).code(exc.getCode());
            exchange.sendResponseHeaders(exc.getCode(), 0);
        } else {
                responseBuilder.code(500).message(throwable.getMessage());
                exchange.sendResponseHeaders(500, 0);
        }
        return responseBuilder.build();
    }
}
