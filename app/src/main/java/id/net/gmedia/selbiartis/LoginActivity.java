package id.net.gmedia.selbiartis;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.leonardus.irfan.ApiVolleyManager;
import com.leonardus.irfan.AppRequestCallback;
import com.leonardus.irfan.JSONBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

    //Label activity
    private static final int RC_SIGN_IN = 234;

    //Variabel autentifikasi
    private GoogleSignInClient client;
    private FirebaseAuth auth;
    private CallbackManager callbackManager;

    //Variabel UI
    private Button btn_facebook, btn_gmail;
    private ProgressBar bar_loading;

    private String fcm_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Inisialisasi UI
        btn_facebook = findViewById(R.id.btn_login_facebook);
        btn_gmail = findViewById(R.id.btn_login_gmail);
        bar_loading = findViewById(R.id.bar_loading);

        //inisialisasi Autentifikasi Firebase
        auth = FirebaseAuth.getInstance();

        //Inisialisasi Login Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        client = GoogleSignIn.getClient(this, gso);
        /*btn_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true);
                Intent signInIntent = client.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });*/

        //Inisialisasi Login Facebook
        callbackManager = CallbackManager.Factory.create();
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( LoginActivity.this,  new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        fcm_id = instanceIdResult.getToken();
                        AppSharedPreferences.setFcmId(LoginActivity.this, fcm_id);
                        Log.d(Constant.TAG, fcm_id);
                    }
                });

                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                setLoading(false);
                Log.w(Constant.TAG, "Login dibatalkan");
            }

            @Override
            public void onError(FacebookException error) {
                setLoading(false);
                Toast.makeText(getApplicationContext(),"Autentifikasi Facebook Gagal",Toast.LENGTH_SHORT).show();
            }
        });

        btn_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true);
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
            }
        });

        btn_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true);
                Intent signInIntent = client.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        /*findViewById(R.id.btn_login_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoading(true);
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(Constant.TAG, "signInWithEmail:success");
                                    FirebaseUser user = auth.getCurrentUser();
                                    setLoading(false);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(Constant.TAG, "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    setLoading(false);
                                }
                                // ...
                            }
                        });

            }
        });*/
    }

    private void setLoading(boolean loading){
        if(loading){
            bar_loading.setVisibility(View.VISIBLE);
            btn_gmail.setEnabled(false);
            btn_facebook.setEnabled(false);
        }
        else{
            bar_loading.setVisibility(View.INVISIBLE);
            btn_gmail.setEnabled(true);
            btn_facebook.setEnabled(true);
        }
    }

    //Menangani hasil login facebook dan google
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //Apabila kode request adalah kode sign in google yang telah didefinisikan
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try{
                //Sign in berhasil, autentifikasi dengan Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( this,  new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        fcm_id = instanceIdResult.getToken();
                        Log.d(Constant.TAG, fcm_id);
                    }
                });

                if(account != null){
                    firebaseAuthWithGoogle(account);
                }
            }
            catch (ApiException e){
                setLoading(false);
                Log.e(Constant.TAG, e.toString());
                Toast.makeText(LoginActivity.this, "Autentifikasi Firebase Gagal", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    //Proses Autentifikasi Google
    private void firebaseAuthWithGoogle(GoogleSignInAccount acc){
        Log.d(Constant.TAG, "FirebaseAuthGoogle : " + acc.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(Constant.TAG, "signInWithCredential:success");
                            client.signOut();

                            loginToSelby("Google");
                        } else {
                            // If sign in fails, display a message to the user.
                            setLoading(false);
                            Log.w(Constant.TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Autentifikasi Google gagal",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Proses Autentifikasi Facebook
    private void firebaseAuthWithFacebook(AccessToken accessToken){
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(Constant.TAG, "signInWithCredential:success");
                    LoginManager.getInstance().logOut();

                    loginToSelby("Facebook");
                } else {
                    // If sign in fails, display a message to the user.
                    setLoading(false);
                    Log.w(Constant.TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "Autentifikasi Facebook gagal",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loginToSelby(String type){
        JSONBuilder body = new JSONBuilder();
        body.add("uid", FirebaseAuth.getInstance().getUid());
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            body.add("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
            body.add("profile_name", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            body.add("foto", FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        }
        else{
            body.add("email", "");
            body.add("profile_name", "");
            body.add("foto", "");
            Log.w(Constant.TAG, "Current User kosong");
        }

        body.add("type", type);
        body.add("fcm_id", fcm_id);

        ApiVolleyManager.getInstance().addRequest(this, Constant.URL_AUTENTIFIKASI,
                ApiVolleyManager.METHOD_POST, Constant.HEADER_AUTH, body.create(),
                new AppRequestCallback(new AppRequestCallback.SimpleRequestListener() {
            @Override
            public void onSuccess(String response) {
                try{
                    //Masuk halaman home
                    JSONObject json = new JSONObject(response);
                    if(json.getString("penjual").equals("1")){
                        AppSharedPreferences.setLoggedIn(LoginActivity.this, true);
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);

                        finish();
                    }
                    else{
                        Toast.makeText(LoginActivity.this, "Akun tidak memiliki izin login", Toast.LENGTH_SHORT).show();

                    }

                    setLoading(false);
                }
                catch (JSONException e){
                    setLoading(false);
                    Log.e(Constant.TAG, e.getMessage());
                    Toast.makeText(LoginActivity.this, "Autentifikasi gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(String message) {
                setLoading(false);
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
