package Controllers;

import java.nio.channels.SelectionKey;
import java.util.zip.Checksum;
import Entry.*;

public class FixMessages extends messageHandlers {

    public FixMessages(Router router) {
        super(router);
    }

    public boolean checkSumCheck(String msgbody){
        String [] array = msgbody.split("10=");
        String [] checksumOriginal = array[1].split("\\|");
        String checksum = router.getFix().checksumGen(array[0]);
        if (checksum.equals(checksumOriginal[0])) {
            System.out.println("FIX message checksum was successfully validated");
            return true;
        }
        System.out.println("The FIX message checksum was unable to be validated");
        return false;
    }

    @Override
    public void handleMessage(String message, SelectionKey key) {
        if (message.contains("8=FIX.4.4")) {
            System.out.println(message);
            if (!checkSumCheck(message)){
                return;
            }
            String [] array = message.split("\\|");
            String [] senderArray = array[0].split("=");
            String [] idArray = array[6].split("=");
            String id = idArray[1];
            String senderid = senderArray[1];
            if (router.getMarkets().containsKey(id)){
                router.sendMessage(message, router.getMarkets().get(id));
            } else if (router.getBrokers().containsKey(id)) {
                router.sendMessage(message, router.getBrokers().get(id));
            } else {
                System.out.println("Id provided in the FIX message does not exist!");
                router.sendMessage("ID provided for sender does not exist", key);
            }
        } else if (nextHandler != null) {
            nextHandler.handleMessage(message, key);
        }
    }

}
