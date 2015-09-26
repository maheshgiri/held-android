package com.held.retrofit;

import android.media.Image;
import android.widget.ImageView;

import com.held.retrofit.response.ActivityFeedDataResponse;
import com.held.retrofit.response.AddFriendResponse;
import com.held.retrofit.response.ApproveDownloadResponse;
import com.held.retrofit.response.ApproveFriendResponse;
import com.held.retrofit.response.CreateUserResponse;
import com.held.retrofit.response.DeclineDownloadResponse;
import com.held.retrofit.response.DeclineFriendResponse;
import com.held.retrofit.response.DownloadRequestData;
import com.held.retrofit.response.DownloadRequestListResponse;
import com.held.retrofit.response.FeedData;
import com.held.retrofit.response.FeedResponse;
import com.held.retrofit.response.FriendDeclineResponse;
import com.held.retrofit.response.FriendRequestResponse;
import com.held.retrofit.response.FriendsResponse;
import com.held.retrofit.response.HoldResponse;
import com.held.retrofit.response.LoginUserResponse;
import com.held.retrofit.response.LogoutUserResponse;
import com.held.retrofit.response.PostChatResponse;
import com.held.retrofit.response.PostMessageResponse;
import com.held.retrofit.response.PostResponse;
import com.held.retrofit.response.ProfilPicUpdateResponse;
import com.held.retrofit.response.ReleaseResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.retrofit.response.UnDeclineFriendResponse;
import com.held.retrofit.response.UnFriendResponse;
import com.held.retrofit.response.User;
import com.held.retrofit.response.VerificationResponse;
import com.held.retrofit.response.VoiceCallResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by YMediaLabs on 04/02/15.
 */
public interface HeldAPI {

    String CREATE_USER = "/registrations/";
    String RESEND_SMS = "/registrations/{registration_id}/sms";
    String VOICE_CALL = "/registrations/{registration_id}/call";
    String LOGIN_USER = "/sessions/";
    String VERIFY = "/registrations/{registration_id}";

    @POST(CREATE_USER)
    void createUser(@Query("phone") String phoneNo, @Query("name") String name,@Body()String empty, Callback<CreateUserResponse> createUserResponseCallback);

    @GET(RESEND_SMS)
    void resendSms(@Path("registration_id") String RegId , @Query("phone") String phoneNo, @Body()String empty, Callback<CreateUserResponse> createUserResponseCallback);

    @GET(VOICE_CALL)
    void voiceCall(@Path("registration_id") String RegId , @Query("phone") String phoneNo, @Body()String empty, Callback<VoiceCallResponse> voiceCallResponseCallback);

    @POST(LOGIN_USER)
    void loginUser(@Query("phone") String phoneNo, @Query("pin") String pin,@Body()String empty, Callback<LoginUserResponse> loginUserResponseCallback);

    @PUT(VERIFY)
    void verifyUser(@Header("Authorization")String auth,@Path("registration_id") String RegId , @Query("pin") String pin,@Body()String empty ,Callback<VerificationResponse> verificationResponseCallback);


    @Multipart
    @POST("/posts/")
    void uploadFile(@Header("Authorization") String header,@Query("text") String text, @Part("file") TypedFile photoFile,@Query("url") String url , Callback<PostResponse> postResponseCallback);


    @Multipart
    @PUT("/users/{user_id}")
    void updateProfilePic(@Header("Authorization") String token,@Path("user_id")String uid, @Query("field") String fieldValue, @Part("value") String image,
                          Callback<ProfilPicUpdateResponse> profilPicUpdateResponseCallback);

    @GET("/users/{user_id}")
    void searchUser(@Header("Authorization") String token, @Path("user_id") String uid, Callback<SearchUserResponse> searchUserResponseCallback);


    @PUT("/posts/{post_id}/holds/{hold_id}")
    void releasePost(@Header("Authorization") String token,@Path("hold_id") String postId,@Query("start_time") String start_tm,@Query("end_time") String end_tm,@Body()String empty, Callback<ReleaseResponse> releaseResponseCallback);

    @POST("/posts/{post_id}/holds/")
    void holdPost(@Header("Authorization") String token,@Path("post_id") String postId,@Query("start_time") String start_tm, @Body()String empty,Callback<HoldResponse> holdResponseCallback);


    @GET("/posts/")
    void feedPostWithPage(@Header("Authorization") String token, @Query("limit") int limit, @Query("start") long start, Callback<FeedResponse> feedResponseCallback);

    @GET("/users/{user_id}/posts")
    void getUserPosts(@Header("Authorization") String token, @Path("user_id")String uid, @Query("start") long start,@Query("limit") int limit, Callback<FeedResponse> feedResponseCallback);

    @GET("/posts/{post_id}/holds/{hold_id}")
    void releasePostProfile(@Header("Authorization") String token, @Path("hold_id")String hid, Callback<ReleaseResponse> callback);


    ///////////////////************OLD APIs**************///////////////////////////////

   @GET("/users/")
   void searchFriend(@Header("Authorization") String token,@Query("name") String frndName,Callback<User> friendSearchResult);



    @GET("/posts/")
    void feedPost(@Header("X-HELD-TOKEN") String token, Callback<FeedResponse> feedResponseCallback);

    @GET("/friends")
    void getFriends(@Header("X-HELD-TOKEN") String token, Callback<FriendsResponse> friendsResponseCallback);

    @GET("/friends/requests")
    void getFriendRequests(@Header("X-HELD-TOKEN") String token, @Query("limit") int limit, @Query("start") long start, Callback<FriendRequestResponse> friendRequestResponseCallback);

    @GET("/friends/declined")
    void getDeclinedFriends(@Header("X-HELD-TOKEN") String token, Callback<FriendDeclineResponse> friendDeclineResponseCallback);

    @GET("/friends/add")
    void addFriend(@Header("X-HELD-TOKEN") String token, @Query("name") String name, Callback<AddFriendResponse> addFriendResponseCallback);

    @GET("/friends/approve")
    void approveFriend(@Header("X-HELD-TOKEN") String token, @Query("name") String name, Callback<ApproveFriendResponse> approveFriendResponseCallback);

    @GET("/friends/decline")
    void declineFriend(@Header("X-HELD-TOKEN") String token, @Query("name") String name, Callback<DeclineFriendResponse> declineFriendResponseCallback);

    @GET("/friends/undecline")
    void undeclineFriend(@Header("X-HELD-TOKEN") String token, @Query("name") String name, Callback<UnDeclineFriendResponse> unDeclineFriendResponseCallback);

    @GET("/friends/unfriend")
    void unFriend(@Header("X-HELD-TOKEN") String token, @Query("name") String name, Callback<UnFriendResponse> unFriendResponseCallback);



    @GET("/users/logout")
    void logoutUser(@Header("X-HELD-TOKEN") String token, @Query("phone") String phone, @Query("pin") int pin, Callback<LogoutUserResponse> logoutUserResponseCallback);

    @GET("/posts/messages/")
    void getPostChat(@Header("X-HELD-TOKEN") String token, @Query("post") String postId, Callback<PostChatResponse> postChatResponseCallback);

    @GET("/posts/message")
    void postChat(@Header("X-HELD-TOKEN") String token, @Query("post") String postId, @Query("message") String message, Callback<PostMessageResponse> postMessageResponseCallback);



    @GET("/friends/message")
    void friendChat(@Header("X-HELD-TOKEN") String token, @Query("friend") String friendId, @Query("message") String message, Callback<PostMessageResponse> postMessageResponseCallback);

    @GET("/friends/messages")
    void getFriendChat(@Header("X-HELD-TOKEN") String token, @Query("friend") String friendId, Callback<PostChatResponse> postChatResponseCallback);

    @GET("/friends/")
    void getFriendsList(@Header("X-HELD-TOKEN") String token, @Query("limit") int limit, @Query("start") long start, Callback<FriendRequestResponse> friendRequestResponseCallback);

    @GET("/posts/download_requests/")
    void getDownLoadRequestList(@Header("X-HELD-TOKEN") String token, @Query("limit") int limit, @Query("start") long start, Callback<DownloadRequestListResponse> downloadRequestListResponseCallback);

    @GET("/posts/decline_download")
    void declineDownloadRequest(@Header("X-HELD-TOKEN") String token, @Query("request") String rid, Callback<DeclineDownloadResponse> declineDownloadResponseCallback);

    @GET("/posts/approve_download")
    void approveDownloadRequest(@Header("X-HELD-TOKEN") String token, @Query("request") String rid, Callback<ApproveDownloadResponse> approveDownloadResponseCallback);

    @GET("/users/profile")
    void updateRegID(@Header("X-HELD-TOKEN") String token, @Query("field") String fieldValue, @Query("value") String image,
                     Callback<SearchUserResponse> searchUserResponseCallback);

    @GET("/posts/request_download")
    void requestDownLoadPost(@Header("X-HELD-TOKEN") String token, @Query("post") String fieldValue, Callback<DownloadRequestData> downloadRequestDataCallback);



    @GET("/activities/")
    void getActivitiesFeed(@Header("X-HELD-TOKEN") String token, @Query("limit") int limit, @Query("start") long start, @Query("user") String uid, Callback<ActivityFeedDataResponse> activityFeedDataResponseCallback);

    @GET("/posts/search")
    void postSearch(@Header("X-HELD-TOKEN") String token, @Query("post") String postId, Callback<PostResponse> postResponseCallback);


}
