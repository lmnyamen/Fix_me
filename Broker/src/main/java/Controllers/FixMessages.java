package Controllers;

import java.nio.channels.SelectionKey;
import Entry.*;

public class FixMessages extends MessageHandler {

    public FixMessages(Broker broker) {
        super(broker);
    }

    public boolean checkSumCheck(String msgbody){
        String [] array = msgbody.split("10=");
        String [] checksumOriginal = array[1].split("\\|");
        String checksum = broker.getFix().checksumGen(array[0]);
        // System.out.println(checksumOriginal[0]);
        // System.out.println(checksum);
        if (checksum.equals(checksumOriginal[0])) {
            // System.out.println("FIX message checksum was successfully validated");
            return true;
        }
        // System.out.println("The FIX message checksum was unable to be validated");
        return false;
    }

    @Override
    public void handleMessage(String message) {
        if (message.contains("8=FIX.4.4")) {
            if(!checkSumCheck(message))
            {
                return;
            }
            String[] array = message.split("\\|");
            String msg = array[8];
            String[] msgArray = msg.split("=");
            System.out.println("Order notification:" + msgArray[1]);
            if (msg.contains("saved")) {
                String saved = msg.split(":")[1];
                System.out.println("Adding the money saved back to your wallet");
                broker.setWallet(broker.getWallet() + Integer.parseInt(saved));
            } else  if (msg.contains("earned") || msg.contains("Not enough instruments within the price")) {
                String saved = msg.split(":")[1];
                System.out.println("Adding the money earned back to your wallet");
                broker.setWallet(broker.getWallet() + Integer.parseInt(saved));
            }
        }

    }

}
