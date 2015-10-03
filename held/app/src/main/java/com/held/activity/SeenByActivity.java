package com.held.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.held.adapters.SeenByAdapter;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.Objects;
import com.held.retrofit.response.User;
import com.held.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SeenByActivity extends ParentActivity {

    private ImageView mChat, mCamera, mNotification,mSearch;
    private EditText mSearch_edt;
    private TextView mTitle;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SeenByAdapter mSeenByAdapter;
   // private List<String []> mList = new ArrayList<>();
    private RecyclerView mSeenRecyclerView;
    private boolean isLastPage,isLoading;
    private LinearLayoutManager mLayoutManager;
    private String username="maheshTest2",img="/user_thumbnails/maheshTest2_1443592731143.jpg";
    private PreferenceHelper mPrefernce;
    private ArrayList<String[]> mList;
    private String string1[]={"maheshTest2","/user_thumbnails/maheshTest2_1443592731143.jpg","Add as Friend"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seen_by);
       // mLayoutManager = new LinearLayoutManager(this);

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
        mPrefernce=PreferenceHelper.getInstance(this);
        mSeenRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSeenRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        Log.i("@@Adapter calling@@", "Before");
        mSeenByAdapter = new SeenByAdapter(this,username,img,isLastPage);
        mSeenRecyclerView.setAdapter(mSeenByAdapter);
        Log.i("@@Adapter calling@@", "After");


        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_to_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
return;
            }
        });
/*
        mSeenRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCoount = mLayoutManager.getItemCount();
                int lastVisibleItemPosition = mLayoutManager.findLastVisibleItemPosition();

                if (!isLastPage && (lastVisibleItemPosition + 1) == totalItemCoount && !isLoading) {
return;
                }
            }
        });
*/

    }
    public User setProfile(String username)
    {
        User user1=new User();
        HeldService.getService().searchFriend(mPrefernce.readPreference(getString(R.string.API_session_token)), username,
                new Callback<Objects>() {
                    @Override
                    public void success(Objects objects, Response response) {
                       // user1.setDisplayName(objects.getCreator().getDisplayName());
                       // user1.setProfilePic(objects.getCreator().getProfilePic());
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

        return user1;
    }


}
