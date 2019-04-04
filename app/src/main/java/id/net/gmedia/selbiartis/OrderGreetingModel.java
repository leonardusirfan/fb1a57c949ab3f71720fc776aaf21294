package id.net.gmedia.selbiartis;

public class OrderGreetingModel {
    private String id;
    private UserModel user;
    private String ucapan;
    private String tanggal;
    private String status;

    public OrderGreetingModel(String id, UserModel user, String ucapan, String tanggal, String status){
        this.id = id;
        this.user = user;
        this.ucapan = ucapan;
        this.tanggal = tanggal;
        this.status = status;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getId() {
        return id;
    }

    public String getUcapan() {
        return ucapan;
    }

    public UserModel getUser() {
        return user;
    }

    public String getStatus() {
        return status;
    }
}
