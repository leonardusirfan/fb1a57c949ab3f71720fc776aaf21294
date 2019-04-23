package id.net.gmedia.selbiartis;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class AkunProfilActivity extends AppCompatActivity {

    //Variabel UI
    TextView txt_nama, txt_alamat, txt_telepon, txt_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun_profil);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        if(getSupportActionBar() != null){
            findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            TextView txt_title = findViewById(R.id.txt_title);
            txt_title.setText("Info Pribadi");
        }

        //Inisialisasi variabel UI
        txt_nama = findViewById(R.id.txt_nama);
        txt_alamat = findViewById(R.id.txt_alamat);
        txt_telepon = findViewById(R.id.txt_telepon);
        txt_email = findViewById(R.id.txt_email);

        initProfil();
    }

    private void initProfil(){
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PROFIL,
                ApiVolleyManager.METHOD_GET, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String response) {
                        try{
                            JSONObject akun = new JSONObject(response);

                           String temp = akun.getString("profile_name");
                           if(!temp.equals("")){
                               txt_nama.setText(temp);
                               txt_nama.setVisibility(View.VISIBLE);
                           }
                            temp = akun.getString("alamat");
                            if(!temp.equals("")){
                                txt_alamat.setText(temp);
                                txt_alamat.setVisibility(View.VISIBLE);
                            }
                            temp = akun.getString("no_telp");
                            if(!temp.equals("")){
                                txt_telepon.setText(temp);
                                txt_telepon.setVisibility(View.VISIBLE);
                            }
                            temp = akun.getString("email");
                            if(!temp.equals("")){
                                txt_email.setText(temp);
                                txt_email.setVisibility(View.VISIBLE);
                            }
                        }
                        catch (JSONException e){
                            Log.e(Constant.TAG, e.getMessage());
                            Toast.makeText(AkunProfilActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(AkunProfilActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }
}
