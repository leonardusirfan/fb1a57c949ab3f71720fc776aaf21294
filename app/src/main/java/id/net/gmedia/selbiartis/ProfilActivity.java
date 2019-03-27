package id.net.gmedia.selbiartis;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.LockableScrollView;
import com.leonardus.irfan.TopCropImageView;

import org.json.JSONException;
import org.json.JSONObject;

public class ProfilActivity extends AppCompatActivity {

    //flag apakah info detail artis sedang tampil sebagai popup atau tidak
    private boolean detail = false;

    //variabel yang menampung informasi artis yang sedang ditampilkan
    //private ArtisModel artis;

    //Variabel UI
    private Button btn_detail;
    private CardView layout_button;
    private FrameLayout layout_detail_artis;
    private TextView txt_detail, txt_artis;
    private TopCropImageView img_artis;
    private LockableScrollView scrollView;
    private ImageView img_gradient;

    //Variabel gesture UI
    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        //Inisialisasi UI
        img_artis = findViewById(R.id.img_artis);
        layout_detail_artis = findViewById(R.id.layout_detail_artis);
        layout_button = findViewById(R.id.layout_button);
        txt_artis = findViewById(R.id.txt_artis);
        txt_detail = findViewById(R.id.txt_detail);
        btn_detail = findViewById(R.id.btn_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        scrollView = findViewById(R.id.scroll);
        img_gradient = findViewById(R.id.img_gradient);

        //Inisialisasi Toolbar
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //Animasi + Transisi dari activity sebelumnya
        Transition enterTransition = getWindow().getSharedElementEnterTransition();
        enterTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                layout_detail_artis.setVisibility(View.VISIBLE);
                layout_button.setVisibility(View.VISIBLE);

                final Animation enterAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.anim_move_inscreen_up);

                layout_button.startAnimation(enterAnimation);
                layout_detail_artis.startAnimation(enterAnimation);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
            }

            @Override
            public void onTransitionCancel(Transition transition) {}

            @Override
            public void onTransitionPause(Transition transition) {}

            @Override
            public void onTransitionResume(Transition transition) {}
        });

        //Inisialisasi Informasi Artis
        initArtis();

        //Apabila button detail diklik akan memunculkan transisi layout menuju detail artis
        btn_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConstraintLayout constraintLayout = findViewById(R.id.layout_root);
                ConstraintSet constraintSet = new ConstraintSet();

                Animation rotate;
                if(!detail){
                    constraintSet.clone(ProfilActivity.this, R.layout.activity_profil_detail);
                    rotate = AnimationUtils.loadAnimation(ProfilActivity.this, R.anim.anim_rotate);
                    scrollView.setScrollingEnabled(true);
                    img_gradient.setVisibility(View.INVISIBLE);
                }
                else{
                    constraintSet.clone(ProfilActivity.this, R.layout.activity_profil);
                    rotate = AnimationUtils.loadAnimation(ProfilActivity.this, R.anim.anim_rotate_reverse);
                    scrollView.setScrollingEnabled(false);
                    img_gradient.setVisibility(View.VISIBLE);
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }

                detail = !detail;
                TransitionManager.beginDelayedTransition(constraintLayout);
                btn_detail.startAnimation(rotate);
                constraintSet.applyTo(constraintLayout);
            }
        });

        //gesture agar detail artis bisa dibuka dengan fling
        mDetector = new GestureDetector(this, new MyGestureListener());
        layout_detail_artis.setOnTouchListener(touchListener);
        scrollView.setScrollingEnabled(false);
        img_gradient.setVisibility(View.VISIBLE);
    }

    private void initArtis(){
        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_PROFIL, ApiVolleyManager.METHOD_GET,
                Constant.getTokenHeader(FirebaseAuth.getInstance().getUid()), new AppRequestCallback(new AppRequestCallback.RequestListener() {
                    @Override
                    public void onEmpty(String message) {
                        Toast.makeText(ProfilActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(String result) {
                        try{
                            JSONObject artis = new JSONObject(result);

                            //Inisialisasi UI terkait artis
                            Glide.with(ProfilActivity.this).load(artis.getString("foto")).
                                    thumbnail(0.3f).apply(new RequestOptions().dontTransform().
                                    dontAnimate().priority(Priority.IMMEDIATE).override(500, 300)).
                                    transition(DrawableTransitionOptions.withCrossFade()).into(img_artis);

                            txt_artis.setText(artis.getString("profile_name"));
                            txt_detail.setText(artis.getString("deskripsi"));
                        }
                        catch (JSONException e){
                            Toast.makeText(ProfilActivity.this, R.string.error_json, Toast.LENGTH_SHORT).show();
                            Log.e(Constant.TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onFail(String message) {
                        Toast.makeText(ProfilActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public void onBackPressed() {
        //Jika ditekan back ketika memunculkan detail, akan menutup detail terlebih dulu
        if(!detail){
            super.onBackPressed();
        }
        else{
            //Animasi/transisi dari detail artis ke layout normal
            ConstraintLayout constraintLayout = findViewById(R.id.layout_root);
            ConstraintSet constraintSet = new ConstraintSet();

            constraintSet.clone(ProfilActivity.this, R.layout.activity_profil);

            detail = !detail;
            TransitionManager.beginDelayedTransition(constraintLayout);
            btn_detail.startAnimation(AnimationUtils.loadAnimation(ProfilActivity.this, R.anim.anim_rotate_reverse));
            constraintSet.applyTo(constraintLayout);
            scrollView.setScrollingEnabled(false);
            img_gradient.setVisibility(View.VISIBLE);
            scrollView.fullScroll(ScrollView.FOCUS_UP);
        }
    }

    //Inisialisasi Gesture
    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // pass the events to the gesture detector
            // a return value of true means the detector is handling it
            // a return value of false means the detector didn't
            // recognize the event
            v.performClick();
            return mDetector.onTouchEvent(event);
        }
    };

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent event) {
            // don't return false here or else none of the other
            // gestures will work
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            return true;
        }
        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            if(!detail){
                if(event1.getY() - event2.getY() > 200){
                    ConstraintLayout constraintLayout = findViewById(R.id.layout_root);
                    ConstraintSet constraintSet = new ConstraintSet();

                    Animation rotate;
                    constraintSet.clone(ProfilActivity.this, R.layout.activity_profil_detail);
                    rotate = AnimationUtils.loadAnimation(ProfilActivity.this, R.anim.anim_rotate);

                    detail = !detail;
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    btn_detail.startAnimation(rotate);
                    constraintSet.applyTo(constraintLayout);
                    scrollView.setScrollingEnabled(true);
                    img_gradient.setVisibility(View.INVISIBLE);
                }
            }
            else{
                if(event1.getY() - event2.getY() < 200){
                    ConstraintLayout constraintLayout = findViewById(R.id.layout_root);
                    ConstraintSet constraintSet = new ConstraintSet();

                    Animation rotate;
                    constraintSet.clone(ProfilActivity.this, R.layout.activity_profil);
                    rotate = AnimationUtils.loadAnimation(ProfilActivity.this, R.anim.anim_rotate_reverse);

                    detail = !detail;
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    btn_detail.startAnimation(rotate);
                    constraintSet.applyTo(constraintLayout);
                    scrollView.setScrollingEnabled(false);
                    img_gradient.setVisibility(View.VISIBLE);
                    scrollView.fullScroll(ScrollView.FOCUS_UP);
                }
            }

            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Log Out");
                builder.setMessage("Yakin ingin keluar Selbi?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Log out pengguna
                        FirebaseAuth.getInstance().signOut();
                        AppSharedPreferences.setLoggedIn(ProfilActivity.this, false);
                        Intent i = new Intent(ProfilActivity.this, LoginActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }
}
