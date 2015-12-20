package abgabe4;

import java.util.Arrays;

/**
 * Created by Phenax on 19.12.2015.
 */
public class Response {

    private int sequence;
    private String[] response;

    public Response() {
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
                "sequence=" + sequence +
                ", response=" + Arrays.toString(response) +
                '}';
    }
}
