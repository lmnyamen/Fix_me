package Controllers;

import Entry.*;

public class AdminMessages extends MessageHandler {
    public AdminMessages(Broker broker) {
        super(broker);
    }

    @Override
    public void handleMessage(String message) {
        if (message.contains("Your unique id is:")) {
            String[] array = message.split(":");
            System.out
                    .println("The router has assigned you an ID. It is " + array[1] + ". Saving the ID for future reference and responding to Router...");
            broker.setId(array[1]);
            broker.sendMessage("I am Broker:" + array[1].trim());
        } else if (nextHandler != null) {
            nextHandler.handleMessage(message);
        }
    }
}
