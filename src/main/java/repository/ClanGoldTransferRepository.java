package repository;

import config.exception.InvalidRequestException;
import model.Clan;
import model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static config.data.DbConstants.*;

/**
 * Этот странный репозиторий создан для поддержки транзакционности.
 * Таким образом, либо у клана и пользователя, который переводит золото, вместе изменяется баланс,
 * либо в случае непредвиденной ошибки баланс не меняется ни у кого
 */
public class ClanGoldTransferRepository {

    private static final String UPDATE_CLAN_QUERY = "UPDATE clan SET name=?, gold=? WHERE id = ?";
    private static final String UPDATE_USER_QUERY = "UPDATE player SET name=?, gold=?, rank=? WHERE id = ?";

    public Clan updateClanAndUserGold(Clan clan, User user) {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            var clanStatement = connection.prepareStatement(UPDATE_CLAN_QUERY);
            clanStatement.setString(1, clan.getName());
            clanStatement.setInt(2, clan.getGold());
            clanStatement.setLong(3, clan.getId());
            clanStatement.executeUpdate();
            var userStatement = connection.prepareStatement(UPDATE_USER_QUERY);
            userStatement.setString(1, user.getName());
            userStatement.setInt(2, user.getGold());
            userStatement.setString(3, user.getRank().getAsText());
            userStatement.setLong(4, user.getId());
            userStatement.executeUpdate();
            connection.commit();
        } catch (SQLException throwables) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new InvalidRequestException("Something went wrong while saving clan and user");
            }
            throwables.printStackTrace();
            throw new InvalidRequestException("Something went wrong while saving clan and user");
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                throw new InvalidRequestException("Something went wrong while saving clan and user");
            }
        }
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
