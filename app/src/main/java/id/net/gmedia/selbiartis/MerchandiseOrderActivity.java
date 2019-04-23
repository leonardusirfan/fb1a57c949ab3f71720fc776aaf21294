package id.net.gmedia.selbiartis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
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
    /*private String kategori = "";
    private String search = "";*/
    private String nama_artis = "";

    //Variabel list dan adapter
    private RecyclerView rv_rekomendasi;
    private List<MerchandiseModel> listBarang = new ArrayList<>();
    private List<SimpleObjectModel> listRekomendasi = new ArrayList<>();
    private SpecialOfferAdapter rekomendasiAdapter;
    private RecyclerView.Adapter barangAdapter;
    private LoadMoreScrollListener loadMoreScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandise_order);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Merchandise");
        }

        //Inisialisasi UI
        rv_rekomendasi = findViewById(R.id.rv_rekomendasi);
        RecyclerView rv_merchandise = findViewById(R.id.rv_merchandise);
        TextView txt_special_offer = findViewById(R.id.txt_special_offer);

        if(getIntent().hasExtra(Constant.EXTRA_NAMA_USER)){
            nama_artis = getIntent().getStringExtra(Constant.EXTRA_NAMA_USER);
        }

        //Init Recycler View dan Adapter
        /*rekomendasiAdapter = new KategoriAdapter(this, listKategori);
        rv_kategori.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv_kategori.setItemAnimator(new DefaultItemAnimator());
        rv_kategori.setAdapter(kategoriAdapter);*/

        String special_offer = "Special Offer";
        if(!nama_artis.equals("")){
            special_offer += "untuk " + nama_artis;
        }
        txt_special_offer.setText(special_offer);

        //Inisialisasi Recycler View & Adapter
        barangAdapter = new MerchandiseAdapter(this, listBarang);
        rv_merchandise.setItemAnimator(new DefaultItemAnimator());
        rv_merchandise.setLayoutManager(new GridLayoutManager(this, 2));
        rv_merchandise.setAdapter(barangAdapter);
        loadMoreScrollListener = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadMerchandise(false);
            }
        };
        rv_merchandise.addOnScrollListener(loadMoreScrollListener);

        loadSpecialOffer();
        loadMerchandise(true);
    }

    @Override
    protected void onResume() {
        loadMerchandise(true);
        super.onResume();
    }

    private void loadSpecialOffer(){
        listRekomendasi.add(new SimpleObjectModel("", Converter.getURLForResource("id.net.gmedia.selbiartis", R.drawable.offer1)));
        listRekomendasi.add(new SimpleObjectModel("", Converter.getURLForResource("id.net.gmedia.selbiartis", R.drawable.offer2)));

        if(listRekomendasi.size() > 0){
            //String offer = "Special offer untuk " + artis.getNama();
            findViewById(R.id.layout_rekomendasi).setVisibility(View.VISIBLE);

            rv_rekomendasi.setItemAnimator(new DefaultItemAnimator());
            rv_rekomendasi.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            rekomendasiAdapter = new SpecialOfferAdapter(this, listRekomendasi);
            rv_rekomendasi.setAdapter(rekomendasiAdapter);
        }
    }

    //Mengubah kategori
    /*public void setKategori(String kategori){
        this.kategori = kategori;
        loadMerchandise(true);
    }*/

    /*private void initKategori(){
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
    }*/

    private void loadMerchandise(final boolean init){
        final int LOAD_COUNT = 12;

        if (init){
            AppLoading.getInstance().showLoading(this);
            loadMoreScrollListener.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadMoreScrollListener.getLoaded());
        body.add("count", LOAD_COUNT);
        /*body.add("keyword", search);
        body.add("kategori", kategori);*/

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
