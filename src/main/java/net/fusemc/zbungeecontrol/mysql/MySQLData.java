package net.fusemc.zbungeecontrol.mysql;


public class MySQLData {

    private String hostname = "";
    private String port = "";
    private String database = "";
    private String user = "";
    private String password = "";
    private String id = "";

    public MySQLData() {

    }

    public MySQLData(String hostname, String port, String database, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = username;
        this.password = password;
        this.id = database;
    }

    public String getHostname() {
        return this.hostname;
    }

    public String getPort() {
        return this.port;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUser() {
        return this.user;
    }

    public String getPassword() {
        return this.password;
    }

    public String getId() {
        return id;
    }
}