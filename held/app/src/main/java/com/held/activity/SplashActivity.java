package com.held.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.held.utils.PreferenceHelper;


public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mGetStartedBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mGetStartedBtn = (Button) findViewById(R.id.SPLASH_get_started_btn);
        mGetStartedBtn.setOnClickListener(this);

        launchPostActivity();

//        if (!PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_phone_no)).isEmpty() &&
//                PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_pin), 0) != 0) {
//            launchPostActivity();
//        } else if (!PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_phone_no)).isEmpty() &&
//                PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_pin), 0) == 0)
//            launchVerificationActivity();
    }

    private void launchVerificationActivity() {
        Intent intent = new Intent(SplashActivity.this, VerificationActivity.class);
        intent.putExtra("username", PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_user_name)));
        intent.putExtra("phoneno", PreferenceHelper.getInstance(this).readPreference(getString(R.string.API_phone_no)));
        startActivity(intent);
        finish();
    }

    private void launchPostActivity() {
        Intent intent = new Intent(SplashActivity.this, PostActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.SPLASH_get_started_btn:
                Intent intent = new Intent(SplashActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
        }
    }
}
