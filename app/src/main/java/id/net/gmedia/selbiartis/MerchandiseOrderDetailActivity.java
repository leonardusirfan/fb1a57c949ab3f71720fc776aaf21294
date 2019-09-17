package id.net.gmedia.selbiartis;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.DialogFactory;
import com.leonardus.irfan.ImageSlider.ImageSlider;
import com.leonardus.irfan.ImageSlider.ImageSliderAdapter;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.SimpleSelectableObjectModel;
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback;
import com.midtrans.sdk.corekit.core.Constants;
import com.midtrans.sdk.corekit.core.LocalDataHandler;
import com.midtrans.sdk.corekit.core.MidtransSDK;
import com.midtrans.sdk.corekit.core.TransactionRequest;
import com.midtrans.sdk.corekit.models.BillInfoModel;
import com.midtrans.sdk.corekit.models.ItemDetails;
import com.midtrans.sdk.corekit.models.UserAddress;
import com.midtrans.sdk.corekit.models.UserDetail;
import com.midtrans.sdk.corekit.models.snap.TransactionResult;
import com.midtrans.sdk.uikit.SdkUIFlowBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MerchandiseOrderDetailActivity extends AppCompatActivity implements TransactionFinishedCallback {

    private static final String MERCHANT_BASE_CHECKOUT_URL = "http://gmedia.bz/selbi/api/transaksi/prepayment/";
    //SANDBOX
    private static final String MERCHANT_CLIENT_KEY = "SB-Mid-client-vFml_BHcmKOidw7B";

    private String id = "";
    private String id_order = "";
    private String nama_barang;
    private double harga_barang;

    private Toolbar toolbar;
    private ImageSlider slider;
    private CollapsingToolbarLayout collapsingToolbar;
    private TextView txt_title, txt_nama, txt_harga, txt_warna, txt_ukuran,
            txt_harga_jual, txt_catatan, txt_jumlah, txt_total, txt_keuntungan;

    private List<SimpleSelectableObjectModel> listWarna = new ArrayList<>();
    private List<SimpleSelectableObjectModel> listUkuran = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandise_order_detail);

        if(getIntent().hasExtra(Constant.EXTRA_ID_MERCHANDISE)){
            id = getIntent().getStringExtra(Constant.EXTRA_ID_MERCHANDISE);
        }

        if(getIntent().hasExtra(Constant.EXTRA_ID_ORDER)){
            id_order = getIntent().getStringExtra(Constant.EXTRA_ID_ORDER);
        }

        slider = findViewById(R.id.slider);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbar = findViewById(R.id.main_collapsing);
        txt_title = findViewById(R.id.txt_title);
        txt_nama = findViewById(R.id.txt_nama);
        txt_harga = findViewById(R.id.txt_harga);
        txt_ukuran = findViewById(R.id.txt_ukuran);
        txt_warna = findViewById(R.id.txt_warna);
        txt_catatan = findViewById(R.id.txt_catatan);
        txt_jumlah = findViewById(R.id.txt_jumlah);
        txt_total = findViewById(R.id.txt_total);
        txt_harga_jual = findViewById(R.id.txt_harga_jual);
        txt_keuntungan = findViewById(R.id.txt_keuntungan);
        txt_total.setText(Converter.doubleToRupiah(0));
        txt_keuntungan.setText(Converter.doubleToRupiah(0));

        txt_warna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listWarna.size() > 0){
                    showList(txt_warna, listWarna);
                }
                else{
                    Toast.makeText(MerchandiseOrderDetailActivity.this,
                            "Tidak ada warna tersedia", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txt_ukuran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listUkuran.size() > 0){
                    showList(txt_ukuran, listUkuran);
                }
                else{
                    Toast.makeText(MerchandiseOrderDetailActivity.this,
                            "Tidak ada ukuran tersedia", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_jumlah.getText().toString().equals("") ||
                        Integer.parseInt(txt_jumlah.getText().toString()) <= 0){
                    Toast.makeText(MerchandiseOrderDetailActivity.this,
                            "Pemesanan minimal 1 barang", Toast.LENGTH_SHORT).show();
                }
                else if(Integer.parseInt(txt_jumlah.getText().toString()) > 100){
                    Toast.makeText(MerchandiseOrderDetailActivity.this,
                            "Pemesanan merchandise maksimal 100 barang", Toast.LENGTH_SHORT).show();
                }
                else if(txt_warna.getText().toString().equals("")){
                    Toast.makeText(MerchandiseOrderDetailActivity.this,
                            "Warna belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else if(txt_ukuran.getText().toString().equals("")){
                    Toast.makeText(MerchandiseOrderDetailActivity.this,
                            "Ukuran belum dipilih", Toast.LENGTH_SHORT).show();
                }
                else if(txt_harga_jual.getText().toString().equals("")){
                    Toast.makeText(MerchandiseOrderDetailActivity.this,
                            "Masukkan harga jual barang", Toast.LENGTH_SHORT).show();
                }
                else if(Double.parseDouble(txt_harga_jual.getText().toString()) < harga_barang){
                    Toast.makeText(MerchandiseOrderDetailActivity.this,
                            "Harga jual tidak boleh lebih kecil dari harga beli", Toast.LENGTH_SHORT).show();
                    String harga_jual = String.format(Locale.getDefault(), "%.0f", harga_barang);
                    txt_harga_jual.setText(harga_jual);
                }
                else{
                    pesanMerchandise();
                }
            }
        });

        txt_jumlah.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    txt_total.setText(Converter.doubleToRupiah(0));
                }
                else{
                    txt_total.setText(Converter.doubleToRupiah(harga_barang * Integer.parseInt(s.toString())));
                }
            }
        });

        txt_harga_jual.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equals("")){
                    txt_keuntungan.setText(Converter.doubleToRupiah(0));
                }
                else{
                    txt_keuntungan.setText(Converter.doubleToRupiah(Double.parseDouble(s.toString()) - harga_barang));
                }
            }
        });

        initToolbar();
        loadMerchandise();
    }

    private void initTransaksi(){
        //Inisialisasi transaksi Midtrans
        SdkUIFlowBuilder.init()
                .setClientKey(MERCHANT_CLIENT_KEY) // client_key is mandatory
                .setContext(this) // context is mandatory
                .setTransactionFinishedCallback(this) // set transaction finish callback (sdk callback)
                .setMerchantBaseUrl(MERCHANT_BASE_CHECKOUT_URL) //set merchant url (required) BASE_URL
                .enableLog(true)// enable sdk log (optional)
                .buildSDK();
    }

    private void pesanMerchandise(){
        JSONBuilder body = new JSONBuilder();
        body.add("id_order", id_order);
        body.add("id_merchandise", id);
        body.add("harga", txt_harga_jual.getText().toString());
        body.add("jumlah", Integer.parseInt(txt_jumlah.getText().toString()));
        List<String> warnaDipilih = new ArrayList<>();
        for(SimpleSelectableObjectModel w : listWarna){
            if(w.isSelected()){
                warnaDipilih.add(w.getValue());
            }
        }
        body.add("warna", new JSONArray(warnaDipilih));
        List<String> ukuranDipilih = new ArrayList<>();
        for(SimpleSelectableObjectModel w : listUkuran){
            if(w.isSelected()){
                ukuranDipilih.add(w.getValue());
            }
        }
        body.add("ukuran", new JSONArray(ukuranDipilih));
        body.add("keterangan", txt_catatan.getText().toString());

        //System.out.println("BODY " + body.create());
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MERCHANDISE_ORDER, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(MerchandiseOrderDetailActivity.this,
                                "Pemesanan berhasil", Toast.LENGTH_SHORT).show();

                        Intent resultIntent = new Intent(MerchandiseOrderDetailActivity.this, OrderActivity.class);
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MerchandiseOrderDetailActivity.this);
                        stackBuilder.addNextIntentWithParentStack(resultIntent);
                        stackBuilder.startActivities();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MerchandiseOrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void showList(final TextView txt_selected, final List<SimpleSelectableObjectModel> list){
        final Dialog dialog = DialogFactory.getInstance().createDialog(this, R.layout.popup_list,
                70, 40);

        RecyclerView rv_list = dialog.findViewById(R.id.rv_list);
        Button btn_selesai = dialog.findViewById(R.id.btn_selesai);

        rv_list.setLayoutManager(new LinearLayoutManager(this));
        rv_list.setItemAnimator(new DefaultItemAnimator());
        ListAdapter adapter = new ListAdapter(list);
        rv_list.setAdapter(adapter);

        btn_selesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder builder = new StringBuilder();
                boolean first = true;
                for(SimpleSelectableObjectModel l : list){
                    if(l.isSelected()){
                        if(first){
                            builder.append(l.getValue());
                            first = false;
                        }
                        else{
                            builder.append(", ");
                            builder.append(l.getValue());
                        }
                    }
                }

                txt_selected.setText(builder.toString());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void loadMerchandise(){
        JSONBuilder body = new JSONBuilder();
        body.add("id", id);
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MERCHANDISE_DETAIL, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(MerchandiseOrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        /*
                        {"nama":"Topi Pantai","harga":"95000","deskripsi":"Topi Pantai cocok untuk merchandise artis",
                        "image":[{"path":"http:\/\/gmedia.bz\/selbi\/assets\/uploads\/merchandise\/","image":"20190123095132hastings_hat_ash_choc.jpg"}]}
                        */
                        try{
                            System.out.println(result);
                            JSONObject merchandise = new JSONObject(result);

                            nama_barang = merchandise.getString("nama");
                            harga_barang = merchandise.getDouble("harga");

                            txt_nama.setText(nama_barang);
                            txt_harga.setText(Converter.doubleToRupiah(harga_barang));

                            List<String> listImage = new ArrayList<>();
                            JSONArray images = merchandise.getJSONArray("image");
                            for(int i = 0; i < images.length(); i++){
                                JSONObject path = images.getJSONObject(i);
                                listImage.add(path.getString("path") + path.getString("image"));
                            }

                            JSONArray ukuran = merchandise.getJSONArray("ukuran");
                            for(int i = 0; i < ukuran.length(); i++){
                                //System.out.println(ukuran.getString(i));
                                listUkuran.add(new SimpleSelectableObjectModel(ukuran.getString(i), ukuran.getString(i)));
                            }

                            JSONArray warna = merchandise.getJSONArray("warna");
                            for(int i = 0; i < warna.length(); i++){
                                //System.out.println(warna.getString(i));
                                listWarna.add(new SimpleSelectableObjectModel(warna.getString(i), warna.getString(i)));
                            }

                            initSlider(listImage);
                        }
                        catch (JSONException e){
                            Toast.makeText(MerchandiseOrderDetailActivity.this,
                                    R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MerchandiseOrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void initToolbar(){
        //Inisialisasi Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        collapsingToolbar.setTitle(" ");
        txt_title.setText("");
        AppBarLayout appBarLayout = findViewById(R.id.main_appbar);
        appBarLayout.setExpanded(true);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if(getSupportActionBar() != null){
                        if (appBarLayout.getTotalScrollRange() + verticalOffset <= getActionBarHeight()) {
                            if(nama_barang != null){
                                txt_title.setText(nama_barang);
                            }
                            isShow = true;
                            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        } else if (isShow) {
                            txt_title.setText("");
                            isShow = false;
                            getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.style_rectangle_gradient_black));
                        }
                    }
                }
            });
        }
    }

    private int getActionBarHeight() {
        int actionBarHeight = 0;
        if(getSupportActionBar() != null){
            actionBarHeight = getSupportActionBar().getHeight();
            if (actionBarHeight != 0)
                return actionBarHeight;
            final TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    private void initSlider(List<String> listImage){
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(this, listImage, true);
        slider.setAdapter(sliderAdapter);
    }

    @Override
    public void onTransactionFinished(TransactionResult result) {
        if (result.getResponse() != null) {
            switch (result.getStatus()) {
                case TransactionResult.STATUS_SUCCESS:
                    Toast.makeText(this, "Transaksi berhasil", Toast.LENGTH_SHORT).show();
                    Log.d(Constant.TAG, "Transaction Finished. ID: " + result.getResponse().getTransactionId());

                    //Kembali ke home
                    Toast.makeText(MerchandiseOrderDetailActivity.this,
                            "Pemesanan berhasil", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent(MerchandiseOrderDetailActivity.this, OrderActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(MerchandiseOrderDetailActivity.this);
                    stackBuilder.addNextIntentWithParentStack(resultIntent);
                    stackBuilder.startActivities();

                    break;
                case TransactionResult.STATUS_PENDING:
                    Toast.makeText(this, "Transaksi pending", Toast.LENGTH_SHORT).show();
                    Log.d(Constant.TAG, "Transaction Pending. ID: " + result.getResponse().getTransactionId());
                    break;
                case TransactionResult.STATUS_FAILED:
                    Toast.makeText(this, "Transaksi gagal", Toast.LENGTH_SHORT).show();
                    Log.d(Constant.TAG, "Transaction Failed. ID: " + result.getResponse().getTransactionId() + ". Message: " + result.getResponse().getStatusMessage());
                    break;
            }
            result.getResponse().getValidationMessages();
        } else if (result.isTransactionCanceled()) {
            Toast.makeText(this, "Transaksi dibatalkan", Toast.LENGTH_LONG).show();
        } else {
            if (result.getStatus().equalsIgnoreCase(TransactionResult.STATUS_INVALID)) {
                Toast.makeText(this, "Transaksi Invalid", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Transaction selesai dengan kegagalan.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void verifyUser(){
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PROFIL,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String response) {
                        try{
                            JSONObject akun = new JSONObject(response);

                            UserModel user = new UserModel(akun.getString("id"), akun.getString("profile_name"),
                                    akun.getString("alamat"), akun.getString("no_telp"),
                                    akun.getString("email"));

                            //userDetail = LocalDataHandler.readObject("user_details", UserDetail.class);
                            UserDetail userDetail = new UserDetail();
                            userDetail.setUserFullName(user.getNama());
                            userDetail.setEmail(user.getEmail());
                            userDetail.setPhoneNumber(user.getTelepon());

                            ArrayList<UserAddress> userAddresses = new ArrayList<>();
                            UserAddress userAddress = new UserAddress();
                            userAddress.setAddress(akun.getString("alamat"));
                            //userAddress.setCity("Jakarta");
                            userAddress.setAddressType(Constants.ADDRESS_TYPE_BILLING);
                            //userAddress.setZipcode("40184");
                            userAddress.setCountry("IDN");
                            userAddresses.add(userAddress);
                            userDetail.setUserAddresses(userAddresses);
                            LocalDataHandler.saveObject("user_details", userDetail);

                            pembayaran();
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                            Toast.makeText(MerchandiseOrderDetailActivity.this,
                                    R.string.error_json, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MerchandiseOrderDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void pembayaran(){
       /* ArrayList<ItemDetails> itemDetailsList = new ArrayList<>();
        for(ArtisModel a : listHeader){
            String header_id = a.getId();

            //Inisialisasi detail pembayaran
            for(int i = 0; i < listBarangBeli.get(header_id).size(); i++){
                BarangJualModel barang = listBarangBeli.get(header_id).get(i);
                itemDetailsList.add(new ItemDetails(barang.getId(), barang.getHarga(),
                        barang.getJumlah(), barang.getNama()));
            }

            //Biaya Ongkir
            itemDetailsList.add(new ItemDetails("0", listOngkir.get(header_id).getHarga(),
                    1, listOngkir.get(header_id).getService()));
        }

        String id_transaksi = String.valueOf(System.currentTimeMillis());
        TransactionRequest transactionRequest = new TransactionRequest(id_transaksi,
                harga_total_barang + harga_total_ongkir);
        Log.d(Constant.TAG, id_transaksi);
        transactionRequest.setItemDetails(itemDetailsList);

        BillInfoModel billInfoModel = new BillInfoModel("SELBI", "Transaksi");
        // Set the bill info on transaction details
        transactionRequest.setBillInfoModel(billInfoModel);
        MidtransSDK.getInstance().setTransactionRequest(transactionRequest);*/

        MidtransSDK.getInstance().startPaymentUiFlow(MerchandiseOrderDetailActivity.this);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
}
