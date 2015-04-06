package net.fusemc.zbungeecontrol.ban;

import java.sql.Timestamp;

/**
 * Created by Marco on 07.08.2014.
 */
public class Ban {

    private String reason;
    private Timestamp timestamp;

    public Ban(String reason, Timestamp timestamp) {
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public String getReason() {
        return reason;
    }

    public Timestamp getTime() {
        return timestamp;
    }

    public String getPrintTime() {
        String message = "";
        long seconds = (timestamp.getTime() - System.currentTimeMillis()) / 1000;
        if (seconds >= 86400) {
            long days = seconds / 86400;
            seconds %= 86400;

            message = message + days + " Tag(e) ";
        }
        if (seconds >= 3600) {
            long hours = seconds / 3600;
            seconds %= 3600;

            message = message + hours + " Stunde(n) ";
        }
        if (seconds >= 60) {
            long min = seconds / 60;
            seconds %= 60;

            message = message + min + " Minute(n) ";
        }
        if (seconds > 0) {
            message = message + seconds + " Sekunde(n) ";
        }
        return message;
    }
}
