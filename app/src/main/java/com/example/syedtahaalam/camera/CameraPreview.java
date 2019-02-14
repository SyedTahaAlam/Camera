package com.example.syedtahaalam.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;

/**
 * Created by Syed Taha Alam on 7/19/2018.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private Camera camera;
    private SurfaceHolder mSurfaceHolder;
    private Context context;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.context = context;

        this.camera = camera;
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (camera == null) {
            return;
        }

        try {
            camera.setPreviewDisplay(holder);
            params(camera);
            camera.startPreview();
        } catch (IOException e) {
            Log.e("tag", "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {
            Log.e("tag", "Error setting camera stop: " + e.getMessage());
        }

        try {
            camera.setPreviewDisplay(holder);
            params(camera);
            camera.startPreview();
        } catch (IOException e) {
            Log.e("tag", "Error setting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void params(Camera camera) {

        Camera.Parameters parameters = camera.getParameters();

        parameters.setFlashMode(parameters.FLASH_MODE_AUTO);
        parameters.setJpegQuality(100);
        parameters.setRotation(270);
        camera.setDisplayOrientation(180);
//        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        if (display.getRotation() == Surface.ROTATION_0) {
//            camera.setDisplayOrientation(display.getRotation());
//            Log.e("rotation","is "+display.getRotation()+"         458");
//        } else if (display.getRotation() == Surface.ROTATION_270) {
//            Log.e("rotation","is "+display.getRotation()+"         41111");
//
//            camera.setDisplayOrientation(display.getRotation());
//        }
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(1, info);
        int rotation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()

                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i).width > size.width)
                size = sizes.get(i);
        }
        parameters.setPictureSize(size.width, size.height);
        camera.setParameters(parameters);

    }

}