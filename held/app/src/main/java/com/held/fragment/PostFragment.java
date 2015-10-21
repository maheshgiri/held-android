package com.held.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.held.activity.FeedActivity;
import com.held.activity.PostActivity;
import com.held.activity.R;
import com.held.activity.SeenByActivity;
import com.held.customview.PicassoCache;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.PostResponse;
import com.held.retrofit.response.ProfilPicUpdateResponse;
import com.held.retrofit.response.SearchUserResponse;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import timber.log.Timber;

public class PostFragment extends ParentFragment {

    public static final String TAG = PostFragment.class.getSimpleName();
    private ImageView mPostImg, mUserImg, mImageToUpload,mBackImg;
    private TextView mPostTxt, mUserNameTxt, mCancelTxt, mOkTxt, mTimeTxt,mTitle;
    private RelativeLayout mPostLayout;
    private RelativeLayout mBoxLayout,mTimeLayout;
    private String sourceFileName, mCaption,mImageUri,muserProfileUrl;
    private File mFile;
    private Uri mFileUri;
    private EditText mCaptionEdt;
    private Fragment mfragment;
    private GestureDetector mGestureDetector;
    private Button mPostBtn;
    private PreferenceHelper mPrefernce;

    public static PostFragment newInstance() {

        Log.d(TAG, "Returning new instance of PostFragment");
        return new PostFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "Inflating compose fragment in onCreateView");
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mUserImg = (ImageView) view.findViewById(R.id.profile_img);
        mUserNameTxt = (TextView) view.findViewById(R.id.user_name_txt);
        mPostTxt = (TextView) view.findViewById(R.id.post_txt);
        mPostImg = (ImageView) view.findViewById(R.id.post_image);
        mBoxLayout = (RelativeLayout) view.findViewById(R.id.BOX_layout);
        mPostLayout = (RelativeLayout) view.findViewById(R.id.POST_post_data);
        mImageToUpload = (ImageView) view.findViewById(R.id.POST_image);
        mBackImg=(ImageView)view.findViewById(R.id.back_home);
        mCaptionEdt = (EditText) view.findViewById(R.id.post_edit_txt);
        mCaptionEdt.setVisibility(View.VISIBLE);
        mCancelTxt = (TextView) view.findViewById(R.id.POST_cancel);
        mOkTxt = (TextView) view.findViewById(R.id.POST_ok);
        mPostBtn=(Button)view.findViewById(R.id.post_button);
        mPrefernce=PreferenceHelper.getInstance(getCurrActivity());
        mTitle=(TextView)view.findViewById(R.id.tv_title);
        setTypeFace(mPostBtn,"book");
        setTypeFace(mUserNameTxt,"1");
        setTypeFace(mPostTxt,"book");
        setTypeFace(mPostBtn,"1");
        setTypeFace(mTitle,"1");
        setProfilePic();

        TextView mTitle = (TextView)view.findViewById(R.id.tv_title);
        PreferenceHelper myhelper = PreferenceHelper.getInstance(getCurrActivity());
        if (muserProfileUrl==null&&myhelper.readPreference(getString(R.string.is_first_post),false)) {
            mTitle.setText(getString(R.string.title_profilepic_upload));
        }else{
            mTitle.setText(getString(R.string.title_photo_upload));
        }

        mTimeLayout=(RelativeLayout)view.findViewById(R.id.time_layout);
        mBackImg.setOnClickListener(this);
        mPostBtn.setOnClickListener(this);
        mUserNameTxt.setText(PreferenceHelper.getInstance(getCurrActivity()).readPreference("USER_NAME"));
        //mTimeTxt = (TextView) view.findViewById(R.id.box_time_txt);
       // mTimeTxt.setText("Click here to upload Image");

      //  mTimeTxt.setVisibility(View.GONE);
        mTimeLayout.setVisibility(View.GONE);
       // mPostImg.setVisibility(View.VISIBLE);

        if (getCurrActivity() instanceof PostActivity)
            openImageIntent();

        mGestureDetector = new GestureDetector(getCurrActivity(), new GestureListener());

        mPostImg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });

        getView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return mGestureDetector.onTouchEvent(motionEvent);
            }
        });



    }

    @Override
    protected void bindListeners(View view) {
        mPostImg.setOnClickListener(this);
        mCancelTxt.setOnClickListener(this);
        mOkTxt.setOnClickListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
            openImageIntent();
    }

    @Override
    public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.post_image:
//                openImageIntent();
                break;
          /*  case R.id.TOOLBAR_retake_btn:
                openImageIntent();
                break;*/
            case R.id.post_button:
                if (mFile != null & getCurrActivity().getNetworkStatus()) {
                    DialogUtils.showProgressBar();
                    callPostDataApi();
                } else {
                    UiUtils.showSnackbarToast(getView(), "You are not connected to internet.");
                }
                mCaptionEdt.setVisibility(View.GONE);

                break;
            case R.id.POST_cancel:
                Utils.hideSoftKeyboard(getCurrActivity());
                getCurrActivity().getToolbar().setVisibility(View.VISIBLE);
                mBoxLayout.setVisibility(View.VISIBLE);
                mPostLayout.setVisibility(View.GONE);
                break;
            case R.id.POST_ok:
                Utils.hideSoftKeyboard(getCurrActivity());
                mTimeTxt.setVisibility(View.INVISIBLE);
                //updateBoxUI();
                break;
            case R.id.back_home:
                Intent intent = new Intent(getCurrActivity(),FeedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getCurrActivity().finish();

        }
    }

    private void updateBoxUI() {
       // getCurrActivity().getToolbar().setVisibility(View.VISIBLE);
        mBoxLayout.setVisibility(View.VISIBLE);
        mPostLayout.setVisibility(View.GONE);
       // mCaption = mCaptionEdt.getText().toString().trim();
        Timber.i("Inside Update Box");
        BitmapFactory.Options options = new BitmapFactory.Options();
        UiUtils.calculateInSampleSize(options,
                UiUtils.dpToPx(getResources(), 350),
                UiUtils.dpToPx(getResources(), 350));
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        Bitmap mAttachment;
        try {
            mAttachment = MediaStore.Images.Media.getBitmap(getCurrActivity().getContentResolver(), mFileUri);
            mPostImg.setImageBitmap(mAttachment);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        Timber.i("End Update Box");
    }

    private void openImageIntent() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getCurrActivity());
        builder.setTitle("Select Source");
        CharSequence charSequence[] = {"Camera ", "Gallery"};
        builder.setItems(charSequence,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            case 0:
                                // GET IMAGE FROM THE CAMERA
                                Intent getCameraImage = new Intent(
                                        "android.media.action.IMAGE_CAPTURE");
                                getCameraImage.putExtra("android.intent.extras.CAMERA_FACING", 1);

                                File cameraFolder;

                                cameraFolder = new File(Environment
                                        .getExternalStorageDirectory(), "/HELD");
                                if (!cameraFolder.exists())
                                    cameraFolder.mkdirs();

                                sourceFileName = "/IMG_"
                                        + System.currentTimeMillis() + ".jpg";

                                File photo = new File(cameraFolder, sourceFileName);
                                getCameraImage.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(photo));
                                if (getCurrActivity() instanceof PostActivity) {
                                    startActivityForResult(getCameraImage,
                                            AppConstants.REQUEST_CAMEAR);
                                } else {
                                    getParentFragment().startActivityForResult(getCameraImage,
                                            AppConstants.REQUEST_CAMEAR);
                                }

                                break;

                            case 1:
                                Intent intent;
                                intent = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                Intent chooser = Intent.createChooser(intent,
                                        "Choose a Picture");
                                if (getCurrActivity() instanceof PostActivity) {
                                    startActivityForResult(chooser,
                                            AppConstants.REQUEST_GALLERY);
                                } else {
                                    getParentFragment().startActivityForResult(chooser,
                                            AppConstants.REQUEST_GALLERY);
                                }

                                break;

                            default:
                                break;
                        }
                    }
                });

        builder.show();
//updateBoxUI();

    }

    private void doCrop(Uri mCurrentPhotoPath) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(mCurrentPhotoPath, "image/*");
        if (!PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.is_first_post), false)) {
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 320);
            cropIntent.putExtra("outputY", 320);
        }

        File cameraFolder;

        cameraFolder = new File(Environment.getExternalStorageDirectory(),
                "/HELD");

        if (!cameraFolder.exists()) {
            cameraFolder.mkdirs();
        }

        sourceFileName = "/IMG_" + System.currentTimeMillis() + ".jpg";

        File photo = new File(cameraFolder, sourceFileName);
        try {
            photo.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mFile = photo;

        Uri mCropImageUri = Uri.fromFile(photo);

        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCropImageUri);

        if (getCurrActivity() instanceof PostActivity)
            startActivityForResult(cropIntent, AppConstants.REQUEST_CROP);
        else
            getParentFragment().startActivityForResult(cropIntent, AppConstants.REQUEST_CROP);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case AppConstants.REQUEST_CAMEAR:
                    File photo = new File(Environment.getExternalStorageDirectory(),
                            "/HELD" + sourceFileName);
                    Uri photoUri = Uri.fromFile(photo);
                    doCrop(photoUri);
                    mFileUri=photoUri;
                    mFile = new File(photoUri.getPath());
                    updateBoxUI();
                    break;

                case AppConstants.REQUEST_GALLERY:
                    Uri PhotoURI = data.getData();
                    doCrop(PhotoURI);
                    mFileUri=PhotoURI;
                    mFile = new File(getRealPathFromURI(PhotoURI));
                    updateBoxUI();
                    break;
            }
        }

        if (requestCode == AppConstants.REQUEST_CROP) {
            File photo = new File(Environment.getExternalStorageDirectory(),
                    "/HELD" + sourceFileName);

            if (resultCode != Activity.RESULT_OK) {
                photo.delete();
                return;
            }



        }


    }

    private String getRealPathFromURI(Uri contentURI) {
        String result = "";
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getCurrActivity().getContentResolver().query(contentURI, filePathColumn, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(filePathColumn[0]);//MediaStore.Images.ImageColumns.DATA
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void updateUI() {
        mBoxLayout.setVisibility(View.GONE);
        mPostLayout.setVisibility(View.VISIBLE);
        getCurrActivity().getToolbar().setVisibility(View.GONE);
        BitmapFactory.Options options = new BitmapFactory.Options();
        // BitmapFactory.decodeFile(mPicturePath, options);

        options.inSampleSize = UiUtils.calculateInSampleSize(options,
                UiUtils.dpToPx(getResources(), 200),
                UiUtils.dpToPx(getResources(), 350));
        options.inJustDecodeBounds = false;
        Bitmap mAttachment = BitmapFactory.decodeFile(mFile.getAbsolutePath(),
                options);
        mImageToUpload.setImageBitmap(mAttachment);
//        PreferenceHelper.getInstance(getCurrActivity()).readPreference("isFirstPostCreated", false)

            mCaptionEdt.setVisibility(View.VISIBLE);
    }

    private void callPostDataApi() {
        PreferenceHelper helper = PreferenceHelper.getInstance(getCurrActivity());
        String sessionToken = helper.readPreference(getString(R.string.API_session_token));
        Log.i("PostFrgament", "Session token: " + sessionToken);

        HeldService.getService().uploadFile(sessionToken,
                mCaptionEdt.getText().toString().trim(), new TypedFile("multipart/form-data", mFile), "", new Callback<PostResponse>() {
                    @Override
                    public void success(PostResponse postResponse, Response response) {
                        DialogUtils.stopProgressDialog();

                PreferenceHelper myhelper = PreferenceHelper.getInstance(getCurrActivity());
                if (myhelper.readPreference(getString(R.string.is_first_post), false)==false) {
                    myhelper.writePreference(getString(R.string.is_first_post), true);

                    Timber.i("This is user's first post. Setting it as profile image");
                    callPicUpdateApi(postResponse.getImageUri());
                    callThumbnailUpdateApi(postResponse.getThumbnailUri());
                    launchProfileScreen();
                }
                else {
                    callThumbnailUpdateApi(postResponse.getThumbnailUri());
                    launchFeedScreen();
                }


                getCurrActivity().perform(1, null);
                if (getCurrActivity() instanceof PostActivity)
                    getCurrActivity().perform(1, null);
                else
                    ((HomeFragment) getParentFragment()).mViewPager.setCurrentItem(2);

            }



                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                });
    }

    //todo: what is this function doing??
    //Todo: put notification token in user object
    private void callThumbnailUpdateApi(String image) {
        HeldService.getService().updateProfilePic(mPrefernce.readPreference(getString(R.string.API_session_token)),
                mPrefernce.readPreference(getString(R.string.API_user_regId)),"notification_token",mPrefernce.readPreference(getString(R.string.API_gcm_registration_key)), new Callback<ProfilPicUpdateResponse>() {
                    @Override
                    public void success(ProfilPicUpdateResponse profilPicUpdateResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                        Timber.i(TAG,"Push notification token complete");

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                }
        );
    }

    private void callPicUpdateApi(String image) {
        HeldService.getService().updateProfilePic(mPrefernce.readPreference(getString(R.string.API_session_token)),
                mPrefernce.readPreference(getString(R.string.API_user_regId)), "pic", image, new Callback<ProfilPicUpdateResponse>() {
                    @Override
                    public void success(ProfilPicUpdateResponse profilPicUpdateResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                  //      PreferenceHelper.getInstance(getCurrActivity()).writePreference(getString(R.string.API_user_img), AppConstants.BASE_URL + profilPicUpdateResponse.getPic());
                   //     UiUtils.showSnackbarToast(getView(), "User Profile  Pic Updated..");

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogUtils.stopProgressDialog();
                        if (error != null && error.getResponse() != null && !TextUtils.isEmpty(error.getResponse().getBody().toString())) {
                            String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                            UiUtils.showSnackbarToast(getView(), json.substring(json.indexOf(":") + 2, json.length() - 2));
                        } else
                            UiUtils.showSnackbarToast(getView(), "Some Problem Occurred");
                    }
                }
        );
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_MAX_OFF_PATH = 200;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {
            try {
                float diffAbs = Math.abs(e1.getY() - e2.getY());
                float diff = e1.getX() - e2.getX();

                if (diffAbs > SWIPE_MAX_OFF_PATH)
                    return false;

                // Left swipe
                if (diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    ((PostActivity) getCurrActivity()).onLeftSwipe();

                    // Right swipe
                } else if (-diff > SWIPE_MIN_DISTANCE
                        && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    ((PostActivity) getCurrActivity()).onRightSwipe();
                }
            } catch (Exception e) {
                Log.e("YourActivity", "Error on gestures");
            }
            return false;
        }
    }

    public void setProfilePic()
    {
        HeldService.getService().searchUser(mPrefernce.readPreference(getString(R.string.API_session_token)),
                mPrefernce.readPreference(getString(R.string.API_user_regId)), new Callback<SearchUserResponse>() {
                    @Override
                    public void success(SearchUserResponse searchUserResponse, Response response) {
                      // Log.i("PostFragment", "@@Image Url" + searchUserResponse.getProfilePic());
                        PicassoCache.getPicassoInstance(getCurrActivity())
                                .load(AppConstants.BASE_URL + searchUserResponse.getUser().getProfilePic())
                                .placeholder(R.drawable.user_icon)
                                .into(mUserImg);
                        muserProfileUrl=searchUserResponse.getUser().getProfilePic();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

    }
    private void launchFeedScreen() {
        Intent intent = new Intent(getCurrActivity(), FeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isProfile", true);
        startActivity(intent);
    }
    private void launchProfileScreen() {
        Intent intent = new Intent(getCurrActivity(), FeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("isProfile", true);
        startActivity(intent);
    }

    public void setTypeFace(TextView tv,String type){
        Typeface medium = Typeface.createFromAsset(getCurrActivity().getAssets(), "BentonSansMedium.otf");
        Typeface book = Typeface.createFromAsset(getCurrActivity().getAssets(), "BentonSansBook.otf");
        if(type=="book"){
            tv.setTypeface(book);

        }else {
            tv.setTypeface(medium);
        }
    }
    }
