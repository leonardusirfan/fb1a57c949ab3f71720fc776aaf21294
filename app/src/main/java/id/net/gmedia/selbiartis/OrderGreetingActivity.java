package id.net.gmedia.selbiartis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class OrderGreetingActivity extends AppCompatActivity {

    private OrderGreetingAdapter adapter;
    private List<OrderGreetingModel> listGreeting = new ArrayList<>();
    private LoadMoreScrollListener loadManager;

    private ArrayAdapter<String> spinner_adapter;
    private List<SimpleObjectModel> listStatus = new ArrayList<>();
    private List<String> stringStatus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_greeting);

        /*if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Request Greeting");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }*/

        AppCompatSpinner spn_status = findViewById(R.id.spn_status);
        listStatus.add(new SimpleObjectModel("", "Semua"));
        stringStatus.add("Semua");
        spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, stringStatus);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_status.setAdapter(spinner_adapter);
        spn_status.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadGreeting(true, listStatus.get(position).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RecyclerView rv_greeting = findViewById(R.id.rv_greeting);
        rv_greeting.setItemAnimator(new DefaultItemAnimator());
        rv_greeting.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderGreetingAdapter(this, listGreeting);
        rv_greeting.setAdapter(adapter);
        loadManager = new LoadMoreScrollListener() {
            @Override
            public void onLoadMore() {
                loadGreeting(false);
            }
        };
        rv_greeting.addOnScrollListener(loadManager);

        loadGreeting(true);
        loadStatus();
    }

    private void loadStatus(){
        JSONBuilder body = new JSONBuilder();
        body.add("kode", "");
        body.add("modul", "greeting");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_STATUS_MASTER, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {

                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                String status = response.getJSONObject(i).getString("keterangan");
                                listStatus.add(new SimpleObjectModel(response.getJSONObject(i).getString("kode"), status));
                                stringStatus.add(status);
                            }

                            spinner_adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {

                    }
                }));
    }

    private void loadGreeting(final boolean init){
        loadGreeting(init, "");
    }

    private void loadGreeting(final boolean init, String status){
        if(init){
            AppLoading.getInstance().showLoading(this);
            loadManager.initLoad();
        }

        JSONBuilder body = new JSONBuilder();
        body.add("id", "");
        body.add("artis", "");
        body.add("start", "");
        body.add("count", "");
        body.add("status", status);
        body.add("keyword", "");

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_GREETING_LIST,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        if(init){
                            listGreeting.clear();
                            adapter.notifyDataSetChanged();
                        }

                        loadManager.finishLoad(0);
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            if(init){
                                listGreeting.clear();
                            }

                            JSONArray response = new JSONArray(result);
                            for(int i = 0; i < response.length(); i++){
                                JSONObject greeting = response.getJSONObject(i);
                                listGreeting.add(new OrderGreetingModel(greeting.getString("id"),
                                        new UserModel(greeting.getString("id_user"),
                                                greeting.getString("pemesan"),
                                                greeting.getString("foto_pemesan")),
                                                greeting.getString("request"),
                                        greeting.getString("insert_at"),
                                        greeting.getString("status_order")));
                            }

                            loadManager.finishLoad(response.length());
                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(OrderGreetingActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());

                            loadManager.finishLoad(0);
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(OrderGreetingActivity.this, message, Toast.LENGTH_SHORT).show();

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
