package Controllers;

import java.nio.channels.SelectionKey;
import Entry.*;
public class AdminMessages extends messageHandlers {
    public AdminMessages(Router router) {
        super(router);
    }

    @Override
    public void handleMessage(String message, SelectionKey key) {
        if (message.contains("I am Broker:")) {
            String[] array = message.split(":");
            router.addBroker(array[1].trim(), key);
            System.out.println("A connection has identified itself as a broker with id" + array[1]
                    + ". Saving the ID and relevant selection key");
            router.sendMessage("You have been registered as a broker using id: " + array[1], key);
        } else if (message.contains("I am Market:")) {
            String[] array = message.split(":");
            router.addMarket(array[1].trim(), key);
            System.out.println("A connection has identified itself as a market with id" + array[1]
                    + ". Saving the ID and relevant selection key");
            router.sendMessage("You have been registered as a market using id: " + array[1], key);
        } else if (nextHandler != null) {
            nextHandler.handleMessage(message, key);
        }
    }
}
