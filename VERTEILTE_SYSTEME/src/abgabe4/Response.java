package abgabe4;

import java.util.Arrays;

/**
 * Created by Phenax on 19.12.2015.
 */
public class Response {

    private int statuscode;
    private int sequence;
    private String[] response;

    public Response() {
    }

    public Response(int statuscode, int sequence, String[] response) {
        this.statuscode = statuscode;
        this.sequence = sequence;
        this.response = response;
    }

    public int getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String[] getResponse() {
        return response;
    }

    public void setResponse(String[] response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "Response{" +
                "statuscode=" + statuscode +
                ", sequence=" + sequence +
                ", response=" + Arrays.toString(response) +
                '}';
    }
}
