package id.net.gmedia.selbiartis;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;
import com.leonardus.irfan.TopCropCircularImageView;

import java.util.List;

public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.NotifViewHolder> {

    private Activity activity;
    private List<NotifModel> listNotif;

    NotifAdapter(Activity activity, List<NotifModel> listNotif){
        this.activity = activity;
        this.listNotif = listNotif;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new NotifViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notifikasi, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder viewHolder, int i) {
        final NotifModel notif = listNotif.get(i);
        final NotifViewHolder final_holder = viewHolder;

        Glide.with(activity).load(notif.getImage()).transition(DrawableTransitionOptions.withCrossFade()).into(viewHolder.img_user);
        viewHolder.txt_notifikasi.setText(notif.getNotif());
        viewHolder.txt_tanggal.setText(notif.getTanggal());
        if(notif.isProses()){
            viewHolder.img_belum_diproses.setVisibility(View.INVISIBLE);
        }
        else{
            viewHolder.img_belum_diproses.setVisibility(View.VISIBLE);
        }

        viewHolder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notif.setProses(true);
                notifyItemChanged(final_holder.getAdapterPosition());

                updateNotif(notif.getId());
            }
        });
    }

    private void updateNotif(String id){
        JSONBuilder body = new JSONBuilder();
        body.add("id", id);

        ApiVolleyManager.getInstance().addRequest(activity, Constant.URL_NOTIF_READ, ApiVolleyManager.METHOD_POST,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
                    @Override
                    public void onSuccess(String result) {
                    }

                    @Override
                    public void onFail(String message) {
                        Log.e(Constant.TAG, message);
                    }
                }));

    }

    @Override
    public int getItemCount() {
        return listNotif.size();
    }

    class NotifViewHolder extends RecyclerView.ViewHolder{

        TopCropCircularImageView img_user, img_belum_diproses;
        TextView txt_notifikasi, txt_tanggal;
        ConstraintLayout layout_root;

        NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.img_user);
            img_belum_diproses = itemView.findViewById(R.id.img_belum_diproses);
            txt_notifikasi = itemView.findViewById(R.id.txt_notifikasi);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            layout_root = itemView.findViewById(R.id.layout_root);
        }
    }
}
