package net.fusemc.zbungeecontrol.mysql;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fusemc.zbungeecontrol.utils.FileHelper;

import java.io.File;

public enum MySQLDBType {

    PLAYER("player"),
    NETWORK("network");
    private final String name;
    private MySQLData data;

    MySQLDBType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public MySQLData getMySQLData() {
        return data;
    }

    static {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File("plugins/ZBungeeControl/mysql.json");

        if (file.exists()) {
            MySQLData[] sqlDataSet = gson.fromJson(FileHelper.stringFromFile(file), MySQLData[].class);
            for (MySQLData data : sqlDataSet) {
                for (MySQLDBType type : MySQLDBType.values()) {
                    if (data.getId().equals(type.getName())) {
                        type.data = data;
                    }
                }
            }
        }
    }
}