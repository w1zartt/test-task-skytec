package service.clan.impl;

import lombok.RequiredArgsConstructor;
import model.Clan;
import repository.clan.ClanRepository;
import service.clan.ClanService;

@RequiredArgsConstructor
public class ClanServiceImpl implements ClanService {

    private final ClanRepository clanRepository;

    @Override
    public Clan get(long clanId) {
        return clanRepository.findById(clanId);
    }

    @Override
    public Clan save(Clan clan) {
        return clanRepository.save(clan);
    }

    @Override
    public Clan update(Clan clan) {
        return clanRepository.update(clan);
    }
}
