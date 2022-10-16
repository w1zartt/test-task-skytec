package service.addgold;

import lombok.extern.slf4j.Slf4j;
import model.Clan;
import lombok.RequiredArgsConstructor;
import model.User;
import model.UserRank;
import repository.ClanGoldTransferRepository;
import service.clan.gold.ClanGoldService;
import service.user.UserService;
import service.user.gold.UserGoldService;
import util.lock.ClanThreadLocker;
import util.lock.UserThreadLocker;

@RequiredArgsConstructor
@Slf4j
public class UserTransferGoldService {

    private volatile UserThreadLocker userThreadLocker = new UserThreadLocker();
    private volatile ClanThreadLocker clanThreadLocker = new ClanThreadLocker();

    private final ClanGoldService clanGoldService;
    private final UserGoldService userGoldService;
    private final UserService userService;
    private final ClanGoldTransferRepository clanGoldTransferRepository;

    public Clan addGoldToClan(Long clanId, Long userId, Integer gold) {
        try {
            userThreadLocker.lock(userId);
            clanThreadLocker.lock(clanId);
            log.info("Adding {} gold to clan id={} from user id={}", gold, clanId, userId);
            var user = userGoldService.decreaseGold(userId, gold);
            var clan = clanGoldService.increaseGold(clanId, gold);

            log.info("{} gold is added to clan id={}(name: {}) from user id ={} " +
                            "(name: {}). Current clan gold={}, current user gold={}",
                    gold, clanId, clan.getName(), userId, user.getName(), clan.getGold(), user.getGold());

            return clanGoldTransferRepository.updateClanAndUserGold(clan, user);
        } finally {
            userThreadLocker.unlock(userId);
            clanThreadLocker.unlock(clanId);
        }
    }

    public Clan takeGoldFromClan(Long clanId, Long userId, Integer gold) {
        try {
            userThreadLocker.lock(userId);
            clanThreadLocker.lock(clanId);
            log.info("User id={} taking {} gold from clan id={}", userId, gold, clanId);
            final User user = userService.get(userId);
            if (UserRank.LEAD.equals(user.getRank())) {
                final Clan clan = clanGoldService.decreaseGold(clanId, gold);
                var userafterIncrease = userGoldService.increaseGold(userId, gold);
                log.info("{} gold is added to user id={}(name: {}) from clan id ={}" +
                                "(name: {}). Current user gold={}, current clan gold={}",
                        gold, userId, user.getName(), clanId, clan.getName(), user.getGold(),
                        clan.getGold());
                return clanGoldTransferRepository.updateClanAndUserGold(clan, userafterIncrease);
            } else {
                throw new IllegalStateException("Current user can't to take money from clan");
            }
        } finally {
            userThreadLocker.unlock(userId);
            clanThreadLocker.unlock(clanId);
        }
    }
}
