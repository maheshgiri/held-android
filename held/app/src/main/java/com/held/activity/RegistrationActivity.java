package com.held.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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

    private ImageView mBackImg;
    private EditText mUserNameEdt, mPhoneNoEdt;
    private boolean mNetWorStatus;
    private Button mRegisterBtn;
    private Spinner mCountryCodes;
    private String mCountryCode;
    private int mPin;
    private String mRegKey,mAccessToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mUserNameEdt = (EditText) findViewById(R.id.REG_user_name_edt);
        mPhoneNoEdt = (EditText) findViewById(R.id.REG_mobile_no_edt);
        mBackImg = (ImageView) findViewById(R.id.REG_back);
        mRegisterBtn = (Button) findViewById(R.id.REG_register_btn);
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.REG_back:
                finish();
                break;
            case R.id.REG_register_btn:
                Utils.hideSoftKeyboard(this);
                if (mNetWorStatus)
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
        HeldService.getService().createUser(mCountryCode + mPhoneNoEdt.getText().toString().trim(), mUserNameEdt.getText().toString().trim().toLowerCase(),"" ,new Callback<CreateUserResponse>() {
            @Override
            public void success(CreateUserResponse createUserResponse, Response response) {
                DialogUtils.stopProgressDialog();
                if (createUserResponse == null) return;
                PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.API_phone_no), mCountryCode + mPhoneNoEdt.getText().toString().trim());
                PreferenceHelper.getInstance(getApplicationContext()).writePreference(getString(R.string.API_user_name), mUserNameEdt.getText().toString().trim());

                mPin = createUserResponse.getPin();
                mRegKey=createUserResponse.getRid();
                mAccessToken=createUserResponse.getAccessToken();
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
        intent.putExtra("accessToken",mAccessToken);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNetworkStatusChanged(boolean isEnabled) {
        mNetWorStatus = isEnabled;
    }

}
