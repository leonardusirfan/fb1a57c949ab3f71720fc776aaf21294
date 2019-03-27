package id.net.gmedia.selbiartis;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.List;

public class MerchandisePenawaranAdapter extends RecyclerView.Adapter<MerchandisePenawaranAdapter.MerchandisePenawaranViewHolder> {

    private final int SATU_DESAIN = 10;
    private final int DUA_DESAIN = 20;
    private final int TIGA_DESAIN = 30;

    private Activity activity;
    private List<PenawaranModel> listDesain;

    MerchandisePenawaranAdapter(Activity activity, List<PenawaranModel> listDesain){
        this.activity = activity;
        this.listDesain = listDesain;
    }

    @Override
    public int getItemViewType(int position) {
        if(listDesain.get(position).getListDesain().size() == 1){
            return SATU_DESAIN;
        }
        else if(listDesain.get(position).getListDesain().size() == 2){
            return DUA_DESAIN;
        }
        else if(listDesain.get(position).getListDesain().size() >= 3){
            return TIGA_DESAIN;
        }
        else{
            return super.getItemViewType(position);
        }
    }

    @NonNull
    @Override
    public MerchandisePenawaranViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        switch (i){
            case SATU_DESAIN:
                return new MerchandisePenawaranSatu(LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.item_penawaran_satu, viewGroup, false));
            case DUA_DESAIN:
                return new MerchandisePenawaranDua(LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.item_penawaran_dua, viewGroup, false));
            case TIGA_DESAIN:
                return new MerchandisePenawaranTiga(LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.item_penawaran_tiga, viewGroup, false));
            default:
                return new MerchandisePenawaranViewHolder(LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.item_penawaran_satu, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MerchandisePenawaranViewHolder holder, int i) {
        final PenawaranModel p = listDesain.get(i);

        if(p.getStatus() == 1){
            holder.layout_approval.setVisibility(View.VISIBLE);
            holder.txt_status.setVisibility(View.GONE);
        }
        else{
            holder.layout_approval.setVisibility(View.GONE);
            holder.txt_status.setVisibility(View.VISIBLE);

            holder.txt_status.setText(p.getStatus_string());
        }

        holder.txt_penawaran.setText(p.getJudul());
        if (!p.getKeterangan().equals("")){
            holder.txt_keterangan.setVisibility(View.VISIBLE);
            holder.txt_keterangan.setText(p.getKeterangan());
        }
        holder.btn_terima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MerchandisePenawaranActivity)activity).responPenawaran(true, p.getId(), p.getTerima());
            }
        });
        holder.btn_tolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MerchandisePenawaranActivity)activity).responPenawaran(false, p.getId(), p.getTolak());
            }
        });

        if(holder instanceof MerchandisePenawaranSatu){
            Glide.with(activity).load(p.getListDesain().get(0)).transition(DrawableTransitionOptions.withCrossFade()).
                    into(((MerchandisePenawaranSatu)holder).img_penawaran);
            ((MerchandisePenawaranSatu)holder).img_penawaran.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MerchandisePenawaranActivity)activity).zoomImage(p.getListDesain(), 0);
                }
            });
        }
        else if(holder instanceof MerchandisePenawaranDua){
            Glide.with(activity).load(p.getListDesain().get(0)).transition(DrawableTransitionOptions.withCrossFade()).
                    into(((MerchandisePenawaranDua)holder).img_penawaran1);
            Glide.with(activity).load(p.getListDesain().get(1)).transition(DrawableTransitionOptions.withCrossFade()).
                    into(((MerchandisePenawaranDua)holder).img_penawaran2);
            ((MerchandisePenawaranDua)holder).img_penawaran1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MerchandisePenawaranActivity)activity).zoomImage(p.getListDesain(), 0);
                }
            });
            ((MerchandisePenawaranDua)holder).img_penawaran2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MerchandisePenawaranActivity)activity).zoomImage(p.getListDesain(), 1);
                }
            });
        }
        else if(holder instanceof MerchandisePenawaranTiga){
            Glide.with(activity).load(p.getListDesain().get(0)).transition(DrawableTransitionOptions.withCrossFade()).
                    into(((MerchandisePenawaranTiga)holder).img_penawaran1);
            Glide.with(activity).load(p.getListDesain().get(1)).transition(DrawableTransitionOptions.withCrossFade()).
                    into(((MerchandisePenawaranTiga)holder).img_penawaran2);
            Glide.with(activity).load(p.getListDesain().get(2)).transition(DrawableTransitionOptions.withCrossFade()).
                    into(((MerchandisePenawaranTiga)holder).img_penawaran3);
            ((MerchandisePenawaranTiga)holder).img_penawaran1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MerchandisePenawaranActivity)activity).zoomImage(p.getListDesain(), 0);
                }
            });
            ((MerchandisePenawaranTiga)holder).img_penawaran2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MerchandisePenawaranActivity)activity).zoomImage(p.getListDesain(), 1);
                }
            });
            ((MerchandisePenawaranTiga)holder).img_penawaran3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MerchandisePenawaranActivity)activity).zoomImage(p.getListDesain(), 2);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listDesain.size();
    }

    class MerchandisePenawaranViewHolder extends RecyclerView.ViewHolder{

        private TextView txt_penawaran, txt_keterangan, txt_status;
        private LinearLayout layout_approval;
        private Button btn_terima, btn_tolak;

        MerchandisePenawaranViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_penawaran = itemView.findViewById(R.id.txt_penawaran);
            txt_keterangan = itemView.findViewById(R.id.txt_keterangan);
            btn_terima = itemView.findViewById(R.id.btn_terima);
            btn_tolak = itemView.findViewById(R.id.btn_tolak);
            layout_approval = itemView.findViewById(R.id.layout_approval);
            txt_status = itemView.findViewById(R.id.txt_status);
        }
    }

    class MerchandisePenawaranSatu extends MerchandisePenawaranViewHolder{
        private ImageView img_penawaran;

        MerchandisePenawaranSatu(@NonNull View itemView) {
            super(itemView);
            img_penawaran = itemView.findViewById(R.id.img_penawaran);
        }
    }

    class MerchandisePenawaranDua extends MerchandisePenawaranViewHolder{
        private ImageView img_penawaran1;
        private ImageView img_penawaran2;

        MerchandisePenawaranDua(@NonNull View itemView) {
            super(itemView);
            img_penawaran1 = itemView.findViewById(R.id.img_penawaran1);
            img_penawaran2 = itemView.findViewById(R.id.img_penawaran2);
        }
    }

    class MerchandisePenawaranTiga extends MerchandisePenawaranViewHolder{
        private ImageView img_penawaran1;
        private ImageView img_penawaran2;
        private ImageView img_penawaran3;

        MerchandisePenawaranTiga(@NonNull View itemView) {
            super(itemView);
            img_penawaran1 = itemView.findViewById(R.id.img_penawaran1);
            img_penawaran2 = itemView.findViewById(R.id.img_penawaran2);
            img_penawaran3 = itemView.findViewById(R.id.img_penawaran3);
        }
    }
}
