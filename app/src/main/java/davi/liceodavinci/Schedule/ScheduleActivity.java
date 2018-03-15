package davi.liceodavinci.Schedule;

/**
 * Created by Emanuele on 12/03/2018 at 23:05!
 */

public class ScheduleActivity {

    public static final int MON = 0;
    public static final int TUE = 1;
    public static final int WED = 2;
    public static final int THU = 3;
    public static final int FRI = 4;
    public static final int SAT = 5;

    private int num;
    private String durata;
    private String mat_cod;
    private String materia;
    private String doc_cognome;
    private String doc_nome;
    private String classe;
    private String aula;
    private String giorno;
    private String inizio;
    private String sede;

    public String getMateria() {
        return materia;
    }

    public int getDuration() {
        return Integer.valueOf(durata.split("(\\d)h(\\d\\d)m")[0]);
    }

    public int[] getBeginningTime() {
        return new int[]{Integer.valueOf(inizio.split("(\\d\\d):(\\d\\d)\\w\\w")[0]), Integer.valueOf(inizio.split("(\\d\\d):(\\d\\d)\\w\\w")[1])};
    }

    public int getHourNum() {
        String[] beginTimes = {"08:05AM", "09:05AM", "10:00AM", "11:10AM", "12:05PM", "13:00PM"};
        for (int i = 0; i < beginTimes.length; i++) if (inizio.equals(beginTimes[i])) return i;
        return -1;
    }

    public int getGiorno() {
        String[] dayKeywords = {"lun", "mar", "mer", "gio", "ven", "sab"};
        for (int i = 0; i < dayKeywords.length; i++) if (giorno.toLowerCase().contains(dayKeywords[i])) return i;
        return -1;
    }
}