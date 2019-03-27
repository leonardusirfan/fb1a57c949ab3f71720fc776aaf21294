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

public class OrderActivity extends AppCompatActivity {

    private List<OrderModel> listOrder = new ArrayList<>();
    private List<OrderModel> listOrderPenawaran = new ArrayList<>();
    private RecyclerView rv_order;
    private LoadMoreScrollListener loadManager;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Pesanan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Semua Pesanan"));
        tabLayout.addTab(tabLayout.newTab().setText("Penawaran Desain"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchOrder(tab.getPosition()==0);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        rv_order = findViewById(R.id.rv_order);
        rv_order.setLayoutManager(new LinearLayoutManager(this));
        rv_order.setItemAnimator(new DefaultItemAnimator());
        adapter = new OrderAdapter(this, listOrder);
        rv_order.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadOrder(false);
            }
        };
        rv_order.addOnScrollListener(loadManager);

        loadOrder(true);
    }

    private void switchOrder(boolean semua){
        if(semua){
            rv_order.setAdapter(new OrderAdapter(this, listOrder));
        }
        else{
            rv_order.setAdapter(new OrderAdapter(this, listOrderPenawaran));
        }
    }

    private void loadOrder(final boolean init){
        if(init){
            loadManager.initLoad();
            AppLoading.getInstance().showLoading(this);
        }

        JSONBuilder body = new JSONBuilder();
        body.add("start", loadManager.getLoaded());
        body.add("count", 10);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_ORDER_LIST, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listOrder.clear();
                            listOrderPenawaran.clear();
                            adapter.notifyDataSetChanged();
                        }

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listOrder.clear();
                                listOrderPenawaran.clear();
                            }
                            JSONArray response = new JSONArray(result);

                            for(int i = 0; i < response.length(); i++){
                                JSONObject order = response.getJSONObject(i);
                                OrderModel o = new OrderModel(order.getString("id_order"),
                                        order.getString("id_merchandise"), order.getString("merchandise"),
                                        order.getString("path") + order.getString("image"),
                                        order.getInt("jumlah"), order.getDouble("harga"), order.getString("tgl"),
                                        order.getString("keterangan"));
                                listOrder.add(o);
                                if(order.getInt("status") == 2){
                                    listOrderPenawaran.add(o);
                                }
                            }

                            loadManager.finishLoad(response.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(OrderActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(OrderActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    public void batalOrder(String id_order){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("id_order", id_order);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MERCHANDISE_BATAL, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        AppLoading.getInstance().stopLoading();
                        loadOrder(true);
                    }

                    @Override
                    public void onFail(String message) {
                        AppLoading.getInstance().stopLoading();
                        Toast.makeText(OrderActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
