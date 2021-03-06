package id.net.gmedia.selbiartis;

import java.util.ArrayList;
import java.util.List;

public class OrderModel {

    public static final int STATUS_PROSES = 1;
    public static final int STATUS_MENUNGGU_PERSETUJUAN = 2;
    public static final int STATUS_DISETUJUI = 3;
    public static final int STATUS_DIBATALKAN = 4;
    public static final int STATUS_DITOLAK = 9;
    public static final int STATUS_SELESAI = 5;

    private String id_order;
    private String id_merchandise;

    private String nama;
    private String gambar;
    private int jumlah;
    private double harga_satuan;
    private String tanggal;
    private String catatan;

    private int status;

    private List<String> listWarna = new ArrayList<>();
    private List<String> listHarga = new ArrayList<>();

    public OrderModel(String id_order, String id_merchandise, String nama, String gambar, int jumlah,
                      double harga_satuan, String tanggal, String catatan, int status){
        this.id_order = id_order;
        this.id_merchandise = id_merchandise;

        this.catatan = catatan;
        this.nama = nama;
        this.gambar = gambar;
        this.harga_satuan = harga_satuan;
        this.tanggal = tanggal;
        this.jumlah = jumlah;

        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getId_merchandise() {
        return id_merchandise;
    }

    public String getId_order() {
        return id_order;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getNama() {
        return nama;
    }

    public int getJumlah() {
        return jumlah;
    }

    public double getHarga_satuan() {
        return harga_satuan;
    }

    public String getCatatan() {
        return catatan;
    }

    public String getGambar() {
        return gambar;
    }
}
