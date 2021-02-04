package Entry;

import Controllers.Controller;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

import Controllers.*;

public class Market {

    private static BufferedReader input = null;
    private Controller messageHandlers = new Controller(this);
    private String Id = null;
    private boolean messageReady = false;
    private String brokerMessage = null;
    private Selector selector = null;
    private SocketChannel sc = null;
    private Db db = new Db(this);
    private Fix fix = new Fix(this);
    private String errorReason;

    public static void main(String[] args) {
        Db.dbCreate();
        System.out.println("hello Market");
        Market market = new Market();
        market.mainMarket();
    }


    public void mainMarket() {
        // try {
        System.out.println(
                "Hello, I am your friendly Market. Please wait a moment while I set things up and connect to the Router");
        socketSetup();
        checkSelector();
    }

    public Boolean processKeys(Set readySet) throws Exception {
        SelectionKey key = null;
        Iterator iterator = null;
        iterator = readySet.iterator();
        while (iterator.hasNext()) {
            key = (SelectionKey) iterator.next();
            iterator.remove();
        }
        if (key.isConnectable()) {
            Boolean connected = serverConnect(key);
            if (!connected) {
                return true;
            } else {
                System.out.println(
                        "I have successfully connected with the router. Waiting to be assigned a market ID...");
                key.interestOps(SelectionKey.OP_READ);
                checkSelector();
            }
        }
        if (key.isReadable()) {
            ByteBuffer bb = ByteBuffer.allocate(1024);
            sc.read(bb);
            String result = new String(bb.array()).trim();
            messageHandlers.handleMessage(result);
            System.out.println("Message received from Router: " + result);
            checkSelector();
        }

        if (this.messageReady) {
            String msg = brokerMessage;
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
            channel.write(bb);

        }

        return false;
    }

    public Boolean serverConnect(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            while (sc.isConnectionPending()) {
                sc.finishConnect();
            }
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void checkSelector() {
        try {
            // while (true) {
            if (selector.select() > 0) {
                Boolean doneStatus = processKeys(selector.selectedKeys());
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    public void socketSetup() {
        try {
            InetSocketAddress addr = new InetSocketAddress(InetAddress.getByName("localhost"), 5001);
            selector = Selector.open();
            sc = SocketChannel.open();
            sc.configureBlocking(false);
            sc.connect(addr);
            sc.register(selector, SelectionKey.OP_CONNECT);
            input = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            System.out.println("Error! " + e);
        }
    }



    public void sendMessage(String msg) {
        ByteBuffer bb = ByteBuffer.wrap(msg.getBytes());
        try {
            sc.write(bb);
        } catch (Exception e) {
            System.out.println("Error??? " + e);
        }
    }

    public SocketChannel getSc() {
        return sc;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Db getDb() {
        return db;
    }

    public void setDb(Db db) {
        this.db = db;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public Fix getFix() {
        return fix;
    }
}
