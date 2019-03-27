package id.net.gmedia.selbiartis;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.ImageSlider.ImageSlider;
import com.leonardus.irfan.ImageSlider.ImageSliderAdapter;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {

    private ImageSlider slider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
        }

        slider = findViewById(R.id.slider);

        findViewById(R.id.img_barang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int device_TotalWidth = metrics.widthPixels;
                int device_TotalHeight = metrics.heightPixels;

                Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.popup_barang_tambah);

                if(dialog.getWindow() != null){
                    dialog.getWindow().setLayout(device_TotalWidth, device_TotalHeight * 60 / 100); // set here your value
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.gravity = Gravity.BOTTOM;
                    lp.windowAnimations = R.style.DialogAnimation;
                    dialog.getWindow().setAttributes(lp);
                }

                dialog.findViewById(R.id.btn_preloved).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, UploadBarangActivity.class);
                        i.putExtra(Constant.EXTRA_JENIS_BARANG, Constant.BARANG_PRELOVED);
                        startActivity(i);
                    }
                });

                dialog.findViewById(R.id.btn_merchandise).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, UploadBarangActivity.class);
                        i.putExtra(Constant.EXTRA_JENIS_BARANG, Constant.BARANG_MERCHANDISE);
                        startActivity(i);
                    }
                });

                dialog.findViewById(R.id.btn_lelang).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, UploadBarangActivity.class);
                        i.putExtra(Constant.EXTRA_JENIS_BARANG, Constant.BARANG_LELANG);
                        startActivity(i);
                    }
                });

                dialog.findViewById(R.id.txt_pesan).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MainActivity.this, MerchandiseOrderActivity.class));
                    }
                });

                dialog.show();
            }
        });

        findViewById(R.id.img_riwayat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RiwayatActivity.class));
            }
        });

        findViewById(R.id.img_feed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UploadFeedActivity.class));
            }
        });

        findViewById(R.id.img_profil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfilActivity.class));
            }
        });

        findViewById(R.id.btn_order_merchandise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, MerchandiseOrderActivity.class));
            }
        });

        loadSlider();
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

    private void initSlider(List<String> images){
        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(MainActivity.this, images, true);
        CircleIndicator indicator = findViewById(R.id.main_indicator);
        slider.setIndicator(indicator);
        slider.setAdapter(sliderAdapter);
        slider.setAutoscroll(2500);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_notif:{
                startActivity(new Intent(MainActivity.this, NotifActivity.class));
                break;
            }
            case R.id.action_chat:{
                startActivity(new Intent(MainActivity.this, ChatActivity.class));
                break;
            }
            case R.id.action_order:{
                startActivity(new Intent(MainActivity.this, OrderActivity.class));
                break;
            }
            default:break;
        }
        return true;
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
}
