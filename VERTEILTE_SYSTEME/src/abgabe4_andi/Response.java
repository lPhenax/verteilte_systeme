package abgabe4_andi;

import java.util.Arrays;

/**
 * Created by Burger & Schleußner on 30.11.2015.
 *
 */
public class Response {

    private int statusCode;
    private int sequence;
    private String[] res;

    public Response() {
    }

    public Response(int statusCode, int sequence, String[] res) {
        this.statusCode = statusCode;
        this.sequence = sequence;
        this.res = res;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String[] getRes() {
        return res;
    }

    public void setRes(String[] res) {
        this.res = res;
    }

    @Override
    public String toString() {
        return "Response{" +
                "statusCode=" + statusCode +
                ", sequence=" + sequence +
                ", res=" + Arrays.toString(res) +
                '}';
    }
}
