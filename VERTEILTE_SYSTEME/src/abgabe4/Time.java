package abgabe4;

import java.security.Timestamp;
import java.util.Date;

/**
 * Created by Phenax on 30.11.2015.
 */
/////////////////////////////////////////////////////////////////
// hier kann eine beliebig komplexe Klasse entstehen oder auch einfach nur die aktuelle Zeit zurückgegen werden.
/////////////////////////////////////////////////////////////////
public class Time {
    private long time;

    public Time(){
        this.time = new Date().getTime();
    }


    public long getTime() {
        return time;
    }
}

