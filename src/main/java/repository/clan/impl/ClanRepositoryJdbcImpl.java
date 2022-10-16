package repository.clan.impl;

import config.exception.InvalidRequestException;
import config.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import model.Clan;
import repository.clan.ClanRepository;

import java.sql.*;

import static config.data.DbConstants.*;

@Slf4j
public class ClanRepositoryJdbcImpl implements ClanRepository {

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM clan where id = ?";
    private static final String INSERT_QUERY = "INSERT INTO clan (name, gold) values(?,?)";
    private static final String UPDATE_QUERY = "UPDATE clan SET name=?, gold=? WHERE id = ?";

    @Override
    public Clan findById(Long id) {
        log.debug("Start to find clan by id {}", id);
        Clan clan = null;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_QUERY)) {
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("name");
                int gold = resultSet.getInt("gold");
                clan = new Clan(id, name, gold);
            } else {
                throw new ResourceNotFoundException(String.format(
                        "Clan with id %s is not found", id));
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidRequestException(String.format("SQLException while finding clan: %s", e.getMessage()));
        }
        log.debug("Clan with id {} is found. {}", id, clan);
        return clan;
    }

    @Override
    public Clan save(Clan clan) {
        log.debug("Starting to save new clan {}", clan);
        Clan savedClan = null;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, clan.getName());
            statement.setInt(2, clan.getGold());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new InvalidRequestException("Creating clan failed, no rows affected");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    savedClan = new Clan(generatedKeys.getLong(1), clan.getName(), clan.getGold());
                }
                else {
                    throw new InvalidRequestException("Creating clan failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidRequestException(String.format("SQLException while saving clan: %s", e.getMessage()));
        }
        log.info("New clan is saved {}", savedClan);
        return savedClan;
    }

    @Override
    public Clan update(Clan clan) {
        log.debug("Starting to update clan with id {}", clan.getId());
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
            statement.setString(1, clan.getName());
            statement.setInt(2, clan.getGold());
            statement.setLong(3, clan.getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new InvalidRequestException("Updating clan failed, no rows affected");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InvalidRequestException(String.format("SQLException while updating clan: %s", e.getMessage()));
        }
        log.info("Clan with id {} is updated", clan.getId());
        return clan;
    }

    private Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException throwables) {
            throw new IllegalStateException("Can't get connection to database");
        }
    }
}
