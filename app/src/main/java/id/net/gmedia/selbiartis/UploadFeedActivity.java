package id.net.gmedia.selbiartis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.fxn.pix.Pix;

import java.util.ArrayList;
import java.util.List;

import id.net.gmedia.selbiartis.Upload.UploadAdapter;

public class UploadFeedActivity extends AppCompatActivity {

    private List<Fragment> listFragment = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_feed);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Upload Beranda");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TabLayout tabLayout = findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Kegiatan"));
        listFragment.add(new UploadKegiatanFragment());
        tabLayout.addTab(tabLayout.newTab().setText("Galeri"));
        listFragment.add(new UploadGaleriFragment());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadFragment(listFragment.get(tab.getPosition()));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        loadFragment(listFragment.get(0));
    }

    private void loadFragment(Fragment fragment){
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.replace(R.id.frame_upload, fragment);
        trans.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == UploadAdapter.UPLOAD_CODE && data.hasExtra(Pix.IMAGE_RESULTS)) {
            ((UploadGaleriFragment)listFragment.get(1)).adapter.upload(data.getStringArrayListExtra(Pix.IMAGE_RESULTS));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.onBackPressed();
    }
}
