package id.net.gmedia.selbiartis;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.leonardus.irfan.TopCropCircularImageView;

import java.util.List;

public class OrderGreetingAdapter extends RecyclerView.Adapter<OrderGreetingAdapter.OrderGreetingViewHolder> {

    private Activity activity;
    private List<OrderGreetingModel> listGreeting;

    public OrderGreetingAdapter(Activity activity, List<OrderGreetingModel> listGreeting){
        this.activity = activity;
        this.listGreeting = listGreeting;
    }

    @NonNull
    @Override
    public OrderGreetingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new OrderGreetingViewHolder(LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.item_order_greeting, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderGreetingViewHolder holder, int i) {
        OrderGreetingModel greeting = listGreeting.get(i);

        holder.txt_status.setText(greeting.getStatus());
        holder.txt_ucapan.setText(greeting.getUcapan());
        Glide.with(activity).load(greeting.getUser().getImage()).
                transition(DrawableTransitionOptions.withCrossFade()).into(holder.img_user);
        holder.txt_tanggal.setText(greeting.getTanggal());

        String judul = greeting.getUser().getNama() + " Request Video Greeting";
        holder.txt_judul.setText(judul);
    }

    @Override
    public int getItemCount() {
        return listGreeting.size();
    }

    class OrderGreetingViewHolder extends RecyclerView.ViewHolder{

        TextView txt_tanggal, txt_judul, txt_ucapan, txt_status;
        TopCropCircularImageView img_user;

        OrderGreetingViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_judul = itemView.findViewById(R.id.txt_judul);
            txt_status = itemView.findViewById(R.id.txt_status);
            txt_ucapan = itemView.findViewById(R.id.txt_ucapan);
            txt_tanggal = itemView.findViewById(R.id.txt_tanggal);
            img_user = itemView.findViewById(R.id.img_user);
        }
    }
}
