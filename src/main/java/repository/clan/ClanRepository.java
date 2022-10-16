package repository.clan;

import model.Clan;

public interface ClanRepository {
    Clan findById(Long id);
    Clan save(Clan clan);
    Clan update(Clan clan);

}
