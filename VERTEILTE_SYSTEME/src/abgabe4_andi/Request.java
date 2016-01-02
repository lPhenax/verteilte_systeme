package abgabe4_andi;

/**
 * Created by Burger & Schleußner on 30.11.2015.
 *
 */
public class Request {

    private int sequence;
    private String command;
    private String[] params;

    public Request() {
        this.sequence = (int) (Math.random() * 1000);
    }

    public Request(String command, String[] params) {
        this.command = command;
        this.params = params;
    }

    public Request(int sequence, String command, String[] params) {
        this.sequence = sequence;
        this.command = command;
        this.params = params;
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

    @Override
    public String toString() {
        return "Request{" +
                "sequence=" + sequence +
                ", command='" + command + '\'' +
                ", params=" + params +
                '}';
    }
}
