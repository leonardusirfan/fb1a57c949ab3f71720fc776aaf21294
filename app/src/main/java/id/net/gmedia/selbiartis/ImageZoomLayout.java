package id.net.gmedia.selbiartis;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.leonardus.irfan.DialogFactory;
import com.otaliastudios.zoom.ZoomLayout;

import java.util.ArrayList;
import java.util.List;

public class ImageZoomLayout {

    private Activity activity;
    private List<String> listImage = new ArrayList<>();

    private int imgHeight;
    private int imgWidth;
    private int selectedImage = 0;
    private Animation anim_popin, anim_popout;

    public ImageZoomLayout(Activity activity){
        this.activity = activity;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        imgWidth = displayMetrics.widthPixels - displayMetrics.widthPixels/7;
        imgHeight = displayMetrics.heightPixels - displayMetrics.heightPixels/5;

        //Inisialisasi animasi popup
        anim_popin = AnimationUtils.loadAnimation(activity, R.anim.anim_pop_in);
        anim_popout = AnimationUtils.loadAnimation(activity, R.anim.anim_pop_out);
    }

    public void setListImage(List<String> listImage){
        this.listImage = listImage;
    }

    public void show(int start_position){
        if(listImage.size() < 1){
            return;
        }

        final Dialog dialog = DialogFactory.getInstance().createDialog(activity, R.layout.popup_image_zoom,
                100, 100);
        final ImageView img_galeri_selected = dialog.findViewById(R.id.img_galeri_selected);
        ZoomLayout layout_zoom = dialog.findViewById(R.id.layout_zoom);
        /*layout_zoom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("MASUK");
                dialog.dismiss();
                return true;
            }
        });*/
        ConstraintLayout layout_overlay = dialog.findViewById(R.id.layout_overlay);
        Button btn_next, btn_previous;
        btn_next = dialog.findViewById(R.id.btn_next);
        btn_previous = dialog.findViewById(R.id.btn_previous);

        if(listImage.size() == 1){
            btn_next.setVisibility(View.INVISIBLE);
            btn_previous.setVisibility(View.INVISIBLE);
        }
        else{
            btn_next.setVisibility(View.VISIBLE);
            btn_previous.setVisibility(View.VISIBLE);
        }

        selectedImage = start_position;
        Glide.with(activity).load(listImage.get(start_position)).apply(new RequestOptions().
                override(imgWidth, imgHeight)).into(img_galeri_selected);

        //Next foto dalam galeri
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage < listImage.size() - 1){
                    selectedImage++;
                }
                else{
                    selectedImage = 0;
                }

                Glide.with(activity).load(listImage.get(selectedImage)).
                        apply(new RequestOptions().override(imgWidth, imgHeight)).into(img_galeri_selected);
            }
        });

        //Previous foto dalam galeri
        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedImage > 0){
                    selectedImage--;

                }
                else{
                    selectedImage = listImage.size() - 1;
                }

                Glide.with(activity).load(listImage.get(selectedImage)).
                        apply(new RequestOptions().override(imgWidth, imgHeight)).into(img_galeri_selected);
            }
        });

        /*anim_popout.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                detail=false;
                layout_overlay.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });*/

        layout_zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //layout_galeri_selected.startAnimation(anim_popout);
                //img_galeri_selected.startAnimation(anim_popout);
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        layout_overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
