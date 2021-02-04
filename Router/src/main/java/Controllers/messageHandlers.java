package Controllers;

import java.nio.channels.SelectionKey;
import Entry.*;

public abstract class messageHandlers {
    protected messageHandlers nextHandler;
    protected Router router;

    protected messageHandlers(Router router){
        this.router = router;
    }

    public void setNextHandler(messageHandlers handler){
        this.nextHandler = handler;
    }

    public void handleMessage(String message, SelectionKey key){
        System.out.println(message);
    }
}
