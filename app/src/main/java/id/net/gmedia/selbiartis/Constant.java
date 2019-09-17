package id.net.gmedia.selbiartis;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Constant {
    //Header Request
    public final static Map<String, String> HEADER_AUTH = new HashMap<String, String>(){{put("Auth-Key", "gmedia"); put("Client-Service", "selby-ecommerce");}};

    public static final String TAG = "selbi_log";

    public static final String EXTRA_JENIS_BARANG = "jenis_barang";
    public static final String EXTRA_ID_MERCHANDISE = "id_barang";
    public static final String EXTRA_ID_ORDER = "id_barang_edit";
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_NAMA_USER = "user";
    public static final String EXTRA_CHAT_FROM = "from";
    public static final String EXTRA_MERCHANDISE = "merchandise";
    public static final String EXTRA_START_POSITION = "start";

    public static final int BARANG_PRELOVED = 19;
    public static final int BARANG_LELANG = 18;
    public static final int BARANG_MERCHANDISE = 17;
    public static final int BARANG_DONASI = 16;

    //URL Request
    private static final String BASE_URL = "https://selbi.co.id/beta/api/";
    public static final String URL_HOME_SLIDE = BASE_URL + "Slider/index";
    public static final String URL_BARANG_MASTER = BASE_URL + "Produk/stok_barang";
    public static final String URL_HOME_CATEGORY = BASE_URL + "Category/index";
    public static final String URL_BARANG_LELANG = BASE_URL + "Produk/stok_lelang";
    public static final String URL_HOME_BRAND = BASE_URL + "Brand/index";
    public static final String URL_AUTENTIFIKASI = BASE_URL + "Authentication/register";
    public static final String URL_UPLOAD_GAMBAR = BASE_URL + "Feed/add_image";
    public static final String URL_POST = BASE_URL + "Feed/add_post";
    public static final String URL_UPLOAD_BARANG = BASE_URL + "Produk/add_produk";
    public static final String URL_UPLOAD_GAMBAR_BARANG = BASE_URL + "Produk/add_image";
    public static final String URL_MERCHANDISE_LIST = BASE_URL + "Merchandise/index";
    public static final String URL_MERCHANDISE_DETAIL = BASE_URL + "Merchandise/details";
    public static final String URL_ARTIS = BASE_URL + "Penjual/index";
    public static final String URL_PROFIL = BASE_URL + "Profile/index";
    public static final String URL_UPLOAD_FOTO_PROFIL = BASE_URL + "Profile/edit_image";
    public static final String URL_EDIT_PROFIL = BASE_URL + "Profile/edit_profile";
    public static final String URL_KATEGORI_BARANG = BASE_URL + "Category/index";
    public static final String URL_CHAT = BASE_URL + "Chat/list_chat";
    public static final String URL_CHAT_DETAIL = BASE_URL + "Chat/detail_chat";
    public static final String URL_CHAT_TAMBAH = BASE_URL + "Chat/insert";
    public static final String URL_FCM_UPDATE = BASE_URL + "Authentication/update_fcm_id";
    public static final String URL_ORDER_LIST = BASE_URL + "Merchandise/list_order";
    public static final String URL_MERCHANDISE_ORDER = BASE_URL + "Merchandise/order";
    public static final String URL_MERCHANDISE_PENAWARAN = BASE_URL + "Merchandise/list_penawaran";
    public static final String URL_MERCHANDISE_APPROVAL = BASE_URL + "Merchandise/proses_penawaran";
    public static final String URL_MERCHANDISE_BATAL = BASE_URL + "Merchandise/batal_order_merchandise";
    public static final String URL_NOTIF_LIST = BASE_URL + "Notif";
    public static final String URL_NOTIF_READ = BASE_URL + "Notif/update_notif";
    public static final String URL_GREETING_LIST = BASE_URL + "Greeting/index";
    public static final String URL_STATUS_MASTER = BASE_URL + "Main/status";
    public static final String URL_MERCHANDISE_KONFIRMASI_UPLOAD = BASE_URL + "Merchandise/upload_merchandise";
    public static final String URL_MERCHANDISE_KONFIRMASI_KIRIM = BASE_URL + "Merchandise/simpan_merchandise";

    //Token heaader dengan enkripsi
    public static Map<String, String> getTokenHeader(String uuid){
        Map<String, String> header = new HashMap<>();
        header.put("Auth-key", "gmedia");
        header.put("Client-Service", "selby-ecommerce");
        header.put("User-id", uuid);

        String timestamp =  new SimpleDateFormat("SSSHHyyyyssMMddmm", Locale.getDefault()).format(new Date());
        String signature = sha256(uuid+"&"+timestamp,uuid+"die");

        /*System.out.println("UUID : " + uuid);
        System.out.println("Timestamp : " + timestamp);
        System.out.println("Signature : " + signature);*/

        header.put("Timestamp", timestamp);
        header.put("Signature", signature);
        return header;
    }

    private static String sha256(String message, String key) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            return Base64.encodeToString(sha256_HMAC.doFinal(message.getBytes()), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        Log.w("SHA256", "Return string kosong");
        return "";
    }

    public static String getSatuanWaktu(int i){
        switch (i){
            case 0:return "hari";
            case 1:return "bulan";
            case 2:return "tahun";
            default:return "";
        }
    }

    public static String getPathfromDrawable(int res_int){
        return Uri.parse("android.resource://"+R.class.getPackage().getName()+"/" + res_int).toString();
    }
}
