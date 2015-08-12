package com.held.retrofit;


import com.held.utils.AppConstants;

import retrofit.RestAdapter;

/**
 * Created by YMediaLabs on 04/02/15.
 * Helper class which provides Retrofit\'s RestAdapter .
 */
public class HeldService {

    public static HeldAPI getService() {
        return new RestAdapter
                .Builder()
                .setLogLevel(RestAdapter
                        .LogLevel.FULL)
                .setEndpoint(AppConstants.BASE_URL)
                .build()
                .create(HeldAPI.class);
    }
}
