package com.leonardus.irfan.ImageSlider;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.leonardus.irfan.ClickableViewPager;
import com.leonardus.irfan.R;

import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

public class ImageSlider extends RelativeLayout{

    private long slide_duration = 3000;
    private Runnable Update;
    private Timer autoscroll;
    private boolean paused = false;
    private CircleIndicator customIndicator;

    View root_layout;
    ImageSliderViewPager viewpager;
    CircleIndicator indicator;

    public ImageSlider(Context context) {
        super(context);
        init(context);
    }

    public ImageSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        root_layout = inflate(context, R.layout.layout_image_slider, this);
        viewpager = root_layout.findViewById(R.id.viewpager);
        indicator = root_layout.findViewById(R.id.indicator);

        viewpager.setCustomTouchListener(new ClickableViewPager.CustomTouchListener() {
            @Override
            public void onPress() {
                pauseAutoscroll();
            }

            @Override
            public void onRelease() {
                resumeAutoscroll();
            }
        });
    }

    public void setIndicator(CircleIndicator newIndicator){
        customIndicator = newIndicator;
        customIndicator.setViewPager(viewpager);
        if(viewpager.getAdapter() != null){
            indicator.setVisibility(INVISIBLE);
        }
    }

    public void setAdapter(ImageSliderAdapter adapter){
        viewpager.setAdapter(adapter);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(viewpager.getAdapter()!=null){
                    ((ImageSliderAdapter) viewpager.getAdapter()).setPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        adapter.setSlider(viewpager);

        if(customIndicator == null){
            indicator.setViewPager(viewpager);
        }
        else{
            indicator.setVisibility(INVISIBLE);
            customIndicator.setViewPager(viewpager);
        }
    }

    public void setAutoscroll(long duration){
        slide_duration = duration;
        setAutoscroll();
    }

    public void setAutoscroll(){
        Update = new Runnable() {
            @Override
            public void run() {
                if(viewpager.getAdapter()!=null){
                    if(!paused){
                        ((ImageSliderAdapter) viewpager.getAdapter()).slide();
                    }
                }
            }
        };

        final Handler handler = new Handler();

        autoscroll = new Timer();
        autoscroll.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, slide_duration, slide_duration);
    }

    public void stopAutoscroll(){
        if(autoscroll != null){
            autoscroll.cancel();
            autoscroll = null;
        }
    }

    public void pauseAutoscroll(){
        paused = true;
    }

    public void resumeAutoscroll(){
        paused = false;
    }


}
