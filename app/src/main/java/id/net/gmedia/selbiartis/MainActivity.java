package id.net.gmedia.selbiartis;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.Converter;
import com.leonardus.irfan.ImageSlider.ImageSlider;
import com.leonardus.irfan.ImageSlider.ImageSliderAdapter;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.SimpleObjectModel;
import com.leonardus.irfan.TopCropCircularImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {

    private UserModel artis;

    //Variabel UI
    private ImageSlider slider;
    private TextView txt_special_offer;
    private RecyclerView rv_special_offer;
    private SpecialOfferAdapter adapter;

    private List<SimpleObjectModel> listOffer = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
        }

        slider = findViewById(R.id.slider);
        txt_special_offer = findViewById(R.id.txt_special_offer);
        rv_special_offer = findViewById(R.id.rv_special_offer);

        findViewById(R.id.btn_upload_barang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, UploadBarangActivity.class);
                i.putExtra(Constant.EXTRA_JENIS_BARANG, Constant.BARANG_PRELOVED);
                startActivity(i);
            }
        });

        findViewById(R.id.btn_upload_kegiatan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, UploadFeedActivity.class);
                i.putExtra(Constant.EXTRA_START_POSITION, 0);
                startActivity(i);
            }
        });

       findViewById(R.id.btn_riwayat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RiwayatActivity.class));
            }
        });

        findViewById(R.id.btn_order_merchandise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(artis != null){
                    Intent i = new Intent(MainActivity.this, MerchandiseOrderActivity.class);
                    i.putExtra(Constant.EXTRA_NAMA_USER, artis.getNama());
                    startActivity(i);
                }
            }
        });

        findViewById(R.id.layout_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(artis != null){
                    Gson gson = new Gson();
                    Intent i = new Intent(MainActivity.this, AkunActivity.class);
                    i.putExtra(Constant.EXTRA_USER, gson.toJson(artis));
                    startActivity(i);
                }

            }
        });

        loadProfil();
        loadSlider();
    }

    private void loadProfil(){
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PROFIL, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject response = new JSONObject(result);

                            //Inisialisasi UI terkait artis
                            artis = new UserModel(response.getString("id"),
                                    response.getString("profile_name"), response.getString("foto"));
                            Glide.with(MainActivity.this).load(artis.getImage()).
                                    thumbnail(0.3f).apply(new RequestOptions().dontTransform().
                                    dontAnimate().priority(Priority.IMMEDIATE).override(500, 300)).
                                    transition(DrawableTransitionOptions.withCrossFade()).into((TopCropCircularImageView)findViewById(R.id.img_artis));
                            ((TextView)findViewById(R.id.txt_artis)).setText(artis.getNama());

                            loadSpecialOffer();
                        }
                        catch (JSONException e){
                            Toast.makeText(MainActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadSlider(){
        //Inisialisasi slider
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_HOME_SLIDE, ApiVolleyManager.METHOD_GET,
                Constant.HEADER_AUTH, new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String response) {
                        try{
                            List<String> images = new ArrayList<>();
                            JSONArray arrayslider = new JSONArray(response);
                            for(int i = 0; i < arrayslider.length(); i++){
                                //Inisialisasi slider
                                images.add(arrayslider.getJSONObject(i).getString("image"));
                            }

                            initSlider(images);
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                            Toast.makeText(MainActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void loadSpecialOffer(){
        listOffer.add(new SimpleObjectModel("", Converter.getURLForResource("id.net.gmedia.selbiartis", R.drawable.offer1)));
        listOffer.add(new SimpleObjectModel("", Converter.getURLForResource("id.net.gmedia.selbiartis", R.drawable.offer2)));

        if(listOffer.size() > 0){
            String offer = "Special offer untuk " + artis.getNama();
            txt_special_offer.setText(offer);
            txt_special_offer.setVisibility(View.VISIBLE);

            rv_special_offer.setVisibility(View.VISIBLE);
            rv_special_offer.setItemAnimator(new DefaultItemAnimator());
            rv_special_offer.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
            adapter = new SpecialOfferAdapter(MainActivity.this, listOffer);
            rv_special_offer.setAdapter(adapter);
        }
    }

    private void initSlider(List<String> images){
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(MainActivity.this, images, true);
        CircleIndicator indicator = findViewById(R.id.main_indicator);
        slider.setIndicator(indicator);
        slider.setAdapter(sliderAdapter);
        slider.setAutoscroll(2500);
    }

    @Override
    protected void onResume() {
        JSONBuilder body = new JSONBuilder();
        body.add("fcm_id", AppSharedPreferences.getFcmId(this));

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_FCM_UPDATE, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {

                    }

                    @Override
                    public void onFail(String message) {

                    }
                }));

        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_chat:
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
                return true;
            case R.id.action_notif:
                startActivity(new Intent(MainActivity.this, NotifActivity.class));
                return true;
            case R.id.action_order:
                startActivity(new Intent(MainActivity.this, OrderActivity.class));
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }
}
