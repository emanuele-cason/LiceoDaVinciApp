package davi.liceodavinci.agenda;

import java.util.Calendar;

/**
 * Created by Emanuele on 02/06/2018 at 18:06!
 */

public class Event {

    private int inizio;
    private int fine;
    private String contenuto;
    private String titolo;

    public int getBegin() {
        return inizio;
    }

    public int getEnd() {
        return fine;
    }

    public String getContent() {
        return contenuto;
    }

    public String getTitle() {
        return titolo;
    }

    public Calendar getBeginCalendar(){
        Calendar begin = Calendar.getInstance();
        begin.setTimeInMillis((long)inizio * 1000);
        return begin;
    }

    public Calendar getEndCalendar(){
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis((long)fine * 1000);
        return end;
    }
}
