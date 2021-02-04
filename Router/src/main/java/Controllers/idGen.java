package Controllers;

public class idGen {

    private int id = 500000;

    public idGen() {
        //Add if necessary
    }

    public int generateId() {
        this.id++;
        return this.id;
    }

}
