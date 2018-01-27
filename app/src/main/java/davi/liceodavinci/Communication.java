package davi.liceodavinci;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Emanuele on 31/12/2017 at 19:31 at 20:24!
 */

public class Communication {

    private String nome;
    private String data;
    private String tipo;
    private String url;

    static final int COMM_STUDENTS = 0;
    static final int COMM_PARENTS = 1;
    static final int COMM_PROFS = 2;
    static final int COMM_SAVED = 3;

    static final int REMOTE = 0;
    static final int CACHED = 1;
    static final int DOWNLOADED = 2;

    Communication(String name, String date, String type, String URL) {
        nome = name;
        data = date;
        tipo = type;
        url = URL;
    }

    class LocalCommunication extends davi.liceodavinci.Communication {
        private int status;
        private boolean seen;

        LocalCommunication(davi.liceodavinci.Communication communication, int status, boolean seen){
            super(communication.getName(), communication.getData(), communication.getType(), communication.getUrl());
            this.status = status;
            this.seen = seen;
        }

        LocalCommunication(davi.liceodavinci.Communication communication){
            super(communication.getName(), communication.getData(), communication.getType(), communication.getUrl());
            this.status = REMOTE;
            this.seen = false;
        }

        void setSeen(boolean seen){this.seen = seen;}

        boolean isSeen(){return seen;}

        void setStatus(int status){
            if (status == DOWNLOADED || status == CACHED || status == REMOTE) this.status = status;
        }

        int getStatus(){
            return status;
        }
    }

    String getName() {
        return nome;
    }

    String getNameFormatted(){
        return nome;
    }

    String getData() {
        return data;
    }

    Date getDataObject(){
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

    String getUrl() {
        return url;
    }

    public int getId() {
        try {
            return Integer.valueOf(nome.split("-")[0]);
        } catch (Exception e) {
            try {
                return Integer.valueOf(nome.substring(0, 3));
            } catch (Exception ignored) {
            }
        }
        return 0;
    }

    void setUrl(String url) {
        this.url = url;
    }
}
