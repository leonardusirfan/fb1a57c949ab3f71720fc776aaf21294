package id.net.gmedia.selbiartis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.LoadMoreScrollListener;
import com.leonardus.irfan.SimpleObjectModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MerchandiseOrderActivity extends AppCompatActivity {

    //Variabel pencarian barang
    private String kategori = "";
    private String search = "";

    //Variabel list dan adapter
    private List<MerchandiseModel> listBarang = new ArrayList<>();
    private List<SimpleObjectModel> listKategori = new ArrayList<>();
    private KategoriAdapter kategoriAdapter;
    private RecyclerView.Adapter barangAdapter;
    private LoadMoreScrollListener loadMoreScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandise_order);

        //Inisialisasi Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        //Inisialisasi UI
        RecyclerView rv_kategori = findViewById(R.id.rv_kategori);
        RecyclerView rv_barang = findViewById(R.id.rv_list);
        EditText txt_search = findViewById(R.id.txt_search);
        txt_search.setHint(R.string.cari_barang);

        //Init Recycler View dan Adapter
        listKategori.add(new SimpleObjectModel("", "Semua Kategori"));
        kategoriAdapter = new KategoriAdapter(this, listKategori);
        rv_kategori.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_kategori.setItemAnimator(new DefaultItemAnimator());
        rv_kategori.setAdapter(kategoriAdapter);

        //Inisialisasi Recycler View & Adapter
        barangAdapter = new MerchandiseAdapter(this, listBarang);
        rv_barang.setItemAnimator(new DefaultItemAnimator());
        rv_barang.setLayoutManager(new GridLayoutManager(this, 2));
        rv_barang.setAdapter(barangAdapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadMerchandise(false);
            }
        };
        rv_barang.addOnScrollListener(loadMoreScrollListener);

        txt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search = s.toString();
                loadMerchandise(false);
            }
        });

        //Inisialisasi Kategori
        initKategori();
    }

    @Override
    protected void onResume() {
        loadMerchandise(true);
        super.onResume();
    }

    //Mengubah kategori
    public void setKategori(String kategori){
        this.kategori = kategori;
        loadMerchandise(true);
    }

    private void initKategori(){
        JSONBuilder body = new JSONBuilder();
        body.add("start", 0);
        body.add("count", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_KATEGORI_BARANG, ApiVolleyManager.METHOD_POST,
                Constant.HEADER_AUTH, body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONArray arraykategori = new JSONArray(result);
                            for(int i = 0; i < arraykategori.length(); i++){
                                listKategori.add(new SimpleObjectModel(arraykategori.getJSONObject(i).getString("id"),
                                        arraykategori.getJSONObject(i).getString("category")));
                            }
                            kategoriAdapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(MerchandiseOrderActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MerchandiseOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadMerchandise(final boolean init){
        final int LOAD_COUNT = 12;

        if (init){
            AppLoading.getInstance().showLoading(this);
            loadMoreScrollListener.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadMoreScrollListener.getLoaded());
        body.add("count", LOAD_COUNT);
        body.add("keyword", search);
        body.add("kategori", kategori);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MERCHANDISE_LIST, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listBarang.clear();
                            barangAdapter.notifyDataSetChanged();
                        }

                        loadMoreScrollListener.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listBarang.clear();
                            }

                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject merchandise = response.getJSONObject(i);
                                listBarang.add(new MerchandiseModel(merchandise.getString("id"),
                                        merchandise.getString("nama"), merchandise.getString("image")));
                            }

                            barangAdapter.notifyDataSetChanged();
                            loadMoreScrollListener.finishLoad(response.length());

                        }
                        catch (JSONException e){
                            Toast.makeText(MerchandiseOrderActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            loadMoreScrollListener.failedLoad();
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MerchandiseOrderActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadMoreScrollListener.failedLoad();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
