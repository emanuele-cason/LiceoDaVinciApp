package davi.liceodavinci;

/**
 * Created by Emanuele on 31/12/2017 at 19:31.
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

    Communication(String name, String data, String type, String url) {
        this.nome = name;
        this.data = data;
        this.tipo = type;
        this.url = url;
    }

    public String getName() {
        return nome;
    }

    public String getData() {
        return data;
    }

    public String getType() {
        return tipo;
    }

    String getUrl() {
        return url;
    }

    public int getId() {
        try {
            return Integer.valueOf(nome.split("\\-")[0]);
        }catch (Exception e){
            try {
                return Integer.valueOf(nome.substring(0,3));
            }catch (Exception o){}
        }
        return 0;
    }

    void setUrl(String url) {
        this.url = url;
    }
}
