package id.net.gmedia.selbiartis;

import java.util.ArrayList;
import java.util.List;

public class PenawaranModel {
    private String id;
    private String judul;
    private String keterangan;
    private int status;
    private String status_string;
    private List<String> listDesain = new ArrayList<>();

    private String terima;
    private String tolak;

    public PenawaranModel(String id, String judul, int status, String status_string, String keterangan, String terima, String tolak){
        this.id = id;
        this.judul = judul;
        this.keterangan = keterangan;

        this.terima = terima;
        this.tolak = tolak;

        this.status = status;
        this.status_string = status_string;
    }

    public int getStatus() {
        return status;
    }

    public String getStatus_string() {
        return status_string;
    }

    public String getId() {
        return id;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public String getTerima() {
        return terima;
    }

    public String getTolak() {
        return tolak;
    }

    public String getJudul() {
        return judul;
    }

    public List<String> getListDesain() {
        return listDesain;
    }

    public void addDesain(String image){
        listDesain.add(image);
    }
}
