package id.net.gmedia.selbiartis;

public class BarangModel {

    private String id;
    private String nama;
    private String url;
    private double harga;

    private int jenis;
    private UserModel penjual;
    private int stok = 0;
    private int terjual = 0;

    public BarangModel( int jenis, String id, String nama, String url, double harga, int stok, int terjual){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.jenis = jenis;
        this.terjual = terjual;
        this.stok = stok;
    }

    public BarangModel( int jenis, String id, String nama, String url, double harga){
        this.id = id;
        this.nama = nama;
        this.url = url;
        this.harga = harga;
        this.jenis = jenis;
    }

    public int getTerjual() {
        return terjual;
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
