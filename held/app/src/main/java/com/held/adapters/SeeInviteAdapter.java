package com.held.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.held.activity.ParentActivity;
import com.held.activity.R;
import com.held.retrofit.response.InviteResponse;

import java.util.ArrayList;

/**
 * Created by MAHESH on 11/21/2015.
 */
public class SeeInviteAdapter extends BaseAdapter{
    ArrayList<InviteResponse> inviteList=new ArrayList<>();
    ParentActivity mActivity;
    private static LayoutInflater inflater=null;

    public SeeInviteAdapter(ArrayList<InviteResponse>inviteList,ParentActivity mActivity){
        this.inviteList=inviteList;
        this.mActivity=mActivity;
        inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return inviteList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mView=convertView;
        if (convertView == null)
            mView = inflater.inflate(R.layout.invite_item_layout,parent,false);


        TextView text=(TextView) mView.findViewById(R.id.contact_name);
        text.setText(inviteList.get(position).getPhone());
        Button btn = (Button)  mView.findViewById(R.id.action_invite);

        return mView;
    }
}
