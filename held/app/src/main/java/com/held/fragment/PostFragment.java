package com.held.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
        mUserImg = (ImageView) view.findViewById(R.id.BOX_profile_img);
        mUserNameTxt = (TextView) view.findViewById(R.id.BOX_user_name_txt);
        mPostTxt = (TextView) view.findViewById(R.id.BOX_des_txt);
        mPostImg = (ImageView) view.findViewById(R.id.BOX_main_img);
        mBoxLayout = (RelativeLayout) view.findViewById(R.id.BOX_layout);
        mPostLayout = (RelativeLayout) view.findViewById(R.id.POST_post_data);
        mImageToUpload = (ImageView) view.findViewById(R.id.POST_image);
        mCaptionEdt = (EditText) view.findViewById(R.id.BOX_des_edt);
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
//        cropIntent.putExtra("crop", "true");
//        cropIntent.putExtra("aspectX", 1);
//        cropIntent.putExtra("aspectY", 1);
//        cropIntent.putExtra("outputX", 320);
//        cropIntent.putExtra("outputY", 320);

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
                    mFile = new File(photoUri.getPath());
                    updateBoxUI();
                    break;

                case AppConstants.REQUEST_GALLERY:
                    Uri PhotoURI = data.getData();
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

            updateUI();
        }

    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getCurrActivity().getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
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

        HeldService.getService().uploadFile(mCaptionEdt.getText().toString().trim(), new TypedFile("multipart/form-data", mFile), PreferenceHelper.getInstance(getCurrActivity()).readPreference("SESSION_TOKEN"), new Callback<PostResponse>() {
            @Override
            public void success(PostResponse postResponse, Response response) {
                DialogUtils.stopProgressDialog();
                if (!PreferenceHelper.getInstance(getCurrActivity()).readPreference("isFirstPostCreated", false)) {
                    DialogUtils.stopProgressDialog();
                    callPicUpdateApi(postResponse.getImage());
                }
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

    private void callPicUpdateApi(String image) {
        HeldService.getService().updateProfilePic(PreferenceHelper.getInstance(getCurrActivity()).readPreference(getString(R.string.API_session_token)),
                "pic", image, new Callback<ProfilPicUpdateResponse>() {
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

}
