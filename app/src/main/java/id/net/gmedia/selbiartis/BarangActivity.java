package id.net.gmedia.selbiartis;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BarangActivity extends AppCompatActivity {

    private int JENIS_BARANG = Constant.BARANG_PRELOVED;
    private UserModel user;
    private String search = "";
    private boolean search_visible = false;
    private boolean aktif = true;

    //Variabel UI, adapter, dan list Barang
    private EditText txt_search;
    private TabLayout tab_barang;
    private RecyclerView rv_barang;
    private BarangAdapter adapter;
    private LoadMoreScrollListener loadManager;
    private List<BarangModel> listBarangAktif = new ArrayList<>();
    private List<BarangModel> listBarangNonAktif = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barang);

        //Inisialisasi Artis
        if(getIntent().hasExtra(Constant.EXTRA_USER)){
            Gson gson = new Gson();
            user = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_USER), UserModel.class);
        }
        if(getIntent().hasExtra(Constant.EXTRA_JENIS_BARANG)){
            JENIS_BARANG = getIntent().getIntExtra(Constant.EXTRA_JENIS_BARANG, Constant.BARANG_PRELOVED);
        }

        //Inisialisasi toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(user.getNama());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Inisialisasi TabLayout
        tab_barang = findViewById(R.id.tab_barang);
        tab_barang.addTab(tab_barang.newTab().setText("Aktif"));
        tab_barang.addTab(tab_barang.newTab().setText("Terjual"));
        tab_barang.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchBarang(tab.getPosition()==0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        rv_barang = findViewById(R.id.rv_barang);
        adapter = new BarangAdapter(this, listBarangAktif, JENIS_BARANG == Constant.BARANG_LELANG);
        rv_barang.setLayoutManager(new GridLayoutManager(this, 2));
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadBarang(false);
            }
        };
        rv_barang.addOnScrollListener(loadManager);

        txt_search = findViewById(R.id.txt_search);
        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!search_visible){
                    setSearch(true);
                }
                else{
                    search = txt_search.getText().toString();
                    loadBarang(true);

                    setSearch(false);
                }
            }
        });

        txt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search = txt_search.getText().toString();
                    loadBarang(true);

                    setSearch(false);
                    return true;
                }
                return false;
            }
        });

        //Inisialisasi barang jual artis
        loadBarang(true);
    }

    private void switchBarang(boolean is_aktif){
        if(is_aktif){
            aktif = true;

            adapter = new BarangAdapter(this, listBarangAktif, JENIS_BARANG == Constant.BARANG_LELANG);
            rv_barang.setAdapter(adapter);
            rv_barang.setLayoutManager(new GridLayoutManager(this, 2));
            loadBarang(true);
        }
        else{
            aktif = false;

            adapter = new BarangAdapter(this, listBarangNonAktif, JENIS_BARANG == Constant.BARANG_LELANG);
            rv_barang.setAdapter(adapter);
            rv_barang.setLayoutManager(new GridLayoutManager(this, 2));
            loadBarang(true);
        }
    }

    private void loadBarang(final boolean init){
        final int LOAD_COUNT = 10;

        if(init){
            AppLoading.getInstance().showLoading(this);
            loadManager.initLoad();
        }

        //Inisialisasi Barang Jualan Artis dari Web Service
        String url;
        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("count", LOAD_COUNT);
        body.add("keyword", search);
        body.add("aktif", aktif);

        if(JENIS_BARANG == Constant.BARANG_LELANG){
            url = Constant.URL_BARANG_LELANG;
        }
        else{
            url = Constant.URL_BARANG_MASTER;
            if(JENIS_BARANG == Constant.BARANG_PRELOVED){
                body.add("jenis", "1");
                body.add("donasi", "0");
            }
            else if(JENIS_BARANG == Constant.BARANG_MERCHANDISE){
                body.add("jenis", "2");
                body.add("donasi", "0");
            }
            else if(JENIS_BARANG == Constant.BARANG_DONASI){
                body.add("jenis", "");
                body.add("donasi", "1");
            }
        }

        ApiVolleyManager.getInstance().addRequest(this, url,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            findViewById(R.id.rv_barang).setVisibility(View.GONE);
                            findViewById(R.id.txt_kosong).setVisibility(View.VISIBLE);

                            listBarangAktif.clear();
                            listBarangNonAktif.clear();
                        }

                        adapter.notifyDataSetChanged();
                        AppLoading.getInstance().stopLoading();
                        loadManager.finishLoad(0);
                    }

                    @Override
                    public void onSuccess(String response) {
                        try{
                            if(init){
                                findViewById(R.id.rv_barang).setVisibility(View.VISIBLE);
                                findViewById(R.id.txt_kosong).setVisibility(View.GONE);
                                listBarangAktif.clear();
                                listBarangNonAktif.clear();
                            }

                            JSONArray result = new JSONArray(response);
                            for(int i = 0; i < result.length(); i++){
                                JSONObject barang = result.getJSONObject(i);
                                if(aktif){
                                    if(Constant.BARANG_LELANG == JENIS_BARANG){
                                        listBarangAktif.add(new BarangModel(JENIS_BARANG, barang.getString("id"),
                                                barang.getString("nama"), barang.getString("image"),
                                                barang.getDouble("harga")));
                                    }
                                    else{
                                        listBarangAktif.add(new BarangModel(JENIS_BARANG,
                                                barang.getString("id"), barang.getString("nama"),
                                                barang.getString("image"), barang.getDouble("harga"),
                                                barang.getInt("stok"), barang.getInt("terjual")));
                                    }
                                }
                                else{
                                    if(Constant.BARANG_LELANG == JENIS_BARANG){
                                        listBarangNonAktif.add(new BarangModel(JENIS_BARANG, barang.getString("id"),
                                                barang.getString("nama"), barang.getString("image"),
                                                barang.getDouble("harga")));
                                    }
                                    else{
                                        listBarangNonAktif.add(new BarangModel(JENIS_BARANG,
                                                barang.getString("id"),
                                                barang.getString("nama"),barang.getString("image"),
                                                barang.getDouble("harga"), barang.getInt("stok"),
                                                barang.getInt("terjual")));
                                    }
                                }
                            }

                            adapter.notifyDataSetChanged();

                            AppLoading.getInstance().stopLoading();
                            loadManager.finishLoad(result.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(BarangActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.toString());

                            AppLoading.getInstance().stopLoading();
                            loadManager.finishLoad(0);
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(BarangActivity.this, message, Toast.LENGTH_SHORT).show();

                        AppLoading.getInstance().stopLoading();
                        loadManager.finishLoad(0);
                    }
                }));
    }

    private void setSearch(boolean is_search){
        if(is_search){
            search_visible = true;

            tab_barang.setVisibility(View.GONE);
            txt_search.setVisibility(View.VISIBLE);

            txt_search.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(txt_search, InputMethodManager.SHOW_IMPLICIT);
        }
        else{
            search_visible = false;
            /*txt_search.setText("");
            search = "";*/

            tab_barang.setVisibility(View.VISIBLE);
            txt_search.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if(search_visible){
            setSearch(false);
        }
        else{
            super.onBackPressed();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
