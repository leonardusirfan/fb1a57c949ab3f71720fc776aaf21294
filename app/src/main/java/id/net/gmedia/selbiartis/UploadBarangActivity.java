package id.net.gmedia.selbiartis;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import id.net.gmedia.selbiartis.Upload.UploadAdapter;
import id.net.gmedia.selbiartis.Upload.UploadModel;

public class UploadBarangActivity extends AppCompatActivity {

    //Variabel UI
    private Dialog dialog;
    private EditText txt_nama, txt_harga, txt_berat, txt_deskripsi, txt_ukuran;
    private EditText txt_harga_normal, txt_lama_terpakai;
    //private TextView lbl_harga, lbl_harga_normal;
    private TextView txt_selesai_lelang, lbl_lama_terpakai;
    private Spinner spn_satuan_berat, spn_kondisi, spn_kategori, spn_brand, spn_lama_terpakai;
    private LinearLayout layout_lama_terpakai;
    private CheckBox cb_donasi, cb_lelang;

    //Adapter & List Upload Gambar
    private UploadAdapter uploadAdapter;
    private List<UploadModel> listUpload = new ArrayList<>();

    //list objek kategori
    private List<SimpleObjectModel> listKategori = new ArrayList<>();
    private List<SimpleObjectModel> listBrand = new ArrayList<>();

    //flag apakah barang yang diupload adalah barang lelang
    private int JENIS_BARANG;

    //variabel waktu lelang
    private int endyear, endmonth, enddate, endhour, endminute;
    private int startyear, startmonth, startdate, starthour, startminute;
    private String end = "";
    private String start = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_barang);

        //Inisialisasi Toolbar
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });*/
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.upload_barang);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi UI
        txt_nama = findViewById(R.id.txt_nama);
        txt_harga = findViewById(R.id.txt_harga);
        txt_berat = findViewById(R.id.txt_berat);
        txt_deskripsi = findViewById(R.id.txt_deskripsi);
        txt_ukuran = findViewById(R.id.txt_ukuran);
        //txt_harga_normal = findViewById(R.id.txt_harga_normal);
        txt_selesai_lelang = findViewById(R.id.txt_selesai_lelang);
        //lbl_harga = findViewById(R.id.lbl_harga);
        //lbl_harga_normal = findViewById(R.id.lbl_harga_normal);
        //lbl_selesai_lelang = findViewById(R.id.lbl_selesai_lelang);
        spn_satuan_berat = findViewById(R.id.spn_satuan_berat);
        spn_kategori = findViewById(R.id.spn_kategori);
        spn_kondisi = findViewById(R.id.spn_kondisi);
        spn_brand = findViewById(R.id.spn_brand);
        lbl_lama_terpakai = findViewById(R.id.lbl_lama_terpakai);
        layout_lama_terpakai = findViewById(R.id.layout_lama_terpakai);
        txt_lama_terpakai = findViewById(R.id.txt_lama_terpakai);
        spn_lama_terpakai = findViewById(R.id.spn_lama_terpakai);
        cb_donasi = findViewById(R.id.cb_donasi);
        cb_lelang = findViewById(R.id.cb_lelang);
        txt_selesai_lelang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                startyear = c.get(Calendar.YEAR);
                startmonth = c.get(Calendar.MONTH);
                startdate = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(UploadBarangActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                endyear = year;
                                endmonth = monthOfYear;
                                enddate = dayOfMonth;

                                Calendar c = Calendar.getInstance();
                                starthour = c.get(Calendar.HOUR_OF_DAY);
                                startminute = c.get(Calendar.MINUTE);

                                TimePickerDialog timePickerDialog = new TimePickerDialog(UploadBarangActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                endhour = hourOfDay;
                                                endminute = minute;

                                                start = Converter.DTToString(startyear, startmonth + 1, startdate, starthour, startminute, 0);
                                                end = Converter.DTToString(endyear, endmonth + 1,
                                                        enddate, endhour, endminute, 0);
                                                String show = start + " s/d\n" + end;
                                                txt_selesai_lelang.setText(show);
                                                System.out.println(validDate());
                                            }
                                        }, starthour, startminute, true);
                                timePickerDialog.show();
                            }
                        }, startyear, startmonth, startdate);
                datePickerDialog.show();
            }
        });
        RecyclerView rv_foto = findViewById(R.id.rv_foto);

        txt_ukuran.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    spn_kondisi.performClick();
                    return true;
                }
                return false;
            }
        });

        //Inisialisasi variabel global jenis barang
        if(getIntent().hasExtra(Constant.EXTRA_JENIS_BARANG)){
            JENIS_BARANG = getIntent().getIntExtra(Constant.EXTRA_JENIS_BARANG, 0);
            if(JENIS_BARANG == Constant.BARANG_LELANG){
                initLelang();
            }
        }

        cb_lelang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    findViewById(R.id.layout_lelang).setVisibility(View.VISIBLE);
                }
                else{
                    findViewById(R.id.layout_lelang).setVisibility(View.GONE);
                }
            }
        });

        cb_donasi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    findViewById(R.id.layout_donasi).setVisibility(View.VISIBLE);
                }
                else{
                    findViewById(R.id.layout_donasi).setVisibility(View.GONE);
                }
            }
        });

        spn_kondisi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    lbl_lama_terpakai.setVisibility(View.GONE);
                    layout_lama_terpakai.setVisibility(View.GONE);
                }
                else{
                    lbl_lama_terpakai.setVisibility(View.VISIBLE);
                    layout_lama_terpakai.setVisibility(View.VISIBLE);
                    /*txt_lama_terpakai.requestFocus();
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_nama.getText().toString().equals("")){
                    Toast.makeText(UploadBarangActivity.this, "Nama barang belum terisi", Toast.LENGTH_SHORT).show();
                }
                else if(txt_harga.getText().toString().equals("")){
                    Toast.makeText(UploadBarangActivity.this, "Harga barang belum terisi", Toast.LENGTH_SHORT).show();
                }
                else if(uploadAdapter.getMainImage().equals("")){
                    Toast.makeText(UploadBarangActivity.this, "Upload minimal 1 gambar", Toast.LENGTH_SHORT).show();
                }
                else if(!uploadAdapter.isAllUploaded()){
                    Toast.makeText(UploadBarangActivity.this, "Tunggu semua gambar ter-Upload", Toast.LENGTH_SHORT).show();
                }
                else if(JENIS_BARANG == Constant.BARANG_LELANG) {
                    if(end.equals("") || start.equals("")){
                        Toast.makeText(UploadBarangActivity.this, "Waktu lelang belum diisi", Toast.LENGTH_SHORT).show();
                    }
                    else if(!validDate()){
                        Toast.makeText(UploadBarangActivity.this, "Waktu lelang tidak valid", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        uploadBarang();
                    }
                }
                else{
                    uploadBarang();
                }
            }
        });

        initKategori();
        initBrand();

        //Inisialisasi Recycler View Upload Gambar Barang
        uploadAdapter = new UploadAdapter(this, listUpload,  Constant.URL_UPLOAD_GAMBAR_BARANG);
        rv_foto.setItemAnimator(new DefaultItemAnimator());
        rv_foto.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_foto.setAdapter(uploadAdapter);
    }

    private void initKategori(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", 0);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_HOME_CATEGORY,
                ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    List<String> spinnerItem = new ArrayList<>();
                    JSONArray array = new JSONArray(result);
                    for(int i = 0; i < array.length(); i++){
                        listKategori.add(new SimpleObjectModel(array.getJSONObject(i).getString("id"),
                                array.getJSONObject(i).getString("category")));
                        spinnerItem.add(listKategori.get(i).getValue());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            UploadBarangActivity.this, android.R.layout.simple_spinner_item, spinnerItem);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spn_kategori.setAdapter(adapter);
                }
                catch (JSONException e){
                    Toast.makeText(UploadBarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(UploadBarangActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void initBrand(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", 0);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_HOME_BRAND,
                ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String result) {
                try{
                    List<String> spinnerItem = new ArrayList<>();
                    JSONArray array = new JSONArray(result);
                    spinnerItem.add("");
                    listBrand.add(new SimpleObjectModel("0", ""));
                    for(int i = 0; i < array.length(); i++){
                        listBrand.add(new SimpleObjectModel(array.getJSONObject(i).getString("id"), array.getJSONObject(i).getString("brand")));
                        spinnerItem.add(array.getJSONObject(i).getString("brand"));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            UploadBarangActivity.this, android.R.layout.simple_spinner_item, spinnerItem);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spn_brand.setAdapter(adapter);
                }
                catch (JSONException e){
                    Toast.makeText(UploadBarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                    Log.e(Constant.TAG, e.getMessage());
                }
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(UploadBarangActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private void initLelang(){
        findViewById(R.id.layout_lelang).setVisibility(View.VISIBLE);
        txt_selesai_lelang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                startyear = c.get(Calendar.YEAR);
                startmonth = c.get(Calendar.MONTH);
                startdate = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(UploadBarangActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                endyear = year;
                                endmonth = monthOfYear;
                                enddate = dayOfMonth;

                                Calendar c = Calendar.getInstance();
                                starthour = c.get(Calendar.HOUR_OF_DAY);
                                startminute = c.get(Calendar.MINUTE);

                                TimePickerDialog timePickerDialog = new TimePickerDialog(UploadBarangActivity.this,
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                                endhour = hourOfDay;
                                                endminute = minute;

                                                start = Converter.DTToString(startyear, startmonth + 1, startdate, starthour, startminute, 0);
                                                end = Converter.DTToString(endyear, endmonth + 1,
                                                        enddate, endhour, endminute, 0);
                                                String show = start + " s/d\n" + end;
                                                txt_selesai_lelang.setText(show);
                                                System.out.println(validDate());
                                            }
                                        }, starthour, startminute, true);
                                timePickerDialog.show();
                            }
                        }, startyear, startmonth, startdate);
                datePickerDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 999 && data.hasExtra(Pix.IMAGE_RESULTS)) {
            uploadAdapter.upload(data.getStringArrayListExtra(Pix.IMAGE_RESULTS));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadBarang(){
        JSONBuilder body = new JSONBuilder();
        body.add("nama", txt_nama.getText().toString());
        body.add("harga", txt_harga.getText().toString());
        body.add("berat", txt_berat.getText().toString());
        body.add("satuan_berat", spn_satuan_berat.getSelectedItem().toString());
        body.add("deskripsi", txt_deskripsi.getText().toString());
        body.add("ukuran", txt_ukuran.getText().toString());
        body.add("foto", uploadAdapter.getMainImage());
        body.add("gallery", new JSONArray(uploadAdapter.getListImage()));
        body.add("kondisi", spn_kondisi.getSelectedItemPosition() == 0 ? 1 : 0 );
        body.add("brand", listBrand.get(spn_brand.getSelectedItemPosition()).getId());
        body.add("kategori", listKategori.get(spn_kategori.getSelectedItemPosition()).getId());
        body.add("lelang", JENIS_BARANG == Constant.BARANG_LELANG || cb_lelang.isChecked() ?"1":"0");
        body.add("donasi", cb_donasi.isChecked()?"1":"0");
        switch (JENIS_BARANG) {
            case Constant.BARANG_LELANG:
                body.add("start", start);
                body.add("end", end);
                break;
            case Constant.BARANG_PRELOVED:
                body.add("start", "");
                body.add("end", "");
                body.add("jenis", "1");
                break;
            case Constant.BARANG_MERCHANDISE:
                body.add("start", "");
                body.add("end", "");
                body.add("jenis", "2");
                break;
        }
        body.add("pemakaian", txt_lama_terpakai.getText().toString().equals("")?0:Integer.parseInt(txt_lama_terpakai.getText().toString()));
        body.add("satuan_pemakaian", Constant.getSatuanWaktu(spn_lama_terpakai.getSelectedItemPosition()));

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_UPLOAD_BARANG, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
            @Override
            public void onEmpty(String message) {
                Toast.makeText(UploadBarangActivity.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(String result) {
                Intent i = new Intent(UploadBarangActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("start", 1);
                startActivity(i);
            }

            @Override
            public void onFail(String message) {
                Toast.makeText(UploadBarangActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    private boolean validDate(){
        if(startyear < endyear){
            return true;
        }
        else if(startyear == endyear){
            if(startmonth < endmonth){
                return true;
            }
            else if(startmonth == endmonth){
                if(startdate < enddate){
                    return true;
                }
                else if(startdate == enddate){
                    return starthour < endhour;
                }
            }
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
