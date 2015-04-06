package net.fusemc.zbungeecontrol.mysql;

import snaq.db.ConnectionPool;

import java.sql.*;

public class MySQL {

    private MySQLData mySQLData;
    private ConnectionPool connectionPool;

    public MySQL(MySQLDBType type) {
        this(type.getMySQLData());
    }

    public MySQL(MySQLData mySQLData) {
        this.mySQLData = mySQLData;

        openConnection();
    }

    private void openConnection() {
        try {
            Class<?> c = Class.forName("com.mysql.jdbc.Driver");
            Driver driver = (Driver) c.newInstance();
            DriverManager.registerDriver(driver);
            this.connectionPool = new ConnectionPool("local", 5, 10, 30, 60, "jdbc:mysql://" + mySQLData.getHostname() + ":" + mySQLData.getPort() + "/" + mySQLData.getDatabase(), mySQLData.getUser(), mySQLData.getPassword());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkConnection() {
        if (this.connectionPool == null)
            return false;
        return true;
    }

    public Connection getConnection() throws SQLException {
        return this.connectionPool.getConnection(2000);
    }
}