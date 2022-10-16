package repository.user.impl;

import config.exception.InvalidRequestException;
import config.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.User;
import model.UserRank;
import repository.user.UserRepository;

import java.sql.*;

import static config.data.DbConstants.*;

@Slf4j
public class UserRepositoryJdbcImpl implements UserRepository {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM player where id = ?";
    private static final String INSERT_QUERY = "INSERT INTO player (name, gold, rank) values(?,?,?)";
    private static final String UPDATE_QUERY = "UPDATE player SET name=?, gold=?, rank=? WHERE id = ?";

    @Override
    public User findById(Long id) {
        log.debug("Start to user by id {}", id);
        User user = null;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_QUERY)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                String rank = resultSet.getString("rank");
                int gold = resultSet.getInt("gold");
                user = new User(id, name, gold, UserRank.fromString(rank));
            } else {
                throw new ResourceNotFoundException(String.format(
                        "User with id %s is not found", id));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidRequestException(String.format("SQLException while finding user: %s",
                    e.getMessage()));
        }
        log.debug("User with id {} is found. {}", id, user);
        return user;
    }

    @Override
    public User save(User user) {
        log.debug("Starting to save new user {}", user);
        User savedUser = null;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setInt(2, user.getGold());
            statement.setString(3, user.getRank().getAsText());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new InvalidRequestException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    savedUser = new User(generatedKeys.getLong(1), user.getName(),
                            user.getGold(), user.getRank());
                }
                else {
                    throw new InvalidRequestException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidRequestException(String.format("SQLException while saving user: %s",
                    e.getMessage()));
        }
        log.info("New user is saved {}", savedUser);
        return savedUser;
    }

    @Override
    public User update(User user) {
        log.debug("Starting to update user with id {}", user.getId());
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            statement.setString(1, user.getName());
            statement.setInt(2, user.getGold());
            statement.setString(3, user.getRank().getAsText());
            statement.setLong(4, user.getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new InvalidRequestException("Creating user failed, no rows affected.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidRequestException(String.format("SQLException while updating user: %s",
                    e.getMessage()));
        }
        log.info("User with id {} is updated", user.getId());
        return user;
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException throwables) {
            throw new IllegalStateException("Can't get connection to database");
        }
    }
}
