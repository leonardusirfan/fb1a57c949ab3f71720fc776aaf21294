package id.net.gmedia.selbiartis;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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

public class RiwayatActivity extends AppCompatActivity {

    private String tab_aktif = "feed";

    private List<NotifModel> listRiwayatFeed = new ArrayList<>();
    private List<NotifModel> listRiwayatBarang = new ArrayList<>();

    private RecyclerView rv_notif;
    private LoadMoreScrollListener loadManager;
    private NotifAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Riwayat");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("BERANDA"));
        tabLayout.addTab(tabLayout.newTab().setText("BARANG"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchTab(tab.getPosition()==0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        rv_notif = findViewById(R.id.rv_notif);
        rv_notif.setLayoutManager(new LinearLayoutManager(this));
        rv_notif.setItemAnimator(new DefaultItemAnimator());
        adapter = new NotifAdapter(this, listRiwayatFeed);
        rv_notif.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadRiwayat(false);
            }
        };
        rv_notif.addOnScrollListener(loadManager);

        loadRiwayat(true);
    }


    private void switchTab(boolean feed){
        if(feed){
            tab_aktif = "feed";
            rv_notif.setAdapter(new NotifAdapter(this, listRiwayatFeed));
            loadRiwayat(true);
        }
        else{
            tab_aktif = "barang";
            rv_notif.setAdapter(new NotifAdapter(this, listRiwayatBarang));
            loadRiwayat(true);
        }
    }

    private void loadRiwayat(final boolean init){
        final int LOAD_COUNT = 20;

        if(init){
            AppLoading.getInstance().showLoading(this);
            loadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("tipe", tab_aktif);
        body.add("start", loadManager.getLoaded());
        body.add("count", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_NOTIF_LIST, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            switch (tab_aktif){
                                case "feed":{
                                    listRiwayatFeed.clear();
                                    break;
                                }
                                case "barang":{
                                    listRiwayatBarang.clear();
                                    break;
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                switch (tab_aktif){
                                    case "feed":{
                                        listRiwayatFeed.clear();
                                        break;
                                    }
                                    case "barang":{
                                        listRiwayatBarang.clear();
                                        break;
                                    }
                                }
                            }

                            JSONArray list_notif = new JSONArray(result);

                            for(int i = 0; i < list_notif.length(); i++){
                                JSONObject notif = list_notif.getJSONObject(i);
                                NotifModel n = new NotifModel(notif.getString("id"), notif.getString("foto"),
                                        notif.getString("teks"), notif.getString("insert_at"),
                                        notif.getInt("read")==1);
                                switch (tab_aktif){
                                    case "feed":{
                                        listRiwayatFeed.add(n);
                                        break;
                                    }
                                    case "barang":{
                                        listRiwayatBarang.add(n);
                                        break;
                                    }
                                }
                            }

                            adapter.notifyDataSetChanged();
                            loadManager.finishLoad(list_notif.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(RiwayatActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(RiwayatActivity.this, message, Toast.LENGTH_SHORT).show();

                        loadManager.finishLoad(0);
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
