package Controllers;

import java.util.Scanner;
import Entry.*;
public class UserInterface implements Runnable {

    private Broker broker;
    private Fix fix;
    private String currentMarket;
    private boolean mainMenu = true;
    private boolean acceptableOption = false;
    private Scanner input = new Scanner(System.in);
    private int wallet = 1000;

    public UserInterface(Broker broker) {
        this.broker = broker;
        this.fix = new Fix(broker);
    }

    @Override
    public void run() {
        try {
            System.out.println("Starting up the user interface in its own thread.");
            Thread.sleep(3000);
            if (!broker.getConnected()){
                System.out.println("No router is present. Please start up a router first then restart broker");
                return;
            }
            getMarkets();
            start();
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void getMarkets() {
        broker.sendMessage("Retrieve markets");
    }

    public void getSpecificMarket(String marketId) {
        broker.sendMessage("List instruments for market:" + marketId + ":" + broker.getId());
    }

    public void checkMarketExists(String marketId) {
        broker.sendMessage("Does this market exist:" + marketId);
    }


    public void start() {
        try {
            // while (broker.getMarketsRetrieve() != true) {
            // Thread.sleep(2000);
            // getMarkets();
            // }
            while (!acceptableOption) {
                System.out.println("Your wallet currently has: " + broker.getWallet());
                System.out.println(
                        "Your options are:\n1. Display instruments for a specific market \n2. List available markets. \n3. Send a market request");
                String s = input.nextLine();
                parseInput(s);
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void parseInput(String input) {
        switch (input) {
            case "1":
                acceptableOption = true;
                chooseMarket();
                break;
            case "2":
                getMarkets();
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    System.out.println(e);
                }
                break;
            case "3":
                makeRequest();
                break;
            default:
                System.out.println("Please choose options 1, 2 or 3 only");
                break;
        }
    }

    public void parseMarketInput(String input) {
        input = input.trim();
        getSpecificMarket(input);
    }

    public void chooseMarket() {
        broker.setValidMarket(false);
        String s = null;
        while (!broker.getValidMarket()) {
            try {
                System.out.println("Please enter the ID of your market of choice.");
                s = input.nextLine();
                checkMarketExists(s);
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (s != null) {
            getSpecificMarket(s);
        }
        this.acceptableOption = false;
        // Todo
    }

    public void makeRequest() {
        broker.setValidRequest(false);
        String s = null;
        while (!broker.getValidRequest()) {
            try {
                System.out.println("The syntax for a request is <BUY/SELL> <QTY> <TYPE> FOR <PRICE> IN <ID>");
                s = input.nextLine();
                fix.sendRequest(s);
                Thread.sleep(1000);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        if (s != null) {
            getSpecificMarket(s);
        }
        this.acceptableOption = false;
    }

}
