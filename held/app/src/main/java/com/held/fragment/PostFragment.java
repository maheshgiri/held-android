package com.held.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.held.activity.PostActivity;
import com.held.activity.R;
import com.held.retrofit.HeldService;
import com.held.retrofit.response.PostResponse;
import com.held.retrofit.response.ProfilPicUpdateResponse;
import com.held.utils.AppConstants;
import com.held.utils.DialogUtils;
import com.held.utils.PreferenceHelper;
import com.held.utils.UiUtils;
import com.held.utils.Utils;

import java.io.File;
import java.io.IOException;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

public class PostFragment extends ParentFragment {

    public static final String TAG = PostFragment.class.getSimpleName();
    private ImageView mPostImg, mUserImg, mImageToUpload;
    private TextView mPostTxt, mUserNameTxt, mCancelTxt, mOkTxt, mTimeTxt;
    private RelativeLayout mPostLayout;
    private RelativeLayout mBoxLayout;
    private String sourceFileName, mCaption;
    private File mFile;
    private EditText mCaptionEdt;
    private GestureDetector mGestureDetector;

    public static PostFragment newInstance() {
        return new PostFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }

    @Override
    protected void initialiseView(View view, Bundle savedInstanceState) {
        mUserImg = (ImageView) view.findViewById(R.id.profile_img);
        mUserNameTxt = (TextView) view.findViewById(R.id.user_name);
        mPostTxt = (TextView) view.findViewById(R.id.user_post_txt);
        mPostImg = (ImageView) view.findViewById(R.id.user_post_image);
        mBoxLayout = (RelativeLayout) view.findViewById(R.id.BOX_layout);

        mCaptionEdt = (EditText) view.findViewById(R.id.post_edit_text);
        mCaptionEdt.setVisibility(View.VISIBLE);
        mCancelTxt = (TextView) view.findViewById(R.id.POST_cancel);
        mOkTxt = (TextView) view.findViewById(R.id.POST_ok);
        getCurrActivity().getToolbar().findViewById(R.id.TOOLBAR_retake_btn).setOnClickListener(this);
        getCurrActivity().getToolbar().findViewById(R.id.TOOLBAR_post_btn).setOnClickListener(this);
        mUserNameTxt.setText(PreferenceHelper.getInstance(getCurrActivity()).readPreference("USER_NAME"));
        mTimeTxt = (TextView) view.findViewById(R.id.BOX_time_txt);
        mTimeTxt.setText("Click here to upload Image");
        mTimeTxt.setVisibility(View.GONE);
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
    public void onClicked(View v) {
        switch (v.getId()) {
            case R.id.BOX_main_img:
//                openImageIntent();
                break;
            case R.id.TOOLBAR_retake_btn:
                openImageIntent();
                break;
            case R.id.TOOLBAR_post_btn:
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
                ((PostActivity) getCurrActivity()).isPostVisible = false;
                getCurrActivity().getToolbar().setVisibility(View.VISIBLE);
                mBoxLayout.setVisibility(View.VISIBLE);
                mPostLayout.setVisibility(View.GONE);
                break;
            case R.id.POST_ok:
                Utils.hideSoftKeyboard(getCurrActivity());
                mTimeTxt.setVisibility(View.INVISIBLE);
                updateBoxUI();
                break;
        }
    }

    private void updateBoxUI() {
        ((PostActivity) getCurrActivity()).isPostVisible = false;
        getCurrActivity().getToolbar().setVisibility(View.VISIBLE);
        mBoxLayout.setVisibility(View.VISIBLE);
        mPostLayout.setVisibility(View.GONE);
        mCaption = mCaptionEdt.getText().toString().trim();

        BitmapFactory.Options options = new BitmapFactory.Options();
        UiUtils.calculateInSampleSize(options,
                UiUtils.dpToPx(getResources(), 350),
                UiUtils.dpToPx(getResources(), 350));
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        Bitmap mAttachment = BitmapFactory.decodeFile(mFile.getAbsolutePath(),
                options);

        mPostImg.setImageBitmap(mAttachment);
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

                                startActivityForResult(getCameraImage,
                                        AppConstants.REQUEST_CAMEAR);

                                break;

                            case 1:
                                Intent intent;
                                intent = new Intent(
                                        Intent.ACTION_PICK,
                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                Intent chooser = Intent.createChooser(intent,
                                        "Choose a Picture");
                                startActivityForResult(chooser,
                                        AppConstants.REQUEST_GALLERY);

                                break;

                            default:
                                break;
                        }
                    }
                });

        builder.show();

    }

    private void doCrop(Uri mCurrentPhotoPath) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(mCurrentPhotoPath, "image/*");
        if (!PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_is_first_post), false)) {
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

        startActivityForResult(cropIntent, AppConstants.REQUEST_CROP);
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
//                    mFile = new File(photoUri.getPath());
//                    updateBoxUI();
                    break;

                case AppConstants.REQUEST_GALLERY:
                    Uri PhotoURI = data.getData();
                    doCrop(PhotoURI);
//                    mFile = new File(PhotoURI.getPath());
//                    updateBoxUI();
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

            updateBoxUI();
//            updateUI();
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
        ((PostActivity) getCurrActivity()).isPostVisible = true;
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
        if (true) {
            mCaptionEdt.setVisibility(View.VISIBLE);
        } else {
            mCaptionEdt.setVisibility(View.GONE);
        }
    }

    private void callPostDataApi() {
        Log.i("PostFrgament","Sesson"+PreferenceHelper.getInstance(getCurrActivity()).readPreference("SESSION_TOKEN"));

        HeldService.getService().uploadFile(PreferenceHelper.getInstance(getCurrActivity()).readPreference("SESSION_TOKEN"),mCaptionEdt.getText().toString().trim(), new TypedFile("multipart/form-data", mFile),"", new Callback<PostResponse>() {
            @Override
            public void success(PostResponse postResponse, Response response) {
                DialogUtils.stopProgressDialog();

                callPicUpdateApi(postResponse.getImage());
                callThumbnailUpdateApi(postResponse.getImage());
               /* if (!PreferenceHelper.getInstance(getCurrActivity()).readPreference("isFirstPostCreated", false)) {

                }*/
                PreferenceHelper.getInstance(getCurrActivity()).writePreference("isFirstPostCreated", true);
                getCurrActivity().perform(1, null);
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

    private void callThumbnailUpdateApi(Image image) {
        HeldService.getService().updateProfilePic(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_registration_key)),"thumbnail", image, new Callback<ProfilPicUpdateResponse>() {
                    @Override
                    public void success(ProfilPicUpdateResponse profilPicUpdateResponse, Response response) {
                        DialogUtils.stopProgressDialog();
                        UiUtils.showSnackbarToast(getView(), "Profile Pic Updated Successfully..");
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

    private void callPicUpdateApi(Image image) {
        HeldService.getService().updateProfilePic(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_registration_key)),"pic", image, new Callback<ProfilPicUpdateResponse>() {
                    @Override
                    public void success(ProfilPicUpdateResponse profilPicUpdateResponse, Response response) {
                        DialogUtils.stopProgressDialog();
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
}
