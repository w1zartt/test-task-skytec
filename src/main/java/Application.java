import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.data.H2DataBaseInitializer;
import config.exception.ExceptionHandler;
import config.rest.HttpServerConfig;
import repository.ClanGoldTransferRepository;
import rest.handler.clan.CreateClanHandler;
import rest.handler.clan.TransferGoldFromUserHandler;
import rest.handler.clan.TransferGoldToUserHandler;
import rest.handler.user.CreateUserHandler;
import repository.clan.impl.ClanRepositoryJdbcImpl;
import repository.user.impl.UserRepositoryJdbcImpl;
import service.GameWebService;
import service.addgold.UserTransferGoldService;
import service.clan.ClanService;
import service.clan.gold.ClanGoldService;
import service.clan.impl.ClanServiceImpl;
import service.user.gold.UserGoldService;
import service.user.impl.UserServiceImpl;

import java.io.IOException;

public class Application {
    public static void main(String[] args) throws IOException {

        H2DataBaseInitializer.init();

        final ClanRepositoryJdbcImpl clanRepositoryJdbc = new ClanRepositoryJdbcImpl();
        final ClanService clanService = new ClanServiceImpl(clanRepositoryJdbc);
        final ClanGoldService clanGoldService = new ClanGoldService(clanService);

        final UserRepositoryJdbcImpl userRepositoryJdbc = new UserRepositoryJdbcImpl();
        final UserServiceImpl userService = new UserServiceImpl(userRepositoryJdbc);
        final UserGoldService userGoldService = new UserGoldService(userService);

        var clanGoldTransferRepository = new ClanGoldTransferRepository();

        final UserTransferGoldService userTransferGoldService = new UserTransferGoldService(clanGoldService,
                userGoldService, userService, clanGoldTransferRepository);

        final GameWebService gameWebService = new GameWebService(clanService, userService, userTransferGoldService);

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        var exceptionHandler = new ExceptionHandler(objectMapper);

        //TODO вынести в мапу
        var createClanHandler = new CreateClanHandler(gameWebService, exceptionHandler, objectMapper);
        var createUserHandler = new CreateUserHandler(objectMapper, exceptionHandler, gameWebService);
        var transferGoldFromUserHandler = new TransferGoldFromUserHandler(objectMapper, exceptionHandler, gameWebService);
        var transferGoldToUserHandler = new TransferGoldToUserHandler(objectMapper, exceptionHandler, gameWebService);

        final HttpServerConfig httpServerConfig = new HttpServerConfig(createClanHandler, createUserHandler,
                transferGoldFromUserHandler, transferGoldToUserHandler);
        httpServerConfig.startServer();
    }
}
