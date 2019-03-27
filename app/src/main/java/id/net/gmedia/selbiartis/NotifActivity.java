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

    private String tab_aktif = "sosial";

    private List<NotifModel> listNotifSosial = new ArrayList<>();
    private List<NotifModel> listNotifOrder = new ArrayList<>();

    private RecyclerView rv_notif;
    private LoadMoreScrollListener loadManager;
    private NotifAdapter adapter;

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
        adapter = new NotifAdapter(this, listNotifSosial);
        rv_notif.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadNotif(false);
            }
        };
        rv_notif.addOnScrollListener(loadManager);

        loadNotif(true);
    }


    private void switchTab(boolean sosial){
        if(sosial){
            tab_aktif = "sosial";
            rv_notif.setAdapter(new NotifAdapter(this, listNotifSosial));
            loadNotif(true);
        }
        else{
            tab_aktif = "pesanan";
            rv_notif.setAdapter(new NotifAdapter(this, listNotifOrder));
            loadNotif(true);
        }
    }

    private void loadNotif(final boolean init){
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
                                case "sosial":{
                                    listNotifSosial.clear();
                                    break;
                                }
                                case "pesanan":{
                                    listNotifOrder.clear();
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
                                    case "sosial":{
                                        listNotifSosial.clear();
                                        break;
                                    }
                                    case "pesanan":{
                                        listNotifOrder.clear();
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
                                    case "sosial":{
                                        listNotifSosial.add(n);
                                        break;
                                    }
                                    case "pesanan":{
                                        listNotifOrder.add(n);
                                        break;
                                    }
                                }
                            }

                            adapter.notifyDataSetChanged();
                            loadManager.finishLoad(list_notif.length());
                        }
                        catch (JSONException e){
                            Toast.makeText(NotifActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(NotifActivity.this, message, Toast.LENGTH_SHORT).show();

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
       /* NotifModel n = new NotifModel("", "http://gmedia.bz/selbi/assets/uploads/profile/foto/1547804746202.jpg",
                Converter.setSelectionBold("Chelsea Islan mulai mengikuti anda",
                        new ArrayList<>( Arrays.asList("Chelsea Islan"))), "2019-03-04");
        listNotifSosial.add(n);

        n = new NotifModel("", "https://graph.facebook.com/2026495770745083/picture",
                Converter.setSelectionBold("Irfan BayuMahendra mengomentari produk Exclusive Hat Chelsea Islan",
                        new ArrayList<>( Arrays.asList("Irfan BayuMahendra", "Exclusive Hat Chelsea Islan"))), "2019-03-04");
        listNotifSosial.add(n);

        n = new NotifModel("", "http://gmedia.bz/selbi/assets/uploads/profile/foto/1547804746202.jpg",
                Converter.setSelectionBold("Chelsea Islan membeli Exclusive Hat Via Vallen",
                        new ArrayList<>( Arrays.asList("Chelsea Islan", "Exclusive Hat Via Vallen"))), "2019-03-04", false);
        listNotifOrder.add(n);

        n = new NotifModel("", "https://graph.facebook.com/2026495770745083/picture",
                Converter.setSelectionBold("Irfan BayuMahendra membeli Exclusive Hat Via Vallen",
                        new ArrayList<>( Arrays.asList("Irfan BayuMahendra", "Exclusive Hat Via Vallen"))), "2019-03-04", true);
        listNotifOrder.add(n);*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
