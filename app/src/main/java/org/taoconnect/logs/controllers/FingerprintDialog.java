package org.taoconnect.logs.controllers;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.taoconnect.logs.tools.R;

//TODO: manage screen rotations
//TODO: look at attach problem when clicking on cancel

/** Class: FingerprintDialog
 *  This class loads up the dialog box and passes the cryptoobject to FingerprintUiHelper to authenticate the user
 *  Once the user gets authenticated, onAuthenticated gets called and the LogPicker class gets called. The fingerprint
 *  scanner gets destroyed and re-constructed when the activity gets reopened.
 */
public class FingerprintDialog extends DialogFragment implements FingerprintUihelper.Callback {

    private Button mCancelButton;
    private View mFingerprintContent;
    private CheckBox mUseFingerprintFutureCheckBox;


    private FingerprintManager.CryptoObject mCryptoObject;
    private FingerprintUihelper mFingerprintUiHelper;
    private Login mActivity;

    private SharedPreferences mSharedPreferences;
    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        //DO not create a new fragment when the activity is re-created (orientation changes)
        setRetainInstance(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog);
        }
        else
            setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_Dialog);



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

        String message = getTag();
        if(message == "true"){  // User had previously checked marked the box
            mUseFingerprintFutureCheckBox.setChecked(true);
        }
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

    // Fingerprint recognized
    @Override
    public void onAuthenticated() {
        Toast.makeText(mActivity, "Fingerprint recognized", Toast.LENGTH_SHORT).show();
        Intent login = new Intent(mActivity, LogPicker.class);
        login.putExtra("origin", "login");
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(getString(R.string.use_fingerprint_to_authenticate_key), mUseFingerprintFutureCheckBox.isChecked());
        editor.commit();
        startActivity(login);
        dismiss();
    }
}
