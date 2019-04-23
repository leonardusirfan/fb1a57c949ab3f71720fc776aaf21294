package id.net.gmedia.selbiartis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MerchandiseKonfirmasiActivity extends AppCompatActivity {

    private ImageView btn_upload;
    private ImageView img_merchandise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandise_konfirmasi);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Konfirmasi Merchandise");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btn_upload = findViewById(R.id.btn_upload);
        img_merchandise = findViewById(R.id.img_merchandise);

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.btn_konfirmasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upload dan konfirmasi merchandise
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
