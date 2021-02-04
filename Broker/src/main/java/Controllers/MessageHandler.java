package Controllers;

import Entry.*;
import java.nio.channels.SocketChannel;

public  abstract class MessageHandler {

    protected MessageHandler nextHandler;
    protected Broker broker;
    protected SocketChannel sc;
    protected MessageHandler(Broker broker){
        this.broker = broker;
        this.sc = broker.getSc();
    }

    public void setNextHandler(MessageHandler handler){
        this.nextHandler = handler;
    }

    public void handleMessage(String message){
        System.out.println(message);
    }
}
