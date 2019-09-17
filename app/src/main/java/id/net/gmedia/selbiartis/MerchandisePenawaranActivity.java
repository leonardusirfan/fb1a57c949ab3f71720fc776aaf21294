package id.net.gmedia.selbiartis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppLoading;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MerchandisePenawaranActivity extends AppCompatActivity {

    private TextView txt_kosong;
    private RecyclerView rv_penawaran;

    private String id_setuju;
    private String id_tolak;

    private OrderModel order;
    private List<PenawaranModel> listPenawaran = new ArrayList<>();
    private MerchandisePenawaranAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandise_penawaran);

        //Inisialisasi Toolbar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Penawaran Desain");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(Constant.EXTRA_MERCHANDISE)){
            Gson gson = new Gson();
            order = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_MERCHANDISE), OrderModel.class);
        }

        txt_kosong = findViewById(R.id.txt_kosong);
        rv_penawaran = findViewById(R.id.rv_penawaran);
        rv_penawaran.setLayoutManager(new LinearLayoutManager(this));
        rv_penawaran.setItemAnimator(new DefaultItemAnimator());
        adapter = new MerchandisePenawaranAdapter(this, listPenawaran);
        rv_penawaran.setAdapter(adapter);

        findViewById(R.id.btn_setuju).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list_id = new ArrayList<>();
                for(PenawaranModel p : listPenawaran){
                    if(p.isSelected()){
                        list_id.add(p.getId());
                    }
                }

                if(list_id.size() > 0){
                    responPenawaran(list_id, id_setuju);
                }
                else{
                    Toast.makeText(MerchandisePenawaranActivity.this,
                            "Pilih salah satu desain untuk disetujui", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_tolak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> list_id = new ArrayList<>();
                for(PenawaranModel p : listPenawaran){
                    if(p.isSelected()){
                        list_id.add(p.getId());
                    }
                }

                if(list_id.size() > 0){
                    responPenawaran(list_id, id_tolak);
                }
                else{
                    Toast.makeText(MerchandisePenawaranActivity.this,
                            "Pilih salah satu desain untuk ditolak", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadPenawaran();
    }

    private void loadPenawaran(){
        AppLoading.getInstance().showLoading(this);
        JSONBuilder body = new JSONBuilder();
        body.add("id_order", order.getId_order());
        body.add("id_merchandise", order.getId_merchandise());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MERCHANDISE_PENAWARAN, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        txt_kosong.setVisibility(View.VISIBLE);
                        rv_penawaran.setVisibility(View.INVISIBLE);

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            rv_penawaran.setVisibility(View.VISIBLE);
                            txt_kosong.setVisibility(View.INVISIBLE);

                            JSONArray response = new JSONObject(result).getJSONArray("data");
                            id_setuju = new JSONObject(result).getJSONObject("button").getString("setuju");
                            id_tolak = new JSONObject(result).getJSONObject("button").getString("tolak");

                            for(int i = 0; i < response.length(); i++){
                                PenawaranModel penawaran = new PenawaranModel(response.getJSONObject(i).getString("id_penawaran"),
                                        response.getJSONObject(i).getString("title"),
                                        response.getJSONObject(i).getInt("status"),
                                        response.getJSONObject(i).getString("status_tawar"),
                                        response.getJSONObject(i).getString("keterangan"));

                                JSONArray images = response.getJSONObject(i).getJSONArray("images");
                                for(int j = 0; j < images.length(); j++){
                                    penawaran.addDesain(images.getJSONObject(j).getString("path") +
                                            images.getJSONObject(j).getString("image"));
                                }
                                listPenawaran.add(penawaran);
                            }

                            adapter.notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(MerchandisePenawaranActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }

                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MerchandisePenawaranActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    public void responPenawaran( List<String> id_penawaran, String status){
        AppLoading.getInstance().showLoading(this);

        JSONBuilder body = new JSONBuilder();
        body.add("id_penawaran", new JSONArray(id_penawaran));
        body.add("status", status);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MERCHANDISE_APPROVAL, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                        /*if(riwayat){
                            Intent resultIntent = new Intent(MerchandisePenawaranActivity.this, RiwayatActivity.class);
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(MerchandisePenawaranActivity.this);
                            stackBuilder.addNextIntentWithParentStack(resultIntent);
                            stackBuilder.startActivities();
                        }
                        else{
                            onBackPressed();
                        }*/

                        onBackPressed();
                        AppLoading.getInstance().stopLoading();
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MerchandisePenawaranActivity.this, message, Toast.LENGTH_SHORT).show();
                        AppLoading.getInstance().stopLoading();
                    }
                }));
    }

    public void zoomImage(List<String> listImage, int start_position){
        ImageZoomLayout im = new ImageZoomLayout(this);
        im.setListImage(listImage);
        im.show(start_position);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
