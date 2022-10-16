package service;

import model.Clan;
import lombok.RequiredArgsConstructor;
import model.User;
import service.addgold.UserTransferGoldService;
import service.clan.ClanService;
import service.user.UserService;

@RequiredArgsConstructor
public class GameWebService {

    private final ClanService clanService;
    private final UserService userService;
    private final UserTransferGoldService userTransferGoldService;

    public Clan createClan(Clan clan) {
        final Clan save = clanService.save(clan);
        return save;
    }

    public User createUser(User user) {
        return userService.save(user);
    }

    public Clan addGoldToClanFromUser(Long clanId, Long userId, Integer gold) {
        return userTransferGoldService.addGoldToClan(clanId, userId, gold);
    }

    public Clan transferGoldToUserFromClan(Long clanId, Long userId, Integer gold) {
        return userTransferGoldService.takeGoldFromClan(clanId, userId, gold);
    }

    public User getUser(Long id) {
        return userService.get(id);
    }

    public Clan getClan(Long id) {
        return clanService.get(id);
    }
}
