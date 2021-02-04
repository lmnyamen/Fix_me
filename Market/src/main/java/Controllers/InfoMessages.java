package Controllers;

import Entry.*;

public class InfoMessages extends MessageHandler {

    public  InfoMessages(Market market) {
        super(market);
    }

    @Override
    public void handleMessage(String message) {
        if (message.contains("List your instruments for:")) {
            String[] array = message.split(":");
            String brokerId = array[1];
            String instruments = market.getDb().getInstruments();
            market.sendMessage("Here are the market instruments:" + brokerId + ":" + instruments);
        } else if (nextHandler != null) {
            nextHandler.handleMessage(message);
        }
    }

}
