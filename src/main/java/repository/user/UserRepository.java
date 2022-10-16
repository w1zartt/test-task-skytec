package repository.user;

import model.User;

public interface UserRepository {

    User findById(Long id);
    User save(User user);
    User update(User user);
}
