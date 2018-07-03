package io.incepted.cryptoaddresstracker.Activities;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.incepted.cryptoaddresstracker.R;
import io.incepted.cryptoaddresstracker.Utils.ViewModelFactory;
import io.incepted.cryptoaddresstracker.ViewModels.QRScanViewModel;
import io.incepted.cryptoaddresstracker.databinding.ActivityQrscanBinding;

public class QRScanActivity extends AppCompatActivity implements SurfaceHolder.Callback, Detector.Processor<Barcode> {

    private static final String TAG = QRScanViewModel.class.getSimpleName();

    @BindView(R.id.qr_scan_camera_preview)
    SurfaceView mCameraPreview;

    CameraSource mCameraSource;
    BarcodeDetector mBarcodeDetector;
    final int mRequestCameraPermissionId = 1001;


    private List<Camera.Size> mSupportedSizes;
    private Camera.Size optimalPreviewSize;

    private boolean cameraInitialized = false;

    private QRScanViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityQrscanBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_qrscan);
        mViewModel = obtainViewModel(this);
        binding.setViewmodel(mViewModel);

        ButterKnife.bind(this);
        int screenWidth = (int) getResources().getDisplayMetrics().widthPixels;
        int screenHeight = (int) getResources().getDisplayMetrics().heightPixels;


        getSupportedSizes();
        optimalPreviewSize = getOptimalPreviewSize(mSupportedSizes, screenWidth, screenHeight);
        initBarcodeDetector();
        initCameraSource();
        initCameraPreview();
        setBarcodeDetectorProcessor();
    }

    private QRScanViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = ViewModelFactory.getInstance(activity.getApplication());
        return ViewModelProviders.of(activity, factory).get(QRScanViewModel.class);
    }

    private void getSupportedSizes() {
        Camera c = Camera.open();
        mSupportedSizes = c.getParameters().getSupportedPreviewSizes();
        c.release();
    }

    private void initBarcodeDetector() {
        mBarcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE)
                .build();
    }

    private void initCameraSource() {

        mCameraSource = new CameraSource.Builder(this, mBarcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height)
                .setAutoFocusEnabled(true)
                .build();
    }

    private void initCameraPreview() {
        mCameraPreview.getHolder().addCallback(this);
    }

    private void setBarcodeDetectorProcessor() {
        mBarcodeDetector.setProcessor(this);
    }


    // -------------------------------- Callbacks -----------------------------

    // ------------ Surface holder callbacks -----------------

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(QRScanActivity.this,
                    new String[]{Manifest.permission.CAMERA}, mRequestCameraPermissionId);
            return;
        }
        try {
            mCameraSource.start(mCameraPreview.getHolder());
            cameraInitialized = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (cameraInitialized) {
            return;
        }
        cameraSetup(width, height);
        cameraInitialized = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCameraSource.stop();
    }


    // ------------ Barcode processor callbacks -----------------

    @Override
    public void release() {

    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        final SparseArray<Barcode> qrcodes = detections.getDetectedItems();
        if (qrcodes.size() != 0) {
            Log.d(TAG, "receiveDetections: " + qrcodes.valueAt(0).displayValue);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case mRequestCameraPermissionId:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        finish();
                        return;
                    }
                    try {
                        mCameraSource.start(mCameraPreview.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.d(TAG, "getOptimalPreviewSize: " + optimalSize.width + "*" + optimalSize.height + "\n" + w+"*"+h);
        return optimalSize;
    }

    private void cameraSetup(int w, int h) {
        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) mCameraPreview.getLayoutParams();
        double cameraAspectRatio = ((double) optimalPreviewSize.width) / optimalPreviewSize.height;

        Log.d(TAG, "cameraSetup: " + lp.width + "*" + lp.height);

        if (((double) h) / w > cameraAspectRatio) {
            lp.width = (int) (h/cameraAspectRatio + 0.5);
            lp.height = h;
        } else {
            lp.height = (int) (w*cameraAspectRatio + 0.5);
            lp.width = w;
            lp.topMargin = (h - lp.height) / 2;
        }

        Log.d(TAG, "cameraSetup: " + lp.width + "*" + lp.height);

        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;

        mCameraPreview.setLayoutParams(lp);
        mCameraPreview.requestLayout();

    }


}
