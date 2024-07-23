package com.example.notex.plant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class PhotoSelectUtil {
    private static final String TAG = "PhotoSelectUtil";
    
        private static int PHOTO_REQUEST_CODE = 1001;
        private static int CAMERA_REQUEST_CODE = 1002;
        private static Uri mCameraUri = null;

        // 从相册选择
        public void clooosePhoto(Activity activity ){
            Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
            intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            activity.startActivityForResult(intentToPickPic, PHOTO_REQUEST_CODE);
        }

        // 调用系统相机
        public void takePhoto(Activity activity) {
//            var mCameraUri:Uri? = null
            String mCameraImagePath = null;
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 判断是否有相机
            if (captureIntent.resolveActivity(activity.getPackageManager()) != null) {
                Log.i(TAG, "takePhoto: 有相机");
                File photoFile = null;
                Uri photoUri = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // 适配android 10
                    photoUri = createImageUri(activity);
                } else {
                    photoFile = createImageFile(activity);
                    if (photoFile != null) {
                        mCameraImagePath = photoFile.getAbsolutePath();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                            photoUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", photoFile);
                        } else {
                            photoUri = Uri.fromFile(photoFile);
                        }
                    }
                }
                mCameraUri = photoUri;
                if (photoUri != null) {
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    try{
                        activity.startActivityForResult(captureIntent, CAMERA_REQUEST_CODE);
                    }catch (Exception ex){
                        Log.i(TAG, "takePhoto: 启动拍照失败"+ex.toString());
                    }
                    
                }
            }else{
                Log.i(TAG, "takePhoto: 没有相机");
            }
        }

        /**
         * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
         */
        private Uri createImageUri(Activity activity) {
            return FileProvider.getUriForFile(activity,activity.getPackageName() + ".fileprovider", saveFileName(activity));
//            String status = Environment.getExternalStorageState();
//        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
//         if (status == Environment.MEDIA_MOUNTED) {
//            return activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
//        } else {
//            return activity.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
//        }

        }

    /**
     * 保存照片路径
     * @return
     */
    private File saveFileName(Context context){
        File newFolder = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.i("PhotoSelectActivity", "saveFileName: "+newFolder);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String name =format.format(date)+".jpg" ;

        File ji=null;
        try {
            ji=new File(newFolder+"/"+name);
            ji.createNewFile();
//            currentPath = ji.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ji;
    }

        /**
         * 创建保存图片的文件
         */

        private File createImageFile(Activity activity) {
            String imageName = ""+System.currentTimeMillis()+".jpg";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File tempFile = new File(storageDir, imageName);
        return /*if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
                null
            } else */tempFile;
        }


        // 获取回调的图片路径
        public String getActivityResult(Activity activity,int requestCode, int resultCode,  Intent data){
        if(requestCode == CAMERA_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                return FileUtils.getPathFromUri(activity,mCameraUri);
            }
        }
        else if(requestCode == PHOTO_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                return FileUtils.getPathFromUri(activity,data.getData());
            }
        }
        return null;
        }
    



}