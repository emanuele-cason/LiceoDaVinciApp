package davi.liceodavinci.communications;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Emanuele on 31/12/2017 at 19:31 at 20:24!
 */

public class Communication {

    private String nome;
    private String data;
    private String tipo;
    private String url;

    private String regex = "(\\d*)\\s*-?\\s*(.*).pdf";

    public static final int COMM_STUDENTS = 0;
    public static final int COMM_PARENTS = 1;
    public static final int COMM_PROFS = 2;
    public static final int COMM_SAVED = 3;

    public static final int REMOTE = 0;
    public static final int CACHED = 1;
    public static final int DOWNLOADED = 2;

    public Communication(String name, String date, String type, String URL) {
        nome = name;
        data = date;
        tipo = type;
        url = URL;
    }

    public class LocalCommunication extends Communication {
        private int status;
        private boolean seen;

        LocalCommunication(Communication communication, int status, boolean seen){
            super(communication.getName(), communication.getData(), communication.getType(), communication.getUrl());
            this.status = status;
            this.seen = seen;
        }

        public LocalCommunication(Communication communication){
            super(communication.getName(), communication.getData(), communication.getType(), communication.getUrl());
            this.status = REMOTE;
            this.seen = false;
        }

        void setSeen(boolean seen){this.seen = seen;}

        public boolean isSeen(){return seen;}

        public void setStatus(int status){
            if (status == DOWNLOADED || status == CACHED || status == REMOTE) this.status = status;
        }

        public int getStatus(){
            return status;
        }
    }

    public String getName() {
        return nome;
    }

    public String getNameFormatted(){

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nome);

        if (matcher.matches()){
            return matcher.group(2);
        }else return nome;
    }

    public String getData() {
        return data;
    }

    public Date getDataObject(){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSSSSS");

        Date date = null;
        try {
            date = format.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    private String getType(){return tipo;}

    public String getUrl() {
        return url;
    }

    public int getId() {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nome);

        if (matcher.matches()){
            try {
                return Integer.valueOf(matcher.group(1));
            }catch (Exception ignored){}
        }
        return 0;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
