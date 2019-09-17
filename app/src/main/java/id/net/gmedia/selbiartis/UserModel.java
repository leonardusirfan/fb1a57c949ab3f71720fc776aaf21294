package id.net.gmedia.selbiartis;

public class UserModel {
    private String id;
    private String nama;
    private String image;

    private String alamat = "";
    private String no_telp = "";
    private String email = "";

    public UserModel(String id, String nama, String image){
        this.id = id;
        this.nama = nama;
        this.image = image;
    }

    public UserModel(String id, String nama, String alamat, String no_telp, String email){
        this.id = id;
        this.nama = nama;
        this.alamat = alamat;
        this.no_telp = no_telp;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getTelepon() {
        return no_telp;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getImage() {
        return image;
    }
}

