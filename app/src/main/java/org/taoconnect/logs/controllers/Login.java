package org.taoconnect.logs.controllers;

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

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
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


/** Class: Login
 *  This class is the activity that launches right after the splash screen and authenticates the user.
 *  It reads a key and enrypts it using a cypher to authenticate the user fingerprint. It is required by the FingerprintManager
 *  to happen this way.
 *  First it checks that the user has all requirements for fingerprint reader: hardware, permissions and existing fingerprints.
 *  If the user has not registered fingerprints it shows a dialog asking to go to Settings and register them or activate
 *  the keyguard Manager.
 *  If all requirements are met, then the FAB button is showed. If the user checked to always use fingerprint authenticated
 *  in the fingerprint dialog box, then the dialog box opens instantly onlogin.
 *  If this is the user first login, then it will not allow for fingerprint recognition even if the requirements are met.
 *  If the user clicks on login then it sends an authentication request through http using volley to the remote server.
 *  If authentication fails, it displays an error message, otherwise it initializes LogPicker.
 *  If the user logins this way, and there is a fingerprint reader available (requirements are met) and the user has never
 *  tried fingerprint login before, then it displays a dialog message asking the user to use fingerprint in the future.
 *  Regardless of user selection LogPicker will start.
 */
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
    private boolean firstLogin;

    private SharedPreferences mSharedPreferences;
    final Context context = this;

    private Button loginButton;
    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        loginButton = (Button) findViewById(R.id.loginButton);
        username = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.passWord);

        // Removes error message due to auth fail after being displayed
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

        useFingerprint = mSharedPreferences.getBoolean(getString(R.string.use_fingerprint_to_authenticate_key), false); // User wants to use fingerprint at login
        firstLogin = mSharedPreferences.getBoolean(getString(R.string.first_time_login), true);  // First time authenticating, do not allow fingerprint auth


        // If user wanted to use fingerprint then open dialog for fingerprint auth inmmediately
        if(useFingerprint)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                isFingerDialogShown = true;  // User already saw the dialog, probably in last session
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

    // Intent that gets called after authentication through login button
    // Lets the user know it can use fingerprint if it has never seen the fingerprint dialog before and
    // hardware requirements are met (Implicitly deduced from FAB being visible), refer to line 267)
    private void login(){
        final Intent login = new Intent(getApplicationContext(), LogPicker.class);
        login.putExtra("origin", "login");
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

    // Listener for login button
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
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean(getString(R.string.first_time_login), false);
                    editor.apply();
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
                String message = null;
                if (error instanceof NetworkError) {
                    message = "Network error";
                } else if (error instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (error instanceof AuthFailureError) {
                    message = "auth fail";
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (error instanceof NoConnectionError) {
                    message = "NoConnection";
                } else if (error instanceof TimeoutError) {
                    message = "Connection TimeOut! Please check your internet connection.";
                }
                Log.e("Error", message);


            }
        });

        queue.add(mRequest);
    }

    // Listener for FAB button
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


    // Checks hardware requirements and that it is not first login
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressWarnings({"MissingPermission"})
    private void checkFingerprint(){
        fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
        if (fingerprintManager.isHardwareDetected() && !firstLogin) {
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



