package davi.liceodavinci.schedule;

/**
 * Created by Emanuele on 26/03/2018 at 12:43!
 */

public class Prof {

    private String Nome;
    private String Cognome;

    public String getName() {
        if (Nome == null) return "test";
        return Nome;
    }

    public String getSurname() {
        if (Cognome == null) return "ssst";
        return Cognome;
    }
}
