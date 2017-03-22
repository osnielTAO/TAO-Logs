package com.logs.tao.taologs;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
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

    private SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();
        mFAB = (FloatingActionButton) findViewById(R.id.fingerprintReader);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //TODO: automatically ask for fingerprint

        useFingerprint = mSharedPreferences.getBoolean(getString(R.string.use_fingerprint_to_authenticate_key), false);
        if(useFingerprint)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startAuthentication(mFAB.findFocus());
            }
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if(!keyguardManager.isKeyguardSecure()){
                // TODO: ask user to enable lock screen in settings
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startAuthentication(View v) {
        generateKey();
        if (cipherInit()) {
            cryptoObject = new FingerprintManager.CryptoObject(cipher);
            FingerprintDialog fragment = new FingerprintDialog();
            fragment.setCryptoObject(cryptoObject);
            fragment.show(getFragmentManager(), "dialogtag");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressWarnings({"MissingPermission"})
    private void checkFingerprint(){
        fingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
        if (fingerprintManager.isHardwareDetected()) {
            mFAB.setVisibility(View.VISIBLE);
            if (!fingerprintManager.hasEnrolledFingerprints()) {
                //TODO: ask user to enroll fingerprints
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestcode, String[] permissions, int[] grantResults){
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



