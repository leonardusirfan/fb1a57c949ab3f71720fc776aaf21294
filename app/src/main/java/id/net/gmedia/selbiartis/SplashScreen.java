package id.net.gmedia.selbiartis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if(AppSharedPreferences.isLoggedIn(this)){
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        }
        else{
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            finish();
        }
    }
}