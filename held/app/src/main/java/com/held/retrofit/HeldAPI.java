package com.held.retrofit;

import com.held.retrofit.response.AddFriendResponse;
import com.held.retrofit.response.ApproveDownloadResponse;
import com.held.retrofit.response.ApproveFriendResponse;
import com.held.retrofit.response.CreateUserResponse;
import com.held.retrofit.response.DeclineDownloadResponse;
import com.held.retrofit.response.DeclineFriendResponse;
import com.held.retrofit.response.DownloadRequestData;
import com.held.retrofit.response.DownloadRequestListResponse;
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
import com.held.retrofit.response.VerificationResponse;
import com.held.retrofit.response.VoiceCallResponse;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by YMediaLabs on 04/02/15.
 */
public interface HeldAPI {

    String CREATE_USER = "/users/create";
    String RESEND_SMS = "/users/resetpin";
    String VOICE_CALL = "/users/pincall";
    String LOGIN_USER = "/users/login";
    String VERIFY = "/users/verify";

    @GET(CREATE_USER)
    void createUser(@Query("phone") String phoneNo, @Query("name") String name, Callback<CreateUserResponse> createUserResponseCallback);

    @GET(RESEND_SMS)
    void resendSms(@Query("phone") String phoneNo, Callback<CreateUserResponse> createUserResponseCallback);

    @GET(VOICE_CALL)
    void voiceCall(@Query("phone") String phoneNo, Callback<VoiceCallResponse> voiceCallResponseCallback);

    @GET(LOGIN_USER)
    void loginUser(@Query("phone") String phoneNo, @Query("pin") String pin, Callback<LoginUserResponse> loginUserResponseCallback);

    @GET(VERIFY)
    void verifyUser(@Query("phone") String phoneNo, @Query("pin") String pin, Callback<VerificationResponse> verificationResponseCallback);

    @Multipart
    @POST("/posts/create")
    void uploadFile(@Query("text") String text, @Part("file") TypedFile photoFile, @Header("X-HELD-TOKEN") String token, Callback<PostResponse> postResponseCallback);

    @GET("/posts/")
    void feedPostWithPage(@Header("X-HELD-TOKEN") String token, @Query("limit") int limit, @Query("start") long start, Callback<FeedResponse> feedResponseCallback);

    @GET("/posts/")
    void feedPost(@Header("X-HELD-TOKEN") String token, Callback<FeedResponse> feedResponseCallback);


    @GET("/posts/hold")
    void holdPost(@Query("post") String postId, @Header("X-HELD-TOKEN") String token, Callback<HoldResponse> holdResponseCallback);

    @GET("/posts/release")
    void releasePost(@Query("post") String postId, @Query("time") long timeStamp, @Header("X-HELD-TOKEN") String token, Callback<ReleaseResponse> releaseResponseCallback);

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

    @GET("/users/search")
    void searchUser(@Header("X-HELD-TOKEN") String token, @Query("name") String name, Callback<SearchUserResponse> searchUserResponseCallback);

    @GET("/users/logout")
    void logoutUser(@Header("X-HELD-TOKEN") String token, @Query("phone") String phone, @Query("pin") int pin, Callback<LogoutUserResponse> logoutUserResponseCallback);

    @GET("/posts/messages/")
    void getPostChat(@Header("X-HELD-TOKEN") String token, @Query("post") String postId, Callback<PostChatResponse> postChatResponseCallback);

    @GET("/posts/message")
    void postChat(@Header("X-HELD-TOKEN") String token, @Query("post") String postId, @Query("message") String message, Callback<PostMessageResponse> postMessageResponseCallback);

    @GET("/users/profile")
    void updateProfilePic(@Header("X-HELD-TOKEN") String token, @Query("field") String fieldValue, @Query("value") String image,
                          Callback<ProfilPicUpdateResponse> profilPicUpdateResponseCallback);

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
}
