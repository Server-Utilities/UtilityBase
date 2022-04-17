package tv.quaint.utilitybase.utils.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import tv.quaint.utilitybase.PluginBase;
import tv.quaint.utilitybase.config.storage.StorageComponent;
import tv.quaint.utilitybase.config.storage.StorageManager;
import tv.quaint.utilitybase.utils.MessagingUtils;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

public class MariaUtils {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();

            config.setJdbcUrl(PluginBase.CONFIG.configuredMaria.uri
                    .replace("%host%", PluginBase.CONFIG.configuredMaria.host)
                    .replace("%port%", PluginBase.CONFIG.configuredMaria.port)
                    .replace("%database%", PluginBase.CONFIG.configuredMaria.name)
            );
            config.setUsername(PluginBase.CONFIG.configuredMaria.user);
            config.setPassword(PluginBase.CONFIG.configuredMaria.pass);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("allowMultiQueries", "true");
            ds = new HikariDataSource(config);

            return ds.getConnection();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public enum Statements {
        CREATE_TABLE(
                "CREATE TABLE IF NOT EXISTS `" + PluginBase.CONFIG.configuredMaria.table + "` (" +
                        " `identifier` varchar(64) NOT NULL," +
                        " `value` DOUBLE(64) NOT NULL" +
                        " );"
        ),
        ;

        public String statement;

        Statements(String statement) {
            this.statement = statement;
        }
    }

    public static void verifyTables()
    {
        String query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = ? AND table_name = ?;";

        try {
            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, PluginBase.CONFIG.configuredMaria.name);
                statement.setString(2, PluginBase.CONFIG.configuredMaria.table);

                ResultSet resultSet = statement.executeQuery();

                int value = resultSet.getInt(1);

                connection.close();
                if (value != 0) return;
            }

            query = Statements.CREATE_TABLE.statement;
            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.execute();
            }
        } catch (SQLException e) {
            if (e.getMessage().endsWith("doesn't exist")) {
                try {
                    query = Statements.CREATE_TABLE.statement;
                    try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                        statement.execute();
                    }
                } catch (SQLException ex) {
                    if (e.getMessage() != null) MessagingUtils.warn("SQL Error (while creating new verified tables): " + e.getMessage());
                }
            } else if (e.getMessage() != null) MessagingUtils.warn("SQL Error (while verifying tables): " + e.getMessage());
        }
    }

    public static boolean existsOnTheDB(String identifier)
    {
        String query = "SELECT COUNT(*) FROM " + PluginBase.CONFIG.configuredMaria.table + " WHERE identifier = ?;";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, identifier);

            ResultSet resultSet = statement.executeQuery();

            boolean returnValue = false;
            if (resultSet.next())
                returnValue = resultSet.getBoolean(1);
            connection.close();
            return returnValue;

        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.warn("SQL Error (while checking if user exists on database): " + e.getMessage());
        }
        return false;
    }

    public static void saveStorageComponent(StorageComponent component) {
        String query = "REPLACE INTO " + PluginBase.CONFIG.configuredMaria.table + " (identifier, value) VALUES (?, ?);";

        if (! existsOnTheDB(component.key)) {
            StorageManager.createFirstComponent(component.key);
            return;
        }

        try {
            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, component.key);
                statement.setDouble(2, component.value);

                statement.execute();
            }
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.warn("SQL Error (while saving StorageComponent): " + e.getMessage());
        }
    }

    public static void createFirstStorageComponent(StorageComponent component) {
        String query = "INSERT INTO " + PluginBase.CONFIG.configuredMaria.table + " (identifier, value) VALUES (?, ?);";

        if (! existsOnTheDB(component.key)) {
            StorageManager.createFirstComponent(component.key);
            return;
        }

        try {
            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, component.key);
                statement.setDouble(2, component.value);

                statement.execute();
            }
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.warn("SQL Error (while saving StorageComponent): " + e.getMessage());
        }
    }

    public static double getStorageComponentValue(String key) {
        String query = "SELECT `value` FROM " + PluginBase.CONFIG.configuredMaria.table + " WHERE `identifier` = ?;";

        if (! existsOnTheDB(key)) {
            StorageManager.createFirstComponent(key);
        }

        try {
            try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, key);

                ResultSet resultSet = statement.executeQuery();

                connection.close();
                return resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            if (e.getMessage() != null) MessagingUtils.warn("SQL Error (while saving StorageComponent): " + e.getMessage());
            return 0;
        }
    }
}
