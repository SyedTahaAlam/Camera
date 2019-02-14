package com.example.syedtahaalam.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.media.Image;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;

public class cameraActivity extends AppCompatActivity {
    private static final int PICK_IMAGES = 6069;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1001;
    //    private CameraView cameraView;
    CircleButton btn_take_photo;
    


    private int count = 0;
    private int cameraPiccounter = 0;
    private static final String TAG = "CameraActivity";
    List<Image> images;

    private FrameLayout camera_Preview_Frame;
    private Camera camera;
    private CameraPreview cameraPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_camera);
        initilizeViews();

        if (ContextCompat.checkSelfPermission(cameraActivity.this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initCamera();
        } else {
            askForCameraPermission();
            Toast.makeText(this, "Please Allow app to Use Camera of your Device. Thanks", Toast.LENGTH_SHORT).show();
        }
    }

    private void askForCameraPermission() {

        //if permission is already being asked and denied
        if (ActivityCompat.shouldShowRequestPermissionRationale(cameraActivity.this, Manifest.permission.CAMERA)) {
            Snackbar.make(findViewById(android.R.id.content), "Need permission for loading data", Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //asking all required permissions
                            ActivityCompat.requestPermissions(cameraActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_CAMERA);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions(cameraActivity.this, new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
    }

    private boolean checkCameraHardware() {


        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }
    private boolean storeImage(Bitmap imageData) {
        //get path to external storage (SD card)
        String iconsStoragePath = Environment.getExternalStorageDirectory() + "/myAppDir/myImages/";
        File sdIconStorageDir = new File(iconsStoragePath);

        //create storage directories, if they don't exist
        sdIconStorageDir.mkdirs();

        try {
//            String filePath = sdIconStorageDir.toString() + Calendar.getInstance().getTimeInMillis();
            String filePath = sdIconStorageDir.toString() +Calendar.getInstance().getTimeInMillis()+ ".jpg";

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            //choose another format if PNG doesn't suit you
            imageData.compress(Bitmap.CompressFormat.JPEG, 100, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        } catch (IOException e) {
            Log.w("TAG", "Error saving image file: " + e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e("TAG", "onRestart: onRestart");
        if (ContextCompat.checkSelfPermission(cameraActivity.this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initCamera();
        } else {
            askForCameraPermission();
            Toast.makeText(this, "Please Allow app to Use Camera of your Device. Thanks", Toast.LENGTH_SHORT).show();
        }
    }


    //custom camra start
    private void initCamera() {
        if (checkCameraHardware()) {
            camera = getCameraInstance();
            cameraPreview = new CameraPreview(this, camera);

//            CameraPreview.setCameraDisplayOrientation(cameraActivity.this,camera);
            camera_Preview_Frame.addView(cameraPreview);
            setFocus();

        } else {
            Toast.makeText(getApplicationContext(), "Device not support camera feature", Toast.LENGTH_SHORT).show();
        }
    }

    public void setFocus() {
        Camera.Parameters params = camera.getParameters();
        if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        }
        camera.setParameters(params);
    }

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            try {
                String iconsStoragePath = getApplication().getFilesDir() + "/myAppDir/myImages/";
                File sdIconStorageDir = new File(iconsStoragePath);

                //create storage directories, if they don't exist
                sdIconStorageDir.mkdirs();
                try {
//            String filePath = sdIconStorageDir.toString() + Calendar.getInstance().getTimeInMillis();
                    String filePath = sdIconStorageDir.toString() +Calendar.getInstance().getTimeInMillis()+ ".jpg";

                    FileOutputStream fileOutputStream = new FileOutputStream(filePath);

                   fileOutputStream.write(data);
                   fileOutputStream.flush();
                   fileOutputStream.close();
                   camera.startPreview();

                } catch (FileNotFoundException e) {
                    Log.w("TAG", "Error saving image file: " + e.getMessage());

                } catch (IOException e) {
                    Log.w("TAG", "Error saving image file: " + e.getMessage());

                }

//                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//                camera.startPreview();
//                storeImage(decoded);

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                camera.startPreview();

            }

        }
    };

    Camera.ShutterCallback myShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // TODO Auto-generated method stub
        }
    };

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception e) {
            Log.e("TAG", "getCameraInstance: No Camera Found");
        }
        return c;
    }

    //end here

    private void initilizeViews() {

        btn_take_photo = findViewById(R.id.capture_button);

        camera_Preview_Frame = findViewById(R.id.camera);



        btn_take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    camera.takePicture(myShutterCallback, null, pictureCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });

    }




    @Override
    protected void onResume() {
        super.onResume();
//        cameraView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        cameraView.stop();
        if (camera != null) {
            camera_Preview_Frame.removeAllViews();
            camera.release();
            camera = null;
            Log.e("TAG", "onPause: onPause");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCamera();
                }
            }
        }
    }
}
