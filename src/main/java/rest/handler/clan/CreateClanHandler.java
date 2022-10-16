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

import java.io.InputStream;

public class CreateClanHandler extends Handler {

    private final GameWebService gameWebService;

    public CreateClanHandler(GameWebService gameWebService, ExceptionHandler exceptionHandler,
                             ObjectMapper objectMapper) {
        super(objectMapper, exceptionHandler);
        this.gameWebService = gameWebService;
    }

    @Override
    protected void execute(HttpExchange exchange) throws Exception {
        byte[] response;
        if ("GET".equals(exchange.getRequestMethod())) {
            var clanResponseEntity = doGet(exchange.getRequestURI().getPath());
            exchange.getResponseHeaders().putAll(clanResponseEntity.getHeaders());
            exchange.sendResponseHeaders(clanResponseEntity.getStatusCode().getCode(), 0);
            response = writeResponse(clanResponseEntity.getBody());
        } else if ("POST".equals(exchange.getRequestMethod())) {
            final InputStream requestBody = exchange.getRequestBody();
            final ResponseEntity<Clan> clanResponseEntity = doPost(requestBody);
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
    private ResponseEntity<Clan> doPost(InputStream is) {
        final Clan clan = readRequest(is, Clan.class);
        final Clan savedClan = gameWebService.createClan(clan);

        return new ResponseEntity<>(savedClan,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }

    private ResponseEntity<Clan> doGet(String path) {
        var id = extractPathVariable(path);
        var clan = gameWebService.getClan(id);

        return new ResponseEntity<>(clan,
                getHeaders(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON), StatusCode.OK);
    }


}
