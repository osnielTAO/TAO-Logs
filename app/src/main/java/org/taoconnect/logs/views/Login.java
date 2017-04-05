package org.taoconnect.logs.views;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.taoconnect.logs.tools.ExtHttpClientStack;
import org.taoconnect.logs.tools.R;
import org.taoconnect.logs.tools.SslHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/***********************************************
/* My dev server  dev-01-us-w1.tao.local
/***********************************************/
public class Login extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public final int REQUEST_CODE = 0x1;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private KeyguardManager keyguardManager;
    private FingerprintManager fingerprintManager;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;
    private int permissionGranted;
    private FloatingActionButton mFAB;
    static final String DEFAULT_KEY_NAME = "default_key";
    private boolean useFingerprint;
    private boolean isFingerDialogShown = false;

    private SharedPreferences mSharedPreferences;
    final Context context = this;

    private Button loginButton;
    private EditText username;
    private EditText password;

    private FingerprintDialog fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        loginButton = (Button) findViewById(R.id.loginButton);
        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.passWord);

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView error = (TextView) findViewById(R.id.invalidCredentials);
                error.setVisibility(View.INVISIBLE);
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView error = (TextView) findViewById(R.id.invalidCredentials);
                error.setVisibility(View.INVISIBLE);
            }
        });

        mFAB = (FloatingActionButton) findViewById(R.id.fingerprintReader);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        useFingerprint = mSharedPreferences.getBoolean(getString(R.string.use_fingerprint_to_authenticate_key), false);

        if(useFingerprint)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isFingerDialogShown = true;
                startAuthentication(mFAB.findFocus());
            }
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if(!keyguardManager.isKeyguardSecure()){
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                mBuilder.setTitle("Screen lock is not activated");
                mBuilder.setMessage("Go to Settings > Security and select Screen lock");
                mBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT);
            if (permissionGranted != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.USE_FINGERPRINT}, REQUEST_CODE);
            }
            else {
               checkFingerprint();
            }
        }
    }

    private void login(){
        final Intent login = new Intent(getApplicationContext(), LogPicker.class);
        if(!isFingerDialogShown && (mFAB.getVisibility() == View.VISIBLE)){
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
            mBuilder.setTitle("Login with your fingerprint!");
            mBuilder.setMessage("Your device supports fingerprint login, do you want to use next time you sign in?");
            mBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean(getString(R.string.use_fingerprint_to_authenticate_key), true);
                    editor.apply();
                    startActivity(login);
                }
            });
            mBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    startActivity(login);
                }
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        }
        else {
            startActivity(login);
        }
    }

    public void verifyCredentials(View v){
        String user = username.getText().toString();
        String pass = password.getText().toString();
        InputStream keyStore = getResources().openRawResource(R.raw.mykeystore);
        RequestQueue queue = Volley.newRequestQueue(this, new ExtHttpClientStack(new SslHttpClient(keyStore,"123456")));
        String url = "https://172.23.50.150/login?user=" + user + "&passwd=" + pass;
        StringRequest mRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                Log.e("response ", response);
                if(response.equals("\"true\"")) {
                    login();
                    Log.e("Update", "Updated isAuthenticated");
                }
                else{
                    Animation a = AnimationUtils.loadAnimation(context, R.anim.shake);
                    a.reset();
                    TextView error = (TextView) findViewById(R.id.invalidCredentials);
                    error.setVisibility(View.VISIBLE);
                    error.clearAnimation();
                    error.startAnimation(a);
                }
            }

        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(context,"That didn't work", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(mRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startAuthentication(View v) {
        generateKey();
        if (cipherInit()) {
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            FingerprintDialog fragment = new FingerprintDialog();
            fragment.setCryptoObject(cryptoObject);
            isFingerDialogShown = true;
            fragment.setCancelable(false);
            fragment.show(getFragmentManager(), String.valueOf(useFingerprint));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressWarnings({"MissingPermission"})
    private void checkFingerprint(){
        fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
        if (fingerprintManager.isHardwareDetected()) {
            mFAB.setVisibility(View.VISIBLE);
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                mBuilder.setTitle("No fingerprints found");
                mBuilder.setMessage("Go to Settings > Security and select Screen lock");
                mBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestcode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestcode == REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            checkFingerprint();
        }

    }


    public void generateKey() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore");
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                throw new RuntimeException("Failed to get KeyGenerator instance", e);
            }

            try {
                keyStore.load(null);
                keyGenerator.init(new KeyGenParameterSpec.Builder("taologs_key", KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build());
                keyGenerator.generateKey();
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | CertificateException | IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey("taologs_key", null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }



}



