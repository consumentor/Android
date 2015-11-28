package consumentor.shopgun.aidroid.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.analytics.tracking.android.EasyTracker;
import com.mirasense.scanditsdk.ScanditSDKAutoAdjustingBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;

/**
 * Created by Simon on 28.08.13.
 */
public class ScanActivity extends Activity implements ScanditSDKListener {

    public static final int RESULT_SCAN_FAILED = 0x0;
    public static final int RESULT_SCAN_SUCCEEDED = 0x1;
    public static final String EXTRA_BARCODE = "org.consumentor.shopgun.extra.BARCODE";
    private static String SCANDIT_SDK_KEY = "e5Y91A/JEeORlTrQtqMWUp0IkkKgCdohxzSdtejmNog";
    private ScanditSDKAutoAdjustingBarcodePicker mPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPicker = new ScanditSDKAutoAdjustingBarcodePicker(this, SCANDIT_SDK_KEY, ScanditSDKAutoAdjustingBarcodePicker.CAMERA_FACING_BACK);
        mPicker.set1DScanningEnabled(true);
        mPicker.setEan13AndUpc12Enabled(true);
        mPicker.setEan8Enabled(true);
        mPicker.getOverlayView().addListener(this);
        setContentView(mPicker);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPicker != null ){
            mPicker.startScanning();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPicker != null ){
            mPicker.stopScanning();
        }
    }

    @Override
    public void didCancel() {
        setResult(RESULT_SCAN_FAILED);
        finish();
    }

    @Override
    public void didScanBarcode(String barcode, String symbology) {
        Intent data = new Intent();
        data.putExtra(EXTRA_BARCODE, barcode);
        setResult(RESULT_SCAN_SUCCEEDED, data);
        finish();
    }

    @Override
    public void didManualSearch(String s) {

    }
}
