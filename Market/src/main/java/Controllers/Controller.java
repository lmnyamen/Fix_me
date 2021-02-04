package Controllers;

import Entry.*;
public class Controller {
    AdminMessages adminMessages;
    InfoMessages infoMessages;
    FixMessages fixMessages;

    public Controller(Market market) {
        setup(market);
    }

    public void setup(Market market) {
        adminMessages = new AdminMessages(market);
        infoMessages = new InfoMessages(market);
        fixMessages = new FixMessages(market);
        adminMessages.setNextHandler(infoMessages);
        infoMessages.setNextHandler(fixMessages);
        fixMessages.setNextHandler(null);
    }

    public void handleMessage(String message){
        adminMessages.handleMessage(message);
    }
}
