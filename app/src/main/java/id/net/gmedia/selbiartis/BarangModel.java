package id.net.gmedia.selbiartis;

public class BarangModel {

    private String id;
    private String nama;
    private String url;
    private double harga;

    private int jenis;
    private UserModel penjual;
    private int stok;

    public BarangModel( int jenis, String id, String nama, String url, double harga, int stok, UserModel penjual){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.jenis = jenis;
        this.penjual = penjual;
        this.stok = stok;
    }

    public int getStok() {
        return stok;
    }

    public double getHarga() {
        return harga;
    }

    public int getJenis() {
        return jenis;
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

    public UserModel getPenjual() {
        return penjual;
    }
}
