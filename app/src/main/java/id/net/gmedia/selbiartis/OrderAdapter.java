package id.net.gmedia.selbiartis;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.gson.Gson;
import com.leonardus.irfan.Converter;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Activity activity;
    private List<OrderModel> listOrder;

    OrderAdapter(Activity activity, List<OrderModel> listOrder){
        this.activity = activity;
        this.listOrder = listOrder;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new OrderViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_order, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, int i) {
        final OrderModel order = listOrder.get(i);

        holder.txt_nama.setText(order.getNama());
        holder.txt_tanggal.setText(order.getTanggal());
        holder.txt_total.setText(Converter.doubleToRupiah(order.getJumlah() * order.getHarga_satuan()));
        holder.txt_jumlah.setText(String.valueOf(order.getJumlah()));
        holder.txt_catatan.setText(order.getCatatan());

        Glide.with(activity).load(order.getGambar()).transition(DrawableTransitionOptions.withCrossFade()).into(holder.img_order);
        holder.img_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(activity, holder.img_option, Gravity.END);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_order, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        Gson gson = new Gson();
                        switch (item.getItemId()){
                            case R.id.action_edit:{
                                Intent i = new Intent(activity, MerchandiseOrderDetailActivity.class);
                                i.putExtra(Constant.EXTRA_ID_MERCHANDISE, order.getId_merchandise());
                                i.putExtra(Constant.EXTRA_ID_ORDER, order.getId_order());
                                activity.startActivity(i);
                                break;
                            }
                            case R.id.action_batal:
                                ((OrderActivity)activity).batalOrder(order.getId_order());
                                break;
                            case R.id.action_penawaran:{
                                Intent i = new Intent(activity, MerchandisePenawaranActivity.class);
                                i.putExtra(Constant.EXTRA_MERCHANDISE, gson.toJson(order));
                                activity.startActivity(i);
                                break;
                            }
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOrder.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder{

        TextView txt_nama, txt_harga, txt_jumlah, txt_total, txt_tanggal, txt_catatan;
        ImageView img_order, img_option;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_total = itemView.findViewById(R.id.txt_total);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_catatan = itemView.findViewById(R.id.txt_catatan);
            img_order = itemView.findViewById(R.id.img_order);
            img_option = itemView.findViewById(R.id.img_option);
        }
    }
}
