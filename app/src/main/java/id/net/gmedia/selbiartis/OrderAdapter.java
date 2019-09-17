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

    private final int action_penawaran = 198;
    private final int action_konfirmasi = 197;

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
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int i) {
        final OrderModel order = listOrder.get(i);
        final OrderViewHolder final_holder = holder;

        holder.txt_nama.setText(order.getNama());
        holder.txt_tanggal.setText(order.getTanggal());
        holder.txt_total.setText(Converter.doubleToRupiah(order.getJumlah() * order.getHarga_satuan()));
        holder.txt_jumlah.setText(String.valueOf(order.getJumlah()));
        holder.txt_catatan.setText(order.getCatatan());

        Glide.with(activity).load(order.getGambar()).transition(DrawableTransitionOptions.withCrossFade()).into(holder.img_order);
        holder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(activity, final_holder.layout_root, Gravity.END);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_order, popup.getMenu());
                if(order.getStatus() == OrderModel.STATUS_MENUNGGU_PERSETUJUAN){
                    popup.getMenu().add(1, action_penawaran, 2, "Penawaran Desain");
                }
                else if(order.getStatus() == OrderModel.STATUS_DISETUJUI){
                    popup.getMenu().add(2, action_konfirmasi, 3, "Konfirmasi Merchandise");
                }

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
                                return true;
                            }
                            case R.id.action_batal: {
                                ((OrderActivity) activity).batalOrder(order.getId_order());
                                return true;
                            }
                            case action_penawaran: {
                                Intent i = new Intent(activity, MerchandisePenawaranActivity.class);
                                i.putExtra(Constant.EXTRA_MERCHANDISE, gson.toJson(order));
                                activity.startActivity(i);
                                return true;
                            }
                            case action_konfirmasi:{
                                Intent i = new Intent(activity, MerchandiseKonfirmasiActivity.class);
                                i.putExtra(Constant.EXTRA_ID_ORDER, order.getId_order());
                                activity.startActivity(i);
                                return true;
                            }

                            default:return true;
                            }
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

        View layout_root;
        TextView txt_nama, txt_harga, txt_jumlah, txt_total, txt_tanggal, txt_catatan;
        ImageView img_order;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_nama = itemView.findViewById(R.id.txt_nama);
            txt_harga = itemView.findViewById(R.id.txt_harga);
            txt_jumlah = itemView.findViewById(R.id.txt_jumlah);
            txt_total = itemView.findViewById(R.id.txt_total);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            txt_catatan = itemView.findViewById(R.id.txt_catatan);
            img_order = itemView.findViewById(R.id.img_order);
            layout_root = itemView.findViewById(R.id.layout_root);
            //img_option = itemView.findViewById(R.id.img_option);
        }
    }
}
