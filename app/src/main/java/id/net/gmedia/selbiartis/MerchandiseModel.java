package id.net.gmedia.selbiartis;

public class MerchandiseModel {
    private String id;
    private String nama;
    private String url;

    public MerchandiseModel(String id, String nama, String url){
        this.id = id;
        this.nama = nama;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getUrl() {
        return url;
    }
}
