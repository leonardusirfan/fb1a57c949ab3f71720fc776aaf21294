package id.net.gmedia.selbiartis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leonardus.irfan.ImageLoader;

public class AkunActivity extends AppCompatActivity {

    private UserModel artis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun);

        if(getIntent().hasExtra(Constant.EXTRA_USER)){
            Gson gson = new Gson();
            artis = gson.fromJson(getIntent().getStringExtra(Constant.EXTRA_USER), UserModel.class);
        }

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            ImageView img_user = findViewById(R.id.img_user);
            TextView txt_user = findViewById(R.id.txt_user);
            if(artis != null){
                ImageLoader.load(this, artis.getImage(), img_user);
                txt_user.setText(artis.getNama());
            }
        }

        findViewById(R.id.btn_preloved).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(AkunActivity.this, BarangActivity.class);
                i.putExtra(Constant.EXTRA_USER, gson.toJson(artis));
                i.putExtra(Constant.EXTRA_JENIS_BARANG, Constant.BARANG_PRELOVED);
                startActivity(i);
            }
        });

        findViewById(R.id.btn_merchandise).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(AkunActivity.this, BarangActivity.class);
                i.putExtra(Constant.EXTRA_USER, gson.toJson(artis));
                i.putExtra(Constant.EXTRA_JENIS_BARANG, Constant.BARANG_MERCHANDISE);
                startActivity(i);
            }
        });

        findViewById(R.id.btn_lelang).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(AkunActivity.this, BarangActivity.class);
                i.putExtra(Constant.EXTRA_USER, gson.toJson(artis));
                i.putExtra(Constant.EXTRA_JENIS_BARANG, Constant.BARANG_LELANG);
                startActivity(i);
            }
        });

        findViewById(R.id.btn_donasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Intent i = new Intent(AkunActivity.this, BarangActivity.class);
                i.putExtra(Constant.EXTRA_USER, gson.toJson(artis));
                i.putExtra(Constant.EXTRA_JENIS_BARANG, Constant.BARANG_DONASI);
                startActivity(i);
            }
        });

        findViewById(R.id.btn_profil).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AkunActivity.this, AkunProfilActivity.class));
            }
        });

        findViewById(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSharedPreferences.setLoggedIn(AkunActivity.this, true);
                Intent i = new Intent(AkunActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
