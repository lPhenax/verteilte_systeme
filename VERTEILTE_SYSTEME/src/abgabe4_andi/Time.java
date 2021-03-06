package abgabe4_andi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Burger & Schleußner on 30.11.2015.
 *
 */
/////////////////////////////////////////////////////////////////
// hier kann eine beliebig komplexe Klasse entstehen oder auch einfach nur die aktuelle Zeit zurückgegen werden.
/////////////////////////////////////////////////////////////////
public class Time {
    private long time;
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public Time(){
        this.time = new Date().getTime();
    }


    public String getTime() {

        return sdf.format(time);
    }
}

