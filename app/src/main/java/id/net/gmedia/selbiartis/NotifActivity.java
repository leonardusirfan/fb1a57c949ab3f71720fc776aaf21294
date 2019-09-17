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

public class NotifActivity extends AppCompatActivity {

    private List<NotifModel> listNotifSosial = new ArrayList<>();
    private List<NotifModel> listNotifOrder = new ArrayList<>();

    private RecyclerView rv_notif;
    private LoadMoreScrollListener sosialLoadManager;
    private LoadMoreScrollListener pesananLoadManager;
    private NotifAdapter sosialAdapter;
    private NotifAdapter pesananAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notif);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Pemberitahuan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("SOSIAL"));
        tabLayout.addTab(tabLayout.newTab().setText("PESANAN"));
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

        sosialAdapter = new NotifAdapter(this, listNotifSosial);
        pesananAdapter = new NotifAdapter(this, listNotifOrder);

        rv_notif.setAdapter(sosialAdapter);
        sosialLoadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadSosial(false);
            }
        };
        pesananLoadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadPesanan(false);
            }
        };
        rv_notif.addOnScrollListener(sosialLoadManager);

        loadSosial(true);
        loadPesanan(true);
    }


    private void switchTab(boolean sosial){
        if(sosial){
            rv_notif.setAdapter(sosialAdapter);
            rv_notif.clearOnScrollListeners();
            rv_notif.addOnScrollListener(sosialLoadManager);
        }
        else{
            rv_notif.setAdapter(pesananAdapter);
            rv_notif.clearOnScrollListeners();
            rv_notif.addOnScrollListener(pesananLoadManager);
        }
    }

    private void loadPesanan(final boolean init){
        AppLoading.getInstance().showLoading(this);
        final int LOAD_COUNT = 20;

        if(init){
            pesananLoadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("tipe", "pesanan");
        body.add("start", pesananLoadManager.getLoaded());
        body.add("count", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_NOTIF_LIST, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listNotifOrder.clear();
                            pesananAdapter.notifyDataSetChanged();
                        }

                        pesananLoadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listNotifOrder.clear();
                                pesananAdapter.notifyDataSetChanged();
                            }

                            JSONArray list_notif = new JSONArray(result);

                            for(int i = 0; i < list_notif.length(); i++){
                                JSONObject notif = list_notif.getJSONObject(i);
                                NotifModel n = new NotifModel(notif.getString("id"), notif.getString("foto"),
                                        notif.getString("teks"), notif.getString("insert_at"),
                                        notif.getInt("read")==1);
                                listNotifOrder.add(n);
                            }

                            pesananAdapter.notifyDataSetChanged();
                            pesananLoadManager.finishLoad(list_notif.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(NotifActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            pesananLoadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(NotifActivity.this, message, Toast.LENGTH_SHORT).show();

                        pesananLoadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    private void loadSosial(final boolean init){
        AppLoading.getInstance().showLoading(this);
        final int LOAD_COUNT = 20;

        if(init){
            sosialLoadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("tipe", "sosial");
        body.add("start", sosialLoadManager.getLoaded());
        body.add("count", LOAD_COUNT);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_NOTIF_LIST, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listNotifSosial.clear();
                            sosialAdapter.notifyDataSetChanged();
                        }

                        sosialLoadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listNotifSosial.clear();
                                sosialAdapter.notifyDataSetChanged();
                            }

                            JSONArray list_notif = new JSONArray(result);

                            for(int i = 0; i < list_notif.length(); i++){
                                JSONObject notif = list_notif.getJSONObject(i);
                                NotifModel n = new NotifModel(notif.getString("id"), notif.getString("foto"),
                                        notif.getString("teks"), notif.getString("insert_at"),
                                        notif.getInt("read")==1);
                                listNotifSosial.add(n);
                            }

                            sosialAdapter.notifyDataSetChanged();
                            sosialLoadManager.finishLoad(list_notif.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(NotifActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            sosialLoadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(NotifActivity.this, message, Toast.LENGTH_SHORT).show();

                        sosialLoadManager.finishLoad(0);
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
