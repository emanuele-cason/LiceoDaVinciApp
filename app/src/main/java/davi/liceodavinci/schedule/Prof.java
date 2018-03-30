package davi.liceodavinci.schedule;

/**
 * Created by Emanuele on 26/03/2018 at 12:43!
 */

public class Prof {

    private String nome;
    private String cognome;

    public String getName() {
        if (nome == null) return "Errore";
        return nome;
    }

    public String getSurname() {
        if (cognome == null) return "Porco cane!";
        return cognome;
    }
}
