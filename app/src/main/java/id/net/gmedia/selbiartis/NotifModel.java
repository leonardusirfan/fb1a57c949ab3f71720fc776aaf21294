package id.net.gmedia.selbiartis;

import android.text.SpannableStringBuilder;

import com.leonardus.irfan.Converter;

import java.util.Date;

public class NotifModel {
    private String id;
    private String image;
    private String notif;
    private String tanggal;
    private boolean proses = true;

    public NotifModel(String id, String image, String notif, String tanggal, boolean proses){
        this.id = id;
        this.image = image;
        this.notif = notif;
        this.tanggal = tanggal;
        this.proses = proses;
    }

    public NotifModel(String id, String image, String notif, String tanggal){
        this.id = id;
        this.image = image;
        this.notif = notif;
        this.tanggal = tanggal;
    }

    public void setProses(boolean proses) {
        this.proses = proses;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getNotif() {
        return notif;
    }

    public String getTanggal() {
        return Converter.DToString(Converter.stringDTToDate(tanggal));
    }

    public boolean isProses() {
        return proses;
    }
}
