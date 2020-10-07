package okon.Nautilus;

import okon.Nautilus.exception.AppException;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteConnection implements Closeable {
    private final Connection connection;
    private final String sql;

    public SqliteConnection(Job job) {
        try {
            connection = job.getDataSource().getConnection();
            sql = job.getSql();
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    public void execute() {
        try(Statement statement = connection.createStatement();) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new AppException(e);
        }
    }
}