package Controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import Entry.*;

public class Fix {
    Market market;
    public Fix(Market market) {
        this.market = market;
        // Maybe add check for zero length body
    }

    public void sendExecuteReport(String brokerId, int saved) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss");
        LocalDateTime timeStamp = LocalDateTime.now();

        int type;
        int bodyLength;
        String checksum;
        String msgBody;
        String msg;
        String typeMsg;

        if (market.getErrorReason() == null) {
            type = 2;
            if (saved > 0) {
                typeMsg = "Your order has been completed successfully. You saved:" + saved;
            } else {
                typeMsg = "Your order has been completed successfully.";
            }
        } else {
            type = 4;
            typeMsg = market.getErrorReason();
        }
        msgBody = "34=1|52=" + timeStamp + "|56=" + brokerId + "|39=" + type + "|54=" + typeMsg + "|";
        bodyLength = msgBody.length();
        String msgHeader = "49=" + market.getId().trim() + "|8=FIX.4.4|35=8|9=" + bodyLength + "|";
        msg = msgHeader + msgBody;
        checksum = checksumGen(msg);
        msg = msg + "10=" + checksum;
        System.out.println(msg);
        market.sendMessage(msg);
    }
    public String checksumGen(String messagebody) {
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

   /* public boolean validateChecksum(String msg) {
        String[] parts = msg.split("|");
        int checksumIndex = parts.length - 1;
        String messageWithoutChecksum = msg.replace(parts[checksumIndex], "");
        String[] checksum = parts[checksumIndex].split("=");
        if (checksumGen(messageWithoutChecksum) == checksum[1]) {
            return true;
        } else {
            return false;
        }
    } */

}
