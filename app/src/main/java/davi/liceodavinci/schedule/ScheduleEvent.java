package davi.liceodavinci.schedule;

/**
 * Created by Emanuele on 12/03/2018 at 23:05!
 */

public class ScheduleEvent {

    private static String[] beginTimes = {"08:05AM", "09:05AM", "10:00AM", "11:10AM", "12:05PM", "13:00PM"};

    static final int MON = 0;
    static final int TUE = 1;
    static final int WED = 2;
    static final int THU = 3;
    static final int FRI = 4;
    static final int SAT = 5;

    private String num;
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

    private String customName;
    private String notes;

    ScheduleEvent(int hourNum, String duration){
        this.durata = duration;
        this.inizio = beginTimes[hourNum];
    }

    public String getSubject() {return materia;}

    String getSubjectCode(){return mat_cod;}

    int getDuration() {
        return Integer.parseInt(String.valueOf(durata.charAt(0)));
    }

    public int[] getBeginningTime() {
        return new int[]{Integer.valueOf(inizio.split("(\\d\\d):(\\d\\d)\\w\\w")[0]), Integer.valueOf(inizio.split("(\\d\\d):(\\d\\d)\\w\\w")[1])};
    }

    String getBeginningTimeString(){
        return inizio;
    }

    int getHourNum() {
        for (int i = 0; i < beginTimes.length; i++) if (inizio.equals(beginTimes[i])) return i;
        return -1;
    }

    int getDay() {
        String[] dayKeywords = {"lun", "mar", "mer", "gio", "ven", "sab"};
        for (int i = 0; i < dayKeywords.length; i++)
            if (giorno.toLowerCase().contains(dayKeywords[i])) return i;
        return -1;
    }

    String getLocation(){return sede;}

    String getClassId() {
        return classe;
    }

    String getClassroom() {
        return aula;
    }

    String getProfName() {
        return doc_cognome;
    }

    String getProfSurname() {
        return doc_nome;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCustomName() {
        return customName;
    }

    public String getNotes() {
        return notes;
    }

    public int getId(){
        try {
            return Integer.parseInt(this.num);
        }catch (NumberFormatException e){
            return -1;
        }
    }
}