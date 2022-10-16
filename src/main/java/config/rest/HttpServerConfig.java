package config.rest;

import com.sun.net.httpserver.HttpServer;
import rest.handler.clan.CreateClanHandler;
import rest.handler.clan.TransferGoldFromUserHandler;
import rest.handler.clan.TransferGoldToUserHandler;
import rest.handler.user.CreateUserHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;

@RequiredArgsConstructor
@Slf4j
public class HttpServerConfig {
    private static final int PORT = 8000;
    private static HttpServer httpServer;
    private static final String CLAN_API = "/api/skytec/clan";
    private static final String USER_API = "/api/skytec/user";
    private static final String USER_TO_CLAN_TRANSFER_API = "/api/skytec/clan/add-gold-from-user";
    private static final String CLAN_TO_USER_TRANSFER_API = "/api/skytec/clan/transfer-gold-to-user";

    private final CreateClanHandler createClanHandler;
    private final CreateUserHandler createUserHandler;
    private final TransferGoldFromUserHandler transferGoldFromUserHandler;
    private final TransferGoldToUserHandler transferGoldToUserHandler;

    public void startServer() throws IOException {
        log.info("Starting http server");
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext(CLAN_API, createClanHandler::handle);
        httpServer.createContext(USER_API, createUserHandler::handle);
        httpServer.createContext(USER_TO_CLAN_TRANSFER_API, transferGoldFromUserHandler::handle);
        httpServer.createContext(CLAN_TO_USER_TRANSFER_API, transferGoldToUserHandler::handle);
        httpServer.setExecutor(null);
        httpServer.start();
        log.info("Http server is started");
    }
}
