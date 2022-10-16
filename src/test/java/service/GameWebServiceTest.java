package service;

import config.data.H2DataBaseInitializer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import repository.ClanGoldTransferRepository;
import repository.clan.impl.ClanRepositoryJdbcImpl;
import repository.user.impl.UserRepositoryJdbcImpl;
import service.addgold.UserTransferGoldService;
import service.clan.ClanService;
import service.clan.gold.ClanGoldService;
import service.clan.impl.ClanServiceImpl;
import service.user.gold.UserGoldService;
import service.user.impl.UserServiceImpl;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameWebServiceTest {

    private static final Long userId = 1L;
    private static final Long clanId = 1L;

    final ClanRepositoryJdbcImpl clanRepositoryJdbc = new ClanRepositoryJdbcImpl();
    final ClanService clanService = new ClanServiceImpl(clanRepositoryJdbc);
    final ClanGoldService clanGoldService = new ClanGoldService(clanService);

    final UserRepositoryJdbcImpl userRepositoryJdbc = new UserRepositoryJdbcImpl();
    final UserServiceImpl userService = new UserServiceImpl(userRepositoryJdbc);
    final UserGoldService userGoldService = new UserGoldService(userService);

     ClanGoldTransferRepository clanGoldTransferRepository = new ClanGoldTransferRepository();

    final UserTransferGoldService userTransferGoldService = new UserTransferGoldService(clanGoldService,
            userGoldService, userService, clanGoldTransferRepository);

    final GameWebService gameWebService = new GameWebService(clanService, userService, userTransferGoldService);


    @BeforeEach
    void initDb() {
        H2DataBaseInitializer.init();
    }

    @RepeatedTest(10)
    @SneakyThrows
    void testAddGoldToClanFromUser() {
        var clanGoldBeforeTransfer = clanService.get(clanId).getGold();
        var userGoldBeforeTransfer = userService.get(userId).getGold();

        var completableFutures = IntStream.range(0, 100)
                .mapToObj(n -> CompletableFuture.runAsync(() ->
                            gameWebService.addGoldToClanFromUser(clanId, userId, 1)))
                .toArray(CompletableFuture[]::new);
        CompletableFuture.allOf(completableFutures).get();

        var clanGoldAfterTransfer = clanService.get(clanId).getGold();
        var userGoldAfterTransfer = userService.get(userId).getGold();

        assertEquals(clanGoldBeforeTransfer + 100, clanGoldAfterTransfer);
        assertEquals(userGoldBeforeTransfer - 100, userGoldAfterTransfer);

    }

    @RepeatedTest(10)
    @SneakyThrows
    void testTransferGoldToUserFromClan() {
        var clanGoldBeforeTransfer = clanService.get(clanId).getGold();
        var userGoldBeforeTransfer = userService.get(userId).getGold();

        var completableFutures = IntStream.range(0, 100)
                .mapToObj(n -> CompletableFuture.runAsync(() ->
                        gameWebService.transferGoldToUserFromClan(clanId, userId, 1)))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(completableFutures).get();

        var clanGoldAfterTransfer = clanService.get(clanId).getGold();
        var userGoldAfterTransfer = userService.get(userId).getGold();

        assertEquals(clanGoldBeforeTransfer - 100, clanGoldAfterTransfer);
        assertEquals(userGoldBeforeTransfer + 100, userGoldAfterTransfer);
    }
}