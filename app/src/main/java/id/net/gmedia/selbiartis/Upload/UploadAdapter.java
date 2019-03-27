package id.net.gmedia.selbiartis.Upload;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fxn.pix.Pix;
import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.Converter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selbiartis.Constant;
import id.net.gmedia.selbiartis.R;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.UploadViewHolder> {
    public static final int UPLOAD_CODE = 999;
    private FragmentActivity activity;
    private List<UploadModel> listUpload;

    private String url;

    public UploadAdapter(FragmentActivity activity, List<UploadModel> listUpload, String url){
        this.activity = activity;
        this.listUpload = listUpload;
        this.url = url;
    }

    @NonNull
    @Override
    public UploadViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new UploadViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_upload, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final UploadViewHolder uploadViewHolder, int i) {
        if(i == 0){
            uploadViewHolder.img_overlay.setAlpha(0f);
            uploadViewHolder.bar_loading.setVisibility(View.INVISIBLE);
            uploadViewHolder.img_upload.setImageResource(R.drawable.custom_kamera);
            uploadViewHolder.img_hapus.setVisibility(View.INVISIBLE);

            uploadViewHolder.img_upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pix.start(activity, UPLOAD_CODE, 5);
                }
            });
        }
        else{
            uploadViewHolder.img_upload.setImageBitmap(listUpload.get(i - 1).getBitmap());
            if(listUpload.get(i - 1).isUploaded()){
                uploadViewHolder.bar_loading.setVisibility(View.INVISIBLE);
                uploadViewHolder.img_overlay.setAlpha(0f);
            }

            uploadViewHolder.img_hapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listUpload.remove(uploadViewHolder.getAdapterPosition() - 1);
                    notifyItemRemoved(uploadViewHolder.getAdapterPosition());
                }
            });
        }
    }

    public void upload(ArrayList<String> listPath){
        for(int i = 0; i < listPath.size(); i++){
            try{
                //preprocess bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap
                        (activity.getContentResolver(), Uri.fromFile(new File(listPath.get(i))));

                //Update recycler view
                UploadModel u = new UploadModel(bitmap);
                listUpload.add(u);
                notifyDataSetChanged();

                //proses upload
                uploadBitmap(u);
            }
            catch (IOException e){
                Toast.makeText(activity, "File tidak ditemukan", Toast.LENGTH_SHORT).show();
                Log.e(Constant.TAG, e.toString());
            }
        }
    }

    private void uploadBitmap(final UploadModel upload){
        ApiVolleyManager.getInstance().addMultipartRequest(activity, url,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()),
                Converter.getFileDataFromDrawable(upload.getBitmap()), new ApiVolleyManager.RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        try{
                            String id = new JSONObject(result).getJSONObject("response").getString("id");

                            //update status upload
                            upload.setUploaded(true);
                            upload.setId(id);

                            //update recycler view
                            notifyDataSetChanged();
                        }
                        catch (JSONException e){
                            Toast.makeText(activity, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.toString());
                        }
                    }

                    @Override
                    public void onError(String result) {
                        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public boolean isAllUploaded(){
        for(UploadModel u : listUpload){
            if(!u.isUploaded()){
                return false;
            }
        }
        return true;
    }

    public boolean isNoPic(){
        return listUpload.size() == 0;
    }

    public String getMainImage(){
        if(listUpload.size() == 0){
            return "";
        }
        else{
            return listUpload.get(0).getId();
        }
    }

    public List<String> getListImage(){
        List<String> listImage = new ArrayList<>();
        for(UploadModel u : listUpload){
            listImage.add(u.getId());
        }
        return listImage;
    }

    @Override
    public int getItemCount() {
        return listUpload.size() + 1;
    }

    class UploadViewHolder extends RecyclerView.ViewHolder{

        ImageView img_upload, img_overlay;
        ImageView img_hapus;
        ProgressBar bar_loading;

        UploadViewHolder(@NonNull View itemView) {
            super(itemView);
            img_upload = itemView.findViewById(R.id.img_upload);
            img_overlay = itemView.findViewById(R.id.img_overlay);
            img_hapus = itemView.findViewById(R.id.img_hapus);
            bar_loading = itemView.findViewById(R.id.bar_loading);
        }
    }
}
