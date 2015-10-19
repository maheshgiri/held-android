package com.held.activity;

import android.graphics.Typeface;
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
import com.held.retrofit.response.Engager;
import com.held.retrofit.response.EngagersResponse;
import com.held.retrofit.response.FriendRequestObject;
import com.held.retrofit.response.User;
import com.held.utils.PreferenceHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

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
    private PreferenceHelper mPreference;;
    private ArrayList<String[]> mList;
    private String string1[]={"maheshTest2","/user_thumbnails/maheshTest2_1443592731143.jpg","Add as Friend"};
    private String mPostId;
    private int mlimit=10;
    private List<Engager> mEngagersList=new ArrayList<Engager>();
    private List<Engager> tempList=new ArrayList<Engager>();



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

        mPostId=getIntent().getExtras().getString("post_id");

        mSearch.setVisibility(View.GONE);
        mNotification.setVisibility(View.GONE);
        mCamera.setVisibility(View.GONE);
        mSearch_edt.setVisibility(View.GONE);

        mChat.setImageResource(R.drawable.back);
        mChat.setVisibility(View.VISIBLE);

        Typeface medium = Typeface.createFromAsset(getAssets(), "BentonSansMedium.otf");
        mTitle.setTypeface(medium);
        mTitle.setText("Seen By");
        //setToolbar();
        mPreference=PreferenceHelper.getInstance(this);
        mSeenRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mSeenRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        callSeenByApi();

        Timber.d("setting seen by adapter");
        mSeenByAdapter = new SeenByAdapter(this,mEngagersList);
        mSeenByAdapter.setEngagersList(mEngagersList);
        mSeenRecyclerView.setAdapter(mSeenByAdapter);

       // removeCurrentUser();
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.swipe_to_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                callSeenByApi();
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
    public void callSeenByApi()
    {
        HeldService.getService().getPostEngagers(mPreference.readPreference(getString(R.string.API_session_token)), mPostId, mlimit, false,
                new Callback<EngagersResponse>() {
                    @Override
                    public void success(EngagersResponse engagersResponse, Response response) {
                        mEngagersList.addAll(engagersResponse.getObjects());
                        tempList.addAll(engagersResponse.getObjects());
                        removeCurrentUser();
                        mSeenByAdapter.setEngagersList(mEngagersList);
                        mSeenByAdapter.notifyDataSetChanged();
                      //  Timber.d("Print SeenBy List\n"+mEngagersList.toString());

                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

    }

    public void removeCurrentUser(){
        String currentUser=mPreference.readPreference(getString(R.string.API_user_name));
        int sizeofList=tempList.size();
        if(sizeofList<=0)
            return;
        try{
           for(int i=0;i<=sizeofList;i++)
           {
               if(tempList.get(i).getUser().getDisplayName().equals(currentUser))
               {
                   tempList.remove(i);
                   Timber.i("User Removed");
                   break;
               }
           }
            mEngagersList=tempList;
       }catch (IndexOutOfBoundsException e)
       {
           e.printStackTrace();
       }

    }
}
