package Controllers;

import java.nio.channels.SelectionKey;
import Entry.*;

public class FixMessages extends MessageHandler {

    public FixMessages(Market market) {
        super(market);
    }

    public boolean checkSumCheck(String msgbody){
        String [] array = msgbody.split("10=");
        String [] checksumOriginal = array[1].split("\\|");
        String checksum = market.getFix().checksumGen(array[0]);
        System.out.println(checksumOriginal[0]);
        System.out.println(checksum);
        if (checksum.equals(checksumOriginal[0])) {
            System.out.println("FIX message checksum was successfully validated");
            return true;
        }
        System.out.println("The FIX message checksum was unable to be validated");
        return false;
    }

    @Override
    public void handleMessage(String message) {
        if (message.contains("8=FIX.4.4")) {
            if (!checkSumCheck(message)){
                return;
            }
            String[] array = message.split("\\|");
            String brokerId = array[0].split("=")[1];
            String type = array[8].split("=")[1];
            String price = array[12].split("=")[1];
            String qty = array[10].split("=")[1];
            String instrument = array[11].split("=")[1];
            String checksum = array[13].split("=")[1];
            if (type.contains("2")) {
                market.getDb().sellInstrument(instrument, qty, price, brokerId);
                // } else if (router.getBrokers().containsKey(id)) {
                // router.sendMessage(message, router.getBrokers().get(id));
                // } else {
                // System.out.println("Id provided in the FIX message does not exist!");
                // }
            } else if (type.contains("1")) {
                System.out.println("Buying...");
                if(market.getDb().checkBuyMultiPossible(instrument, qty, price, brokerId)){
                    System.out.println("Bought!");
                }
                market.getDb().buyInstrument(instrument, qty, price, brokerId);
            } else if (nextHandler != null) {
                nextHandler.handleMessage(message);
            }
        }

    }

}
