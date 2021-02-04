package Controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import Entry.*;

public class Fix {
    Broker broker;

    public Fix(Broker broker) {
        this.broker = broker;
        // Maybe add check for zero length body
    }

    private boolean checkWallet(int price, int qty) {
        int total = price * qty;
        if (broker.getWallet() < total)
            return false;
        else {
            broker.setWallet(broker.getWallet() - total);
            return true;
        }
    }

    public void sendRequest(String request) {
        if (!validateRequest(request)) {
            return;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm:ss");
        LocalDateTime timeStamp = LocalDateTime.now();

        // BUY 10 CORN FOR 20 IN 333555
        String[] array = request.split(" ");
        int type;
        int bodyLength;
        String checksum;
        String msgBody;
        String msg;

        if (array[0].equalsIgnoreCase("buy")) {
            type = 1;
            if (!checkWallet(Integer.parseInt(array[4]), Integer.parseInt(array[1]))) {
                System.out.println("You don't have enough funds in your wallet to make that buy order!");
                return;
            } else {
                System.out.println("The necessary funds for the buy order have been deducted");
            }
        } else {
            type = 2;
        }
        msgBody = "34=1|52=" + timeStamp + "|56=" + array[6] + "|40=1|54=" + type + "|55=ZAR|38=" + array[1] + "|58="
                + array[2] + "|44=" + array[4] + "|";
        bodyLength = msgBody.length();
        String msgHeader = "49=" + broker.getId().trim() + "|8=FIX.4.4|35=A|9=" + bodyLength + "|";
        msg = msgHeader + msgBody;
        checksum = checksumGen(msg);
        msg = msg + "10=" + checksum;
        System.out.println(msg);
        broker.sendMessage(msg);
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

    private boolean validateRequest(String msg) {
        String[] array = msg.toLowerCase().split(" ");
        if (array.length != 7) {
            System.out.println("Your request is missing syntax values. Please check you have all elements required");
            return false;
        } else if (!array[0].contains("buy") && !array[0].contains("sell")) {
            System.out.println("You can only BUY or SELL instruments at a market");
            return false;
        } else if (!array[1].matches("[0-9]+")) {
            System.out.println("Only NUMERIC values are acceptable when specifying the amount you wish to buy");
            return false;
        } else if (!array[3].contains("for")) {
            System.out.println("Please use the keyword FOR when making a request");
            return false;
        } else if (!array[4].matches("[0-9]+")) {
            System.out.println("Please only use NUMERIC values when specifying the price you wish to buy or sell at");
            return false;
        } else if (!array[5].contains("in")) {
            System.out.println("Please use the keyword ON when making a request");
            return false;
        } else if (array[6].length() != 6 || !array[6].matches("[0-9]+")) {
            System.out.println(
                    "Please only use NUMERIC values for market ID and ensure the ID is least 6 digits in length");
            return false;
        }
        broker.setValidRequest(true);
        return true;

    }

}
