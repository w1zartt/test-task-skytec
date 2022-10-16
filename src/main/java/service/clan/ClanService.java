package service.clan;

import model.Clan;

public interface ClanService {
    Clan get(long clanId);
    Clan save(Clan clan);
    Clan update(Clan clan);
}
