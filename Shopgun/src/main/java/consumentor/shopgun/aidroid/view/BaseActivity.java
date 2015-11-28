package consumentor.shopgun.aidroid.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import consumentor.shopgun.aidroid.view.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * Created by Simon on 2013-10-11.
 */
public abstract class BaseActivity extends FragmentActivity {

    public static final int SCAN_REQUEST = 0x1;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        ImageView view = (ImageView)findViewById(android.R.id.home);
        view.setPadding(5, 0, 5, 0);

        updateLoaderIndicator();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:{
                onSearchRequested();
                return true;
            }
            case R.id.action_scan:{
                Intent intent = new Intent(this, BatchScanActivity.class);
                //startActivityForResult(intent, SCAN_REQUEST);
                startActivity(intent);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case SCAN_REQUEST:{
                if (resultCode == ScanActivity.RESULT_SCAN_SUCCEEDED){

                    Intent productViewIntent = new Intent(this, ProductActivity.class);
                    productViewIntent.putExtra(ScanActivity.EXTRA_BARCODE, data.getStringExtra(ScanActivity.EXTRA_BARCODE));
                    startActivity(productViewIntent);
                }
                break;
            }
        }

    }

    public void updateLoaderIndicator() {

        if (getSupportLoaderManager().hasRunningLoaders()) {
            setProgressBarIndeterminateVisibility(Boolean.TRUE);
        } else {
            setProgressBarIndeterminateVisibility(Boolean.FALSE);
        }
    }

    public void showLoaderIndicator(boolean show){
        setProgressBarIndeterminateVisibility(show);
    }
}
