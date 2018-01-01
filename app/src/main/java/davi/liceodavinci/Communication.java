package davi.liceodavinci;

/**
 * Created by Emanuele on 31/12/2017 at 19:31.
 */

public class Communication {

    private String Nome;
    private String Data;
    private String Tipo;
    private String url;

    public Communication(String name, String data, String type, String url) {
        this.Nome = name;
        this.Data = data;
        this.Tipo = type;
        this.url = url;
    }

    public String getName() {
        return Nome;
    }

    public String getData() {
        return Data;
    }

    public String getType() {
        return Tipo;
    }

    public String getUrl() {
        return url;
    }
}
