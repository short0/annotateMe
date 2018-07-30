//package com.example.user.mobilemicroscopy;
//
//
//import android.content.ComponentName;
//import android.content.Intent;
//import android.content.pm.ResolveInfo;
//import android.net.Uri;
//import android.widget.ImageView;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//
//import java.util.List;
//
//
//public class CropActivity extends AppCompatActivity  {
//
//    ImageView imageView;
//    Uri mImageUri;
//    private static final int PICK_FROM_FILE = 3;
//
//
//
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        imageView = (ImageView) findViewById(R.id.imageView);
//
//    }
//
//
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
////        if (resultCode != RESULT_OK) return;
////
//        switch (requestCode)
//        {
//            case PICK_FROM_FILE:
//                mImageUri = data.getData();
//                imageCrop();
//                break;
//        }
//    }
//
//
//    private void imageCrop()
//    {
//
//
//        Intent intent = new Intent("com.android.camera.action.CROP");
//
//        //       intent.setDataAndType(mImageCaptureUri,"image/*");
//        intent.setType("image/*");
//
//        List<ResolveInfo> list=getPackageManager().queryIntentActivities(intent,0);
//
//
//
//
//        intent.setData(mImageUri);
//
//
//        //
//        intent.putExtra("outputX",3000);   //px of image, higher value = better quality photo
//        intent.putExtra("outputY",3000);   //px of image
//        intent.putExtra("aspectX",1);
//        intent.putExtra("aspectY",1);
//        intent.putExtra("scale",true);
//        intent.putExtra("return-data",true);
////
////        Intent i=new Intent(intent);
//        ResolveInfo res = list.get(0);
////
//        intent.setComponent(new ComponentName(res.activityInfo.packageName,res.activityInfo.name));
//
//        startActivityForResult(intent,2);
//
//    }
//
//
//
//}
