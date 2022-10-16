package rest.handler.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import config.exception.ExceptionHandler;
import config.exception.MethodNotAllowedException;
import config.rest.Constants;
import config.rest.ResponseEntity;
import config.rest.StatusCode;
import rest.handler.Handler;
import model.User;
import service.GameWebService;

import java.io.InputStream;

public class CreateUserHandler extends Handler {

    private final GameWebService gameWebService;

    public CreateUserHandler(ObjectMapper objectMapper, ExceptionHandler exceptionHandler,
                             GameWebService gameWebService) {
        super(objectMapper, exceptionHandler);
        this.gameWebService = gameWebService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws Exception {
        byte[] response;
        if ("GET".equals(exchange.getRequestMethod())) {
            var userResponseEntity = doGet(exchange.getRequestURI().getPath());
            exchange.getResponseHeaders().putAll(userResponseEntity.getHeaders());
            exchange.sendResponseHeaders(userResponseEntity.getStatusCode().getCode(), 0);
            response = writeResponse(userResponseEntity.getBody());
        } else if ("POST".equals(exchange.getRequestMethod())) {
            InputStream requestBody = exchange.getRequestBody();
            var userResponseEntity = doPost(requestBody);
            exchange.getResponseHeaders().putAll(userResponseEntity.getHeaders());
            exchange.sendResponseHeaders(userResponseEntity.getStatusCode().getCode(), 0);
            response = writeResponse(userResponseEntity.getBody());
        }
        else {
            throw new MethodNotAllowedException(
                    "Method " + exchange.getRequestMethod() + " is not allowed for " + exchange.getRequestURI());        }
        exchange.getResponseBody().write(response);
        exchange.getResponseBody().flush();
        exchange.close();
    }

    private ResponseEntity<User> doPost(InputStream is) {
        final User user = readRequest(is, User.class);
        final User savedUser = gameWebService.createUser(user);

        return new ResponseEntity<>(savedUser,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }

    private ResponseEntity<User> doGet(String path) {
        var id = extractPathVariable(path);
        var user = gameWebService.getUser(id);
        return new ResponseEntity<>(user,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }
}
