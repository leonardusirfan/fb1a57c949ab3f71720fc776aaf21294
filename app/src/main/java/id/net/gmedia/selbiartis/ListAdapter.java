package id.net.gmedia.selbiartis;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leonardus.irfan.SimpleSelectableObjectModel;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListViewHolder> {

    private List<SimpleSelectableObjectModel> listObject;

    ListAdapter(List<SimpleSelectableObjectModel> listObject){
        this.listObject = listObject;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ListViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ListViewHolder holder, int i) {
        final SimpleSelectableObjectModel obj = listObject.get(i);

        holder.txt_list.setText(obj.getValue());

        if(obj.isSelected()){
            holder.cb_list.setChecked(true);
        }
        else{
            holder.cb_list.setChecked(false);
        }

        holder.cb_list.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    obj.setSelected(true);
                }
                else{
                    obj.setSelected(false);
                }
            }
        });

        holder.layout_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cb_list.performClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listObject.size();
    }

    class ListViewHolder extends RecyclerView.ViewHolder{

        LinearLayout layout_root;
        TextView txt_list;
        CheckBox cb_list;

        ListViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_root = itemView.findViewById(R.id.layout_root);
            txt_list = itemView.findViewById(R.id.txt_list);
            cb_list = itemView.findViewById(R.id.cb_list);
        }
    }
}
