package com.logs.tao.taologs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by croxx219 on 3/20/17.
 */

public class FingerprintDialog extends DialogFragment implements FingerprintUihelper.Callback {

    private Button mCancelButton;
    private Button mSecondDialogButton;
    private View mFingerprintContent;
    private View mBackupContent;
    private EditText mPassword;
    private CheckBox mUseFingerprintFutureCheckBox;
    private TextView mPasswordDescriptionTextView;
    private TextView mNewFingerprintEnrolledTextView;


    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUihelper mFingerprintUiHelper;
    private Login mActivity;

    private SharedPreferences mSharedPreferences;
    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        //DO not create a new fragment when the activity is re-created (orientation changes)
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getDialog().setTitle(getString(R.string.sign_in));
        View v = inflater .inflate(R.layout.fingerprint_dialog_container, container, false);
        mCancelButton = (Button) v.findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                dismiss();
            }
        });

        mFingerprintContent = v.findViewById(R.id.fingerprint_container);
        mUseFingerprintFutureCheckBox = (CheckBox) v.findViewById(R.id.use_fingerprint_in_future_check);
        mFingerprintUiHelper = new FingerprintUihelper(mActivity.getSystemService(FingerprintManager.class), (ImageView) v.findViewById(R.id.fingerprint_icon),
                (TextView) v.findViewById(R.id.fingerprint_status), this);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume(){
        super.onResume();
        mFingerprintUiHelper.startListening(mCryptoObject);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onPause(){
        super.onPause();
        mFingerprintUiHelper.stopListening();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mActivity = (Login) getActivity();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject){
        mCryptoObject = cryptoObject;
    }

    @Override
    public void onAuthenticated() {
        Toast.makeText(mActivity, "authenticated", Toast.LENGTH_SHORT).show();
        dismiss();
    }


    @Override
    public void onError() {}
}
