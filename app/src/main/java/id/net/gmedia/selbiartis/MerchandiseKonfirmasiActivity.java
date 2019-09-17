package id.net.gmedia.selbiartis;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selbiartis.Upload.UploadAdapter;
import id.net.gmedia.selbiartis.Upload.UploadModel;

public class MerchandiseKonfirmasiActivity extends AppCompatActivity {

    private String id_order = "";

    private UploadAdapter adapter;
    private List<UploadModel> listUpload = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandise_konfirmasi);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Konfirmasi Merchandise");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if(getIntent().hasExtra(Constant.EXTRA_ID_ORDER)){
            id_order = getIntent().getStringExtra(Constant.EXTRA_ID_ORDER);
        }

        RecyclerView rv_foto = findViewById(R.id.rv_foto);
        rv_foto.setItemAnimator(new DefaultItemAnimator());
        rv_foto.setLayoutManager(new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false));
        adapter = new UploadAdapter(this, listUpload, Constant.URL_MERCHANDISE_KONFIRMASI_UPLOAD);
        rv_foto.setAdapter(adapter);

        /*btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pix.start(MerchandiseKonfirmasiActivity.this, UPLOAD_CODE, 1);
            }
        });*/

        findViewById(R.id.btn_konfirmasi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //upload dan konfirmasi merchandise
                if(adapter.isNoPic()){
                    Toast.makeText(MerchandiseKonfirmasiActivity.this,
                            "Upload minimal 1 gambar", Toast.LENGTH_SHORT).show();
                }
                else if(!adapter.isAllUploaded()){
                    Toast.makeText(MerchandiseKonfirmasiActivity.this,
                            "Tunggu semua gambar selesai terupload", Toast.LENGTH_SHORT).show();
                }
                else{
                    List<String> list_id = new ArrayList<>();
                    for(UploadModel u : listUpload){
                        list_id.add(u.getId());
                    }
                    kirimKonfirmasi(list_id);
                }
            }
        });
    }

    private void kirimKonfirmasi(List<String> list_id){
        JSONBuilder body = new JSONBuilder();
        body.add("gallery", new JSONArray(list_id));
        body.add("id_order", id_order);
        body.add("foto", adapter.getMainImage());

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_MERCHANDISE_KONFIRMASI_KIRIM,
                ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                        body.create(), new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                            @Override
                            public void onSuccess(String result) {
                                Toast.makeText(MerchandiseKonfirmasiActivity.this,
                                        "Merchandise berhasil dikonfirmasi", Toast.LENGTH_SHORT).show();

                                Intent i = new Intent(MerchandiseKonfirmasiActivity.this, MainActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            }

                            @Override
                            public void onFail(String message) {
                                Toast.makeText(MerchandiseKonfirmasiActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        }));
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == 999 && data.hasExtra(Pix.IMAGE_RESULTS)) {
            adapter.upload(data.getStringArrayListExtra(Pix.IMAGE_RESULTS));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
