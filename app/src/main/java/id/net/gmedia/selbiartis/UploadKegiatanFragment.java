package id.net.gmedia.selbiartis;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.DateTimeChooser;
import com.leonardus.irfan.JSONBuilder;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadKegiatanFragment extends Fragment {

    private Activity activity;

    //Variabel UI
    private EditText txt_judul, txt_tempat, txt_deskripsi;
    private TextView txt_tanggal;

    public UploadKegiatanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_kegiatan, container, false);
        activity = getActivity();

        //Inisialisasi UI
        txt_judul = v.findViewById(R.id.txt_judul);
        txt_tempat = v.findViewById(R.id.txt_tempat);
        txt_deskripsi = v.findViewById(R.id.txt_deskripsi);
        txt_tanggal = v.findViewById(R.id.txt_tanggal);

        txt_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pilihTanggal();
            }
        });

        txt_tempat.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    pilihTanggal();
                    return true;
                }
                return false;
            }
        });

        v.findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_judul.getText().toString().equals("")){
                    Toast.makeText(activity, "Isi judul kegiatan terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
                else{
                    upload();
                }
            }
        });

        return v;
    }

    private void pilihTanggal(){
        DateTimeChooser.getInstance().selectDate(activity, new DateTimeChooser.DateTimeListener() {
            @Override
            public void onFinished(String dateString) {
                txt_tanggal.setText(dateString);
                txt_deskripsi.requestFocus();
            }
        });
    }

    private void upload(){
        JSONBuilder body = new JSONBuilder();
        body.add("jenis", 1);
        body.add("judul", txt_judul.getText().toString());
        body.add("tgl", txt_tanggal.getText().toString());
        body.add("tempat", txt_tempat.getText().toString());
        body.add("deskripsi", txt_deskripsi.getText().toString());

        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_POST, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String response) {
                        Intent i = new Intent(activity, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

}
