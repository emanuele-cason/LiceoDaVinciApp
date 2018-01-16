package davi.liceodavinci;

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

    static final int DOWNLOADED = 0;
    static final int CACHED = 1;
    static final int REMOTE = 2;

    Communication(String name, String date, String type, String URL) {
        nome = name;
        data = date;
        tipo = type;
        url = URL;
    }

    class CommunicationStored extends Communication{
        private int status;
        private boolean seen;

        CommunicationStored(String name, String date, String type, String URL, int status, boolean seen){
            super(name, date, type, URL);
            this.status = status;
            this.seen = seen;
        }

        CommunicationStored(Communication communication, int status, boolean seen){
            super(communication.getName(), communication.getData(), communication.getType(), communication.getUrl());
            this.status = status;
            this.seen = seen;
        }

        CommunicationStored(Communication communication){
            super(communication.getName(), communication.getData(), communication.getType(), communication.getUrl());
            this.status = REMOTE;
            this.seen = false;
        }

        void setSeen(boolean seen){this.seen = seen;}

        void setStatus(int status){
            if (status == DOWNLOADED || status == CACHED || status == REMOTE) this.status = status;
        }

        int getStatus(){
            return status;
        }
    }

    public String getName() {
        return nome;
    }

    public String getData() {
        return data;
    }

    public String getType(){return tipo;}

    public String getUrl() {
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
