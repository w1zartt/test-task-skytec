package service.user;

import model.User;

public interface UserService {
    User get(Long id);
    User save(User user);
    User update(User user);
}
