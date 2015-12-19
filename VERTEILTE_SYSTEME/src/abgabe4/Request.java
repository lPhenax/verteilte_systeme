package abgabe4;

import javax.json.JsonObject;

/**
 * Created by Phenax on 19.12.2015.
 */
public class Request {

    private int sequence;
    private String command;
    private String[] params;

    public Request(int sequence, String command, String[] params) {
        this.sequence = sequence;
        this.command = command;
        this.params = params;
        req(this);
    }

    private void req(Request requesr){

    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}
