package Controllers;

import Entry.*;
public class AdminMessages extends MessageHandler {
    public AdminMessages(Market market) {
        super(market);
    }

    @Override
    public void handleMessage(String message) {
        if (message.contains("Your unique id is:")) {
            String[] array = message.split(":");
            System.out
                    .println("The router has assigned you an ID. It is " + array[1] + ". Saving the ID for future reference and responding to Router...");
            market.setId(array[1]);
            market.sendMessage("I am Market:" + array[1].trim());
        } else if (nextHandler != null) {
            nextHandler.handleMessage(message);
        }
    }
}
