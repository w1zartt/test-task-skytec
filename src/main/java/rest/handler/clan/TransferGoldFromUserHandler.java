package rest.handler.clan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import config.exception.ExceptionHandler;
import config.exception.MethodNotAllowedException;
import config.rest.Constants;
import config.rest.ResponseEntity;
import config.rest.StatusCode;
import rest.handler.Handler;
import model.Clan;
import service.GameWebService;

public class TransferGoldFromUserHandler extends Handler {

    private final GameWebService gameWebService;

    public TransferGoldFromUserHandler(ObjectMapper objectMapper, ExceptionHandler exceptionHandler,
                                       GameWebService gameWebService) {
        super(objectMapper, exceptionHandler);
        this.gameWebService = gameWebService;
    }


    @Override
    protected void execute(HttpExchange exchange) throws Exception {
        byte[] response;
        if ("POST".equals(exchange.getRequestMethod())) {
            var clanResponseEntity = doPost(exchange.getRequestURI().getRawQuery());
            exchange.getResponseHeaders().putAll(clanResponseEntity.getHeaders());
            exchange.sendResponseHeaders(clanResponseEntity.getStatusCode().getCode(),
                    0);
            response = writeResponse(clanResponseEntity.getBody());
        } else {
            throw new MethodNotAllowedException(
                    "Method " + exchange.getRequestMethod() + " is not allowed for " + exchange.getRequestURI());
        }
        exchange.getResponseBody().write(response);
        exchange.getResponseBody().flush();
        exchange.getResponseBody().close();
    }

    private ResponseEntity<Clan> doPost(String path) {
        var map = splitQuery(path);
        var userId = Long.parseLong(map.get("user-id").get(0));
        var clanId = Long.parseLong(map.get("clan-id").get(0));
        var sum = Integer.parseInt(map.get("sum").get(0));
        var clan = gameWebService.addGoldToClanFromUser(clanId, userId, sum);

        return new ResponseEntity<>(clan,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);


    }
}
