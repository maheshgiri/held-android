package com.held.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.held.customview.CustomTextView;
import com.held.receiver.NetworkStateReceiver;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.CreateUserResponse;
import com.held.utils.DialogUtils;
import com.held.utils.NetworkUtil;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class RegistrationActivity extends ParentActivity implements View.OnClickListener, NetworkStateReceiver.OnNetworkChangeListener {

    private static final String TAG = "RegistrationActivity";
    private ImageView mBackImg;
    private EditText mUserNameEdt, mPhoneNoEdt;
    private boolean mNetWorStatus,flag=false;
    private Button mRegisterBtn;
    private Spinner mCountryCodes;
    private String mCountryCode,tempCode;
    private int mPin;
    private String mRegKey,mAccessToken;
    private PreferenceHelper mPrefernce;
    private CustomTextView mspinnerText;
private TextView mPolicy;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "starting Registration activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        if(getIntent().getExtras()!=null)
            flag=getIntent().getExtras().getBoolean("ForLogin");
        mUserNameEdt = (EditText) findViewById(R.id.REG_user_name_edt);
        mPhoneNoEdt = (EditText) findViewById(R.id.REG_mobile_no_edt);
        mBackImg = (ImageView) findViewById(R.id.REG_back);
        mRegisterBtn = (Button) findViewById(R.id.REG_register_btn);
        mspinnerText=(CustomTextView)findViewById(R.id.spinner_Text);
        mBackImg.setOnClickListener(this);
        mRegisterBtn.setOnClickListener(this);
        mNetWorStatus = NetworkUtil.isInternetConnected(getApplicationContext());
        NetworkStateReceiver.registerOnNetworkChangeListener(this);
        mCountryCodes = (Spinner) findViewById(R.id.REG_country_code_edt);
        String countryCodes[] = getResources().getStringArray(R.array.country_codes);
        mCountryCodes.setAdapter(new ArrayAdapter(this, R.layout.row_spinner_item, countryCodes));
        ArrayAdapter myAdap = (ArrayAdapter) mCountryCodes.getAdapter();
        int spinnerPosition = myAdap.getPosition("+91 (India)");
        mCountryCodes.setSelection(spinnerPosition);
        mPolicy=(TextView)findViewById(R.id.SPLASH_terms_condition_txt);
        Context ctx = getApplicationContext();
        if (ctx != null) {
            Typeface type = Typeface.createFromAsset(ctx.getAssets(),
                    "BentonSansBook.otf");
            mUserNameEdt.setTypeface(type);
            mPhoneNoEdt.setTypeface(type);
            mRegisterBtn.setTypeface(type);
            mPolicy.setTypeface(type);


        }
        mPrefernce=PreferenceHelper.getInstance(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if(flag)
        {
            updateToLoginUI();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.REG_back:
                launchSplashScreen();
                break;
            case R.id.REG_register_btn:
                Utils.hideSoftKeyboard(this);
                if (mNetWorStatus)
                    if(flag)
                        launchLoginVerificationActivity();
                    else
                        validateInput();
                else
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), Utils.getString(R.string.error_offline_msg));
                break;
        }
    }

    private void validateInput() {
        boolean validate = false;
        if (!TextUtils.isEmpty(mUserNameEdt.getText().toString().trim())) {
            if (mUserNameEdt.getText().toString().trim().length() < 6 || mUserNameEdt.getText().toString().trim().length() > 15) {
                mUserNameEdt.setError("Please enter between 6 to 15 chars.");
                mUserNameEdt.requestFocus();
            } else if (!containsChar(mUserNameEdt.getText().toString())) {
                mUserNameEdt.setError("Please enter Alphabet and digits.");
                mUserNameEdt.requestFocus();
            } else {
                validate = true;
            }
        } else {
            mUserNameEdt.setError("Please enter user name");
            mUserNameEdt.requestFocus();
        }
        if (validate) {
            String cc[] = mCountryCodes.getSelectedItem().toString().split(" ");
            mCountryCode = cc[0];
            tempCode=mCountryCode;
            mCountryCode=tempCode.substring(1);
            if (mNetWorStatus) {
                DialogUtils.showProgressBar();
                    callCreateUserApi();
            } else {
                UiUtils.showSnackbarToast(findViewById(R.id.root_view), "You are not connected to internet.");
            }
        }
    }

    private boolean containsChar(String s) {
        return s.matches("[a-zA-Z0-9]*");
    }

    private void callCreateUserApi() {
        HeldService.getService().createUser( mUserNameEdt.getText().toString().trim().toLowerCase(),mCountryCode + mPhoneNoEdt.getText().toString().trim(),"" ,new Callback<CreateUserResponse>() {
            @Override
            public void success(CreateUserResponse createUserResponse, Response response) {
                DialogUtils.stopProgressDialog();
                if (createUserResponse == null)
                {
                    return;
                }
                mPrefernce.writePreference(getString(R.string.API_phone_no), mCountryCode + mPhoneNoEdt.getText().toString().trim());
                mPrefernce.writePreference(getString(R.string.API_user_name), mUserNameEdt.getText().toString().trim());

                mPin = createUserResponse.getPin();
                mRegKey=createUserResponse.getRid();
                mAccessToken=createUserResponse.getAccessToken();
                PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.API_session_token), mAccessToken);
                Log.i("RegistrationActivity", "Profile PIN" + createUserResponse.toString());
                launchVerificationActivity();
            }

            @Override
            public void failure(RetrofitError error) {
                DialogUtils.stopProgressDialog();
                if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                    String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), json.substring(json.indexOf(":") + 2, json.length() - 2));
                } else
                    UiUtils.showSnackbarToast(findViewById(R.id.root_view), "Some Problem Occurred");

            }
        });
    }

    private void launchVerificationActivity() {
        Intent intent = new Intent(RegistrationActivity.this, VerificationActivity.class);
        intent.putExtra("username", mUserNameEdt.getText().toString().trim());
        intent.putExtra("phoneno", mCountryCode + mPhoneNoEdt.getText().toString().trim());
        intent.putExtra("pin", mPin);
        intent.putExtra("regId",mRegKey);
        Log.d(TAG, "sending registration id : " + mRegKey);
        Log.d(TAG, "sending access tokes to verify activity: " + mAccessToken);
        intent.putExtra("accessToken",mAccessToken);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNetworkStatusChanged(boolean isEnabled) {
        mNetWorStatus = isEnabled;
    }

    public void updateToLoginUI(){
        mUserNameEdt.setVisibility(View.GONE);
        mRegisterBtn.setText("Loign");
    }

    public void  launchLoginVerificationActivity(){
        String cc[] = mCountryCodes.getSelectedItem().toString().split(" ");
        mCountryCode = cc[0];
//        tempCode=mCountryCode;
//        mCountryCode=tempCode.substring(1);
        mPrefernce.writePreference(getString(R.string.API_phone_no), mCountryCode + mPhoneNoEdt.getText().toString().trim());
        Intent intent = new Intent(RegistrationActivity.this, VerificationActivity.class);
        intent.putExtra("phoneno", mCountryCode + mPhoneNoEdt.getText().toString().trim());
        intent.putExtra("ForLogin",true);
        startActivity(intent);
        finish();
    }
    public void launchSplashScreen(){
        Intent intent = new Intent(RegistrationActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }
}