package com.security.lib.customview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.privacy.lock.R;
import com.security.manager.meta.Pref;
import com.security.manager.IntruderApi;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by song on 15/8/18.
 */
public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    Camera camera;

    public MySurfaceView(Context context) {
        super(context);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MySurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    int shutterSoundId;
    SoundPool soundPool;

    private void init() {
        getHolder().addCallback(this);
    }

    public void catchIntruder(final String pkgName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (startCamera()) {
                    takePhoto(pkgName);
                }
            }
        }).start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (soundPool == null) {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
            shutterSoundId = soundPool.load(getContext(), R.raw.shutter, 0);
        }
    }

    boolean startCamera() {
        if (!Pref.fetchIntruder()) return false;
        try {
            int cameraCount = Camera.getNumberOfCameras();
            if (cameraCount > 1) {
                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            } else {
                camera = Camera.open();
            }
            return true;
        } catch (Exception | Error e) {
            e.printStackTrace();
            return false;
        }
    }

    void takePhoto(final String pkgName) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            Camera.Size size = getOptimalPreviewSize(sizes, 240, 320);
            Camera.Size optimalSize = getOptimalPictureSize(camera, size);
            parameters.setPictureSize(optimalSize.width, optimalSize.height);
            camera.setParameters(parameters);
            camera.setPreviewDisplay(getHolder());
            camera.startPreview();
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (camera == null) {
                        return;
                    }
                    camera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(final byte[] data, final Camera cam) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (soundPool != null) {
                                            soundPool.play(shutterSoundId, 1, 1, 1, 0, 1);
                                        }
                                        Camera.Parameters parameters = cam.getParameters();
                                        int width = parameters.getPreviewSize().width;
                                        int height = parameters.getPreviewSize().height;

                                        YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);
                                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                                        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);

                                        byte[] bytes = out.toByteArray();
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        bitmap = rotate(bitmap, -90);

                                        if (IntruderApi.addIntruder(bitmap, pkgName)) {
                                            Pref.setHasIntruder(true);
                                        }

                                    } catch (Exception ignore) {
                                        ignore.printStackTrace();
                                    } finally {
                                        release();
                                    }
                                }
                            }).start();
                        }
                    });
                }
            }, 1000);
        } catch (Exception | Error e) {
            e.printStackTrace();
            release();
        }
    }

    public synchronized void release() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
        bitmap.recycle();
        return bmp;
    }

    private Camera.Size getOptimalPictureSize(Camera mCamera, Camera.Size mPreviewSize) {
        if (mCamera == null)
            return null;

        List<Camera.Size> cameraSizes = mCamera.getParameters()
                .getSupportedPictureSizes();
        Camera.Size optimalSize = mCamera.new Size(0, 0);
        double previewRatio = (double) mPreviewSize.width / mPreviewSize.height;

        for (Camera.Size size : cameraSizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - previewRatio) > 0.01f)
                continue;
            if (size.height > optimalSize.height) {
                optimalSize = size;
            }
        }

        if (optimalSize.height == 0) {
            for (Camera.Size size : cameraSizes) {
                if (size.height > optimalSize.height) {
                    optimalSize = size;
                }
            }
        }
        return optimalSize;
    }

    protected Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {

        final double ASPECT_TOLERANCE = 0.01;
        final double targetRatio = (double) 4 / 3d;
        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;

        // Try to find an size match aspect ratio and size
        double ratio;
        Camera.Size size;
        for (int i = 0; i < sizes.size(); i++) {
            size = sizes.get(i);
            ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (int i = 0; i < sizes.size(); i++) {
                size = sizes.get(i);
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            if (soundPool != null) {
                soundPool.unload(shutterSoundId);
                soundPool.release();
                soundPool = null;
            }
            release();
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }
}