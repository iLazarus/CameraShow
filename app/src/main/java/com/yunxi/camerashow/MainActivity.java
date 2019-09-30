package com.yunxi.camerashow;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private Context mContext;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;

    private Camera mCamera = null;
    private int mCameraId = 0;
    private Button mFocusButton, mRotationButton, mTakeButton, mAutoTakeButton;
    private int ROTATION = 90;
    private boolean isValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        initView();

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
    }

    private void initView() {
        mSurfaceView = findViewById(R.id.surfaceView);
        mFocusButton = findViewById(R.id.focus);
        mRotationButton = findViewById(R.id.rotation);
        mTakeButton = findViewById(R.id.take);
        mAutoTakeButton = findViewById(R.id.autotake);

        mFocusButton.setOnClickListener(this);
        mRotationButton.setOnClickListener(this);
        mTakeButton.setOnClickListener(this);
        mAutoTakeButton.setOnClickListener(this);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSurfaceHolder.removeCallback(this);
        mSurfaceHolder = null;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void OpenCamera() {
        CloseCamera();
        if (mCamera == null) {
            if (Camera.getNumberOfCameras() < 1) {
                Log.d("Camera", "OpenCamera: No Camera found");
                return;
            }
            mCamera = Camera.open(mCameraId);
            mCamera.getParameters().setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            mCamera.getParameters().setPictureSize(2560, 1920);
            mCamera.setDisplayOrientation(ROTATION);
            mCamera.setPreviewCallback(null);
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                Log.d("Camera", "OpenCamera: setPreviewDisplay() failed");
                return;
            }
            mCamera.startPreview();
        }
        isValid = true;
        Log.d("Camera", "OpenCamera: Camera Opened");
    }

    private void CloseCamera() {
        isValid = false;
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
        }
        mCamera = null;
        Log.d("Camera", "OpenCamera: Camera Closed");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 6.0 之后的权限
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        OpenCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CloseCamera();
    }

    @Override
    public void onClick(View v) {
        if (!isValid) {
            Log.d("Camera", "onClick: Not ready for take picture");
            return;
        }

        switch (v.getId()) {
            case R.id.focus:
                autoFocus();
                break;
            case R.id.rotation:
                rotationPreview();
                break;
            case R.id.take:
                takePicture();
                break;
            case R.id.autotake:
                takePictureAfterFocused();
                break;
        }
    }

    private void autoFocus() {
        if (mCamera != null) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        Log.d("Camera", "autoFocus: Done!");
                    }
                }
            });
        }
    }

    private void takePicture() {
        if (mCamera != null) {
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    File img = new File(getExternalCacheDir() + File.separator + "123.jpg");
                    FileOutputStream outputStream = null;
                    FileChannel fileChannel = null;
                    ByteBuffer buffer = null;

                    try {
                        outputStream = new FileOutputStream(img);
                        fileChannel = outputStream.getChannel();
                        buffer = ByteBuffer.allocate(data.length);
                        buffer.put(data);
                        buffer.flip();
                        fileChannel.write(buffer);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (buffer != null) {
                                buffer.clear();
                            }
                            if (fileChannel != null) {
                                fileChannel.close();
                            }
                            if (outputStream != null) {
                                outputStream.close();
                            }
                        } catch (IOException e) {

                        }
                        mCamera.startPreview();
                    }

                    Log.d("Camera", "takePicture: done!");
                }
            });
        }
    }

    private void takePictureAfterFocused() {
        if (mCamera != null) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        mCamera.takePicture(null, null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                File img = new File(getExternalCacheDir() + File.separator + "123.jpg");
                                FileOutputStream outputStream = null;
                                FileChannel fileChannel = null;
                                ByteBuffer buffer = null;

                                try {
                                    outputStream = new FileOutputStream(img);
                                    fileChannel = outputStream.getChannel();
                                    buffer = ByteBuffer.allocate(data.length);
                                    buffer.put(data);
                                    buffer.flip();
                                    fileChannel.write(buffer);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        if (buffer != null) {
                                            buffer.clear();
                                        }
                                        if (fileChannel != null) {
                                            fileChannel.close();
                                        }
                                        if (outputStream != null) {
                                            outputStream.close();
                                        }
                                    } catch (IOException e) {

                                    }
                                    mCamera.startPreview();
                                }

                                Log.d("Camera", "takePictureAfterFocused: done!");
                            }
                        });
                    }
                }
            });
        }
    }

    private void rotationPreview() {
        if (mCamera != null) {
            ROTATION += 90;
            mCamera.setDisplayOrientation(ROTATION % 360);
        }
    }

}


