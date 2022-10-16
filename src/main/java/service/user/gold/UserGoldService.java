package service.user.gold;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.User;
import service.user.UserService;
import util.lock.UserThreadLocker;

@Slf4j
@RequiredArgsConstructor
public class UserGoldService {
    private final UserService userService;

    public User increaseGold(Long userId, Integer gold) {
            var user = userService.get(userId);
            user.setGold(user.getGold() + gold);
            return user;
    }

    public User decreaseGold(Long userId, Integer gold) {
            var user = userService.get(userId);
            if (user.getGold() < gold) {
                log.error("User id={} has not enough money to decrease. Current gold={}," +
                        " gold to decrease={}", userId, user.getGold(), gold);
                throw new IllegalArgumentException("Not enough money");
            }
            user.setGold(user.getGold() - gold);
            return user;
    }
}
