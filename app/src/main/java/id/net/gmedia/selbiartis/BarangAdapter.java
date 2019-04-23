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

    BarangAdapter(Activity activity, List<BarangModel> listBarang){
        this.activity = activity;
        this.listBarang = listBarang;
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
        String stok = "Stok : " + barang.getStok();
        holder.txt_stok.setText(stok);
    }

    @Override
    public int getItemCount() {
        return listBarang.size();
    }

    class BarangViewHolder extends RecyclerView.ViewHolder{

        ImageView img_barang;
        TextView txt_nama, txt_stok;

        BarangViewHolder(@NonNull View itemView) {
            super(itemView);
            img_barang = itemView.findViewById(R.id.img_barang);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_stok = itemView.findViewById(R.id.txt_stok);
        }
    }
}
