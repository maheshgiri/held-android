package com.held.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.activity.R;

public class SeenByActivity extends ParentActivity {

    private ImageView mChat, mCamera, mNotification,mSearch;
    private EditText mSearch_edt;
    private TextView mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seen_by);

        mChat=(ImageView)findViewById(R.id.toolbar_chat_img);
        mSearch=(ImageView)findViewById(R.id.toolbar_search_img);
        mNotification=(ImageView)findViewById(R.id.toolbar_notification_img);
        mCamera=(ImageView)findViewById(R.id.toolbar_post_img);
        mTitle=(TextView)findViewById(R.id.toolbar_title_txt);
        mSearch_edt=(EditText)findViewById(R.id.toolbar_search_edt_txt);

        mSearch.setVisibility(View.GONE);
        mNotification.setVisibility(View.GONE);
        mCamera.setVisibility(View.GONE);
        mSearch_edt.setVisibility(View.GONE);

        mChat.setImageResource(R.drawable.back);
        mChat.setVisibility(View.VISIBLE);
        mTitle.setText("Seen By");
        setToolbar();



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seen_by, menu);
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
}
