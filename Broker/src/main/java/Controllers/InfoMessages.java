package Controllers;

import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Map;
import Entry.*;

public class InfoMessages extends MessageHandler {

    public InfoMessages(Broker broker) {
        super(broker);
    }
    @Override
    public void handleMessage(String message) {
        if (message.contains("No markets present")) {
            broker.setMarketsRetrieve(false);
        } else if (message.contains("These are the markets currently available:")) {
            broker.setMarketsRetrieve(true);
        } else if (message.contains("Market does not exist")) {
            broker.setValidMarket(false);
            System.out.println("The market ID you provided does not exist. Please enter a valid market ID");
        } else if (message.contains("Market does exist")) {
            broker.setValidMarket(true);
        }else if (message.contains("Here are the market instruments:")) {
            String [] array = message.split(":");
            broker.setValidMarket(true);
            System.out.println(array[1]);
        } else if (nextHandler != null) {
            nextHandler.handleMessage(message);
        }
    }

}
