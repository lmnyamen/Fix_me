package Controllers;

import java.nio.channels.SelectionKey;
import Entry.*;

public class Controller {
    AdminMessages adminMessages;
    InfoMessages infoMessages;
    FixMessages fixMessages;

    public Controller(Router router) {
        setup(router);
    }

    public void setup(Router router) {
        adminMessages = new AdminMessages(router);
        infoMessages = new InfoMessages(router);
        fixMessages = new FixMessages(router);
        adminMessages.setNextHandler(infoMessages);
        infoMessages.setNextHandler(fixMessages);
        fixMessages.setNextHandler(null);
    }

    public void handleMessage(String message, SelectionKey key){
        adminMessages.handleMessage(message, key);
    }
}
