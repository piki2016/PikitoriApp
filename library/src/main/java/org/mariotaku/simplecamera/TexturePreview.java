package org.mariotaku.simplecamera;

import android.annotation.TargetApi;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.TextureView;

import java.io.IOException;

/**
 * Created by mariotaku on 14-9-9.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TexturePreview implements Preview, TextureView.SurfaceTextureListener {

    private final CameraView mCameraView;
    private final TextureView mTextureView;
    private boolean mAttachedToCamera;

    public TexturePreview(CameraView cameraView) {
        mCameraView = cameraView;
        mTextureView = new TextureView(cameraView.getContext());
        mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public TextureView getView() {
        return mTextureView;
    }

    @Override
    public void layoutPreview(boolean changed, int l, int t, int r, int b) {
        final int measuredWidth = mTextureView.getMeasuredWidth();
        final int measuredHeight = mTextureView.getMeasuredHeight();
        mTextureView.layout(0, 0, measuredWidth, measuredHeight);
        notifyPreviewSizeChanged(measuredWidth, measuredHeight);
    }

    @Override
    public boolean isAddedToCameraView() {
        return mTextureView.getParent() == mCameraView;
    }

    @Override
    public boolean isAttachedToCamera() {
        return mAttachedToCamera;
    }

    @Override
    public void onPreReleaseCamera(Camera camera) {
        mCameraView.setCameraPreviewStarted(false);
        camera.stopPreview();
        try {
            mAttachedToCamera = false;
            camera.setPreviewTexture(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void attachMediaRecorder(MediaRecorder recorder) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            throw new UnsupportedOperationException();
    }


    @Override
    public void detachMediaRecorder(MediaRecorder recorder) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
            throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldSetSizeForRecorder() {
        return true;
    }

    @Override
    public void notifyPreviewSizeChanged(int width, int height) {
        final Camera camera = mCameraView.getOpeningCamera();
        if (width != 0 && height != 0) {
            updateSurface(camera, width, height);
            return;
        }
        final int viewWidth = mTextureView.getWidth(), viewHeight = mTextureView.getHeight();
        if (viewWidth != 0 && viewHeight != 0) {
            updateSurface(camera, viewWidth, viewHeight);
        } else {
            final int measuredWidth = mTextureView.getMeasuredWidth();
            final int measuredHeight = mTextureView.getMeasuredHeight();
            updateSurface(camera, measuredWidth, measuredHeight);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        final Camera camera = mCameraView.openCameraIfNeeded();
        if (camera == null) return;
        try {
            final Camera.Parameters parameters = camera.getParameters();
            camera.setParameters(parameters);
            camera.setPreviewTexture(surface);
            mAttachedToCamera = true;
            updateSurface(camera, width, height);
            camera.startPreview();
            mCameraView.setCameraPreviewStarted(true);
            mCameraView.requestLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        notifyPreviewSizeChanged(0, 0);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCameraView.releaseCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void updateSurface(final Camera camera, final int width, final int height) {
        if (camera == null || width == 0 || height == 0) return;
        final Camera.Size size = camera.getParameters().getPreviewSize();
        final int rotation = mCameraView.getCameraRotation();
        final boolean isPortrait = (rotation % 180) != 0;
        final int cameraWidth = isPortrait ? size.height : size.width;
        final int cameraHeight = isPortrait ? size.width : size.height;
        final float viewRatio = (float) width / height, cameraRatio = (float) cameraWidth / cameraHeight;
        final Matrix transform = new Matrix();
        if (viewRatio > cameraRatio) {
            // fit width
            transform.setScale(1, width / cameraRatio / height);
        } else {
            // fit height
            transform.setScale(height * cameraRatio / width, 1);
        }
        final float translateX, translateY;
        if (viewRatio > cameraRatio) {
            translateX = 0;
            translateY = -(width / cameraRatio - height) / 2;
        } else {
            translateX = -(height * cameraRatio - width) / 2;
            translateY = 0;
        }
        transform.postTranslate(translateX, translateY);
        mTextureView.setTransform(transform);
    }

}
