package service.user.impl;

import lombok.RequiredArgsConstructor;
import model.User;
import repository.user.UserRepository;
import service.user.UserService;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User get(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.update(user);
    }
}
