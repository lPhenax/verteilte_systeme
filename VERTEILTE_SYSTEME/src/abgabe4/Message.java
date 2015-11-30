package abgabe4;

/**
 * Created by Phenax on 30.11.2015.
 */
public class Message {

    /**
     * at first, the simple way without json-objects
     */
    private User client;
    private String msg;

    Message(User client, String msg){
        this.client = client;
        this.msg = msg;
    }
}
