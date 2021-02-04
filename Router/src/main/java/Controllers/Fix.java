package Controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import Entry.*;

public class Fix {

    Router router;
    public Fix(Router router){
        this.router = router;
    }

    public String genAdminMessage(String message, String id){
        String body = message;
        int bodyLength = message.length();
        String cordID = id;
        String checksum;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss");
        LocalDateTime timeStamp = LocalDateTime.now();
        String msg = "49=SERVER|8=FIX.4.4|9="+ bodyLength + "|35=A|34=1|52="+timeStamp+"|56=CLIENT|";
        checksum = checksumGen(msg);
        msg = msg + "10="+checksum;
        return msg;
    }

    public String checksumGen(String messagebody){
        int bytes = messagebody.length();
        bytes = bytes % 256;
        String initialChecksum = Integer.toString(bytes);
        int length = initialChecksum.length();
        if (length == 2) {
            initialChecksum = "0" + initialChecksum;
        } else if (length == 1) {
            initialChecksum = "00" + initialChecksum;
        }
        return initialChecksum;
    }

    public boolean validateChecksum(String msg) {
        String[] parts = msg.split("\\|");
        int checksumIndex = parts.length - 1 ;
        String messageWithoutChecksum = msg.replace(parts[checksumIndex], "");
        String[] checksum = parts[checksumIndex].split("=");
        if (checksumGen(messageWithoutChecksum) == checksum[1]){
            return true;
        } else {
            return false;
        }
    }


}
