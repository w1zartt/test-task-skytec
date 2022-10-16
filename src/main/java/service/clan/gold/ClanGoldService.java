package service.clan.gold;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.Clan;
import service.clan.ClanService;

@RequiredArgsConstructor
@Slf4j
public class ClanGoldService {

    private final ClanService clanService;

    public Clan increaseGold(Long clanId, Integer gold) {
        log.debug("Increasing {} gold for clan id={}", gold, clanId);
        final Clan clan = clanService.get(clanId);
        clan.setGold(clan.getGold() + gold);
        return clan;
    }

    public Clan decreaseGold(Long clanId, Integer gold) {
        log.debug("Decreasing {} gold for clan id={}", gold, clanId);
        final Clan clan = clanService.get(clanId);
        if (clan.getGold() < gold) {
            log.error("Clan id={} has not enough money to decrease. Current gold={}," +
                    " gold to decrease={}", clanId, clan.getGold(), gold);
            throw new IllegalArgumentException("Not enough money");
        }
        clan.setGold(clan.getGold() - gold);
        return clan;
    }
}
