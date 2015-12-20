package abgabe4;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


/**
 * Created by Phenax on 19.12.2015.
 */
public class Request {

    private int sequence;
    private String command;
    private ArrayList<String> params;

    public Request() {
        this.sequence = 0;
    }

    public Request(String command, ArrayList<String> params) {
        this.command = command;
        this.params = params;
    }

    protected String toJson(String request){
        Scanner scan = new Scanner(request);
        String msg = "";
        String sendTo = "";
        ArrayList<String> args = new ArrayList<>();
        if (request.contains("msg")){
            request = scan.next();

            sendTo = scan.next();
            System.out.println(sendTo + ": I want to send to");
            args.add(sendTo);
            while(scan.hasNext()){
                msg = " " +  msg.concat(scan.next());
            }
        }
        System.out.println(msg);
        args.add(msg);
        System.out.println(request);
        Request requ = new Request(request , args);
        Gson gson = new Gson();
        String message = gson.toJson(requ);
        return message;
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

    public ArrayList<String> getParams() {
        return params;
    }

    public void setParams(ArrayList<String> params) {
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
