package id.net.gmedia.selbiartis;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.leonardus.irfan.ImageLoader;
import com.leonardus.irfan.SimpleObjectModel;

import java.util.List;

public class SpecialOfferAdapter extends RecyclerView.Adapter <SpecialOfferAdapter.SpecialOfferViewHolder>{

    private Activity activity;
    private List<SimpleObjectModel> listOffer;

    public SpecialOfferAdapter(Activity activity, List<SimpleObjectModel> listOffer){
        this.activity = activity;
        this.listOffer = listOffer;
    }

    @NonNull
    @Override
    public SpecialOfferViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(activity instanceof MerchandiseOrderActivity){
            return new SpecialOfferViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_special_offer_merchandise_act, viewGroup, false));
        }
        else{
            return new SpecialOfferViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_special_offer, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialOfferViewHolder holder, int i) {
        SimpleObjectModel o = listOffer.get(i);

        ImageLoader.load(activity, o.getValue(), holder.img_offer);
    }

    @Override
    public int getItemCount() {
        return listOffer.size();
    }

    class SpecialOfferViewHolder extends RecyclerView.ViewHolder{

        View layout_root;
       ImageView img_offer;

        SpecialOfferViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            img_offer = itemView.findViewById(R.id.img_offer);
        }
    }
}
