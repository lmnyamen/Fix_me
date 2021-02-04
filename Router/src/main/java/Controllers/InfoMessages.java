package Controllers;

import java.nio.channels.SelectionKey;
import java.util.Iterator;
import java.util.Map;

import Entry.*;

public class InfoMessages extends messageHandlers {
    public InfoMessages(Router router) {
        super(router);
    }

    @Override
    public void handleMessage(String message, SelectionKey key) {
        if (message.contains("Retrieve markets")) {
            if (router.getMarkets().size() <= 0) {
                router.sendMessage("No markets present", key);
            } else {
                String msg = "These are the markets currently available:\n";
                Iterator<Map.Entry<String, SelectionKey>> iterator = router.getMarkets().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, SelectionKey> entry = iterator.next();
                    msg += entry.getKey();
                    msg += " market\n";
                }
                System.out.println("Gotcha!");
                router.sendMessage(msg, key);
            }
        } else if (message.contains("List instruments for market:")) {
            String [] array = message.split(":");
            if (router.getMarkets().containsKey(array[1].trim())) {
                router.sendMessage("List your instruments for:" + array[2], router.getMarkets().get(array[1].trim()));
            } else {
                //    router.sendMessage("No such market exists. Given was " +array[1], key);
            }
        } else if (message.contains("Here are the market instruments:")) {
            String [] array = message.split(":");
            router.sendMessage(array[2], router.getBrokers().get(array[1].trim()));
        } else if (message.contains("Does this market exist:")) {
            String [] array = message.split(":");
            if (router.getMarkets().containsKey(array[1].trim())) {
                router.sendMessage("Market does exist", key);
            } else {
                router.sendMessage("Market does not exist", key);
            }
        } else if (nextHandler != null) {
            nextHandler.handleMessage(message, key);
        }
    }

}
