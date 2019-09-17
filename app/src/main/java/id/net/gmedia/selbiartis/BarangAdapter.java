package id.net.gmedia.selbiartis;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leonardus.irfan.ImageLoader;

import java.util.List;

public class BarangAdapter extends RecyclerView.Adapter<BarangAdapter.BarangViewHolder> {

    private Activity activity;
    private List<BarangModel> listBarang;

    private boolean lelang = false;

    BarangAdapter(Activity activity, List<BarangModel> listBarang, boolean lelang){
        this.activity = activity;
        this.listBarang = listBarang;
        this.lelang = lelang;
    }

    @NonNull
    @Override
    public BarangViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BarangViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_barang, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BarangViewHolder holder, int i) {
        BarangModel barang = listBarang.get(i);

        ImageLoader.load(activity, barang.getUrl(), holder.img_barang);
        holder.txt_nama.setText(barang.getNama());

        if(lelang){
            holder.txt_stok.setVisibility(View.GONE);
            holder.txt_terjual.setVisibility(View.GONE);
        }
        else{
            holder.txt_stok.setVisibility(View.VISIBLE);
            holder.txt_terjual.setVisibility(View.VISIBLE);

            String stok = "Stok : " + barang.getStok();
            holder.txt_stok.setText(stok);
            String terjual = "Terjual : " + barang.getTerjual();
            holder.txt_terjual.setText(terjual);
        }
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class BarangViewHolder extends RecyclerView.ViewHolder{

        ImageView img_barang;
        TextView txt_nama, txt_stok, txt_terjual;

        BarangViewHolder(@NonNull View itemView) {
            super(itemView);
            img_barang = itemView.findViewById(R.id.img_barang);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_stok = itemView.findViewById(R.id.txt_stok);
            txt_terjual = itemView.findViewById(R.id.txt_terjual);
        }
    }
}
