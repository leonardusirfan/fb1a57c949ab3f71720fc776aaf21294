package id.net.gmedia.selbiartis;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selbiartis.Upload.UploadAdapter;
import id.net.gmedia.selbiartis.Upload.UploadModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadGaleriFragment extends Fragment {

    private Activity activity;
    public UploadAdapter adapter;
    private List<UploadModel> listUpload = new ArrayList<>();

    private TextView txt_deskripsi;

    public UploadGaleriFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_galeri, container, false);
        activity = getActivity();

        txt_deskripsi = v.findViewById(R.id.txt_deskripsi);
        RecyclerView rv_upload = v.findViewById(R.id.rv_upload);
        rv_upload.setItemAnimator(new DefaultItemAnimator());
        rv_upload.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
        adapter = new UploadAdapter((FragmentActivity) activity, listUpload, Constant.URL_UPLOAD_GAMBAR);
        rv_upload.setAdapter(adapter);

        v.findViewById(R.id.btn_upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.isNoPic() && txt_deskripsi.getText().toString().equals("")){
                    Toast.makeText(activity, "Tidak bisa menggunggah posting kosong", Toast.LENGTH_SHORT).show();
                }
                else{
                    upload();
                }
            }
        });
        
        return v;
    }

    public void upload(){
        if(adapter.isNoPic()){
            String judul = txt_deskripsi.getText().toString();

            JSONBuilder body = new JSONBuilder();
            body.add("jenis", 3);
            body.add("judul", judul);
            body.add("id_gambar", new JSONArray());
            ApiVolleyManager.getInstance().addRequest(activity,
                    Constant.URL_POST, ApiVolleyManager.METHOD_POST, Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                    body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                        @Override
                        public void onEmpty(String message) {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(String result) {
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
        else if(adapter.isAllUploaded()){
            //inisialisasi gambar yang di upload
            JSONArray listGambar = new JSONArray();
            listGambar.put(adapter.getMainImage());
            for(String url : adapter.getListImage()){
                listGambar.put(url);
            }

            String judul = txt_deskripsi.getText().toString();

            JSONBuilder body = new JSONBuilder();
            body.add("jenis", 2);
            body.add("judul", judul);
            body.add("id_gambar", listGambar);

            ApiVolleyManager.getInstance().addRequest(activity,
                    Constant.URL_POST, ApiVolleyManager.METHOD_POST,
                    Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                    body.create(), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                        @Override
                        public void onEmpty(String message) {
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(String result) {
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
        else{
            Toast.makeText(activity, "Belum semua gambar ter-upload", Toast.LENGTH_SHORT).show();
        }
    }

}
