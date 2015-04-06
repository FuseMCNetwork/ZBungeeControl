package net.fusemc.zbungeecontrol.server;

/**
 * Created by Marco on 28.07.2014.
 */
public class Server {

    private String name;
    private String ip;
    private String port;
    private ServerData serverStats;

    public Server(String name) {

    }

    public Server(String name, String ip, String port) {
        this(name, ip, port, new ServerData(name));
    }

    public Server(String name, String ip, String port, ServerData serverStats) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.serverStats = serverStats;
    }

    public void update() {

    }

    public String getName() {
        return name;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public ServerData getServerStats() {
        return serverStats;
    }

}
